package com.example.toshiba.themovieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.toshiba.themovieapp.data.MovieContract;
import com.example.toshiba.themovieapp.data.MovieDbHelper;
import com.example.toshiba.themovieapp.utilities.JsonUtils;
import com.example.toshiba.themovieapp.utilities.NetworkUtils;

import java.net.URL;


public class MainActivity extends AppCompatActivity implements MovieDbAdapter.ItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<String[][]>{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SORT_BY_POPULAR = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";
    private static final String SORT_BY_FAVORITE = "favorite";
    private static final String BUNDLE_URL_KEY = "key_url";
    private static final int LOADER_NUM = 11;
    private static boolean PREFERENCE_HAS_BEEN_UPDATED = false;
    private String sortBy;

    private RecyclerView recyclerView;
    private TextView textViewErrorMessage;
    private ProgressBar progressBar;
    private MovieDbAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "OnCreate got called");

        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        textViewErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        adapter = new MovieDbAdapter(this);

        int spanCount = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        setupPreferences();
        callMovieLoader(sortBy);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart got called");
        if (PREFERENCE_HAS_BEEN_UPDATED) {
            callMovieLoader(sortBy);
            PREFERENCE_HAS_BEEN_UPDATED = false;
        }
    }

    private void setupPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sharedPreferencesValue = sharedPreferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default));

        switch (sharedPreferencesValue) {
            case SORT_BY_POPULAR:
                sortBy = SORT_BY_POPULAR;
                break;
            case SORT_BY_TOP_RATED:
                sortBy = SORT_BY_TOP_RATED;
                break;
            case SORT_BY_FAVORITE:
                sortBy = SORT_BY_FAVORITE;
        }
    }

    private void callMovieLoader(String sortBy) {
        String url;
        if (!sortBy.equals(SORT_BY_FAVORITE)) {
            url = NetworkUtils.getUrl(sortBy).toString();
        } else {
            url = SORT_BY_FAVORITE;
        }
        Log.d(TAG, "callMovieLoader got called " + url);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_URL_KEY, url);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[][]> loader = loaderManager.getLoader(LOADER_NUM);

        if (loader == null)
            loaderManager.initLoader(LOADER_NUM, bundle, this);
        else
            loaderManager.restartLoader(LOADER_NUM, bundle, this);
    }

    private void showRecyclerView() {
        textViewErrorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        textViewErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String[] movieInformation) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieInformation);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_by_key))) {
            String value = sharedPreferences.getString(key,
                    getString(R.string.pref_sort_by_default));

            switch (value) {
                case SORT_BY_POPULAR:
                    sortBy = SORT_BY_POPULAR;
                    break;
                case SORT_BY_TOP_RATED:
                    sortBy = SORT_BY_TOP_RATED;
                    break;
                case SORT_BY_FAVORITE:
                    sortBy = SORT_BY_FAVORITE;
            }

            PREFERENCE_HAS_BEEN_UPDATED = true;
        }
    }

    @Override
    public Loader<String[][]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[][]>(this) {

            String[][] results;

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "onStartLoading got called");
                if (args == null)
                    return;

                if (results != null) {
                    deliverResult(results);
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[][] loadInBackground() {
                Log.d(TAG, "loadInBackground got called");
                String getUrl = args.getString(BUNDLE_URL_KEY);
                Log.d(TAG, "getUrl is: " + getUrl);
                String returnedData[][];

                if (!getUrl.equals(SORT_BY_FAVORITE)) {
                    try {
                        URL url = new URL(getUrl);
                        String rawJson = NetworkUtils.getHttpResponse(url);
                        return JsonUtils.parseRawJson(rawJson);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
                    Cursor cursor = movieDbHelper.getReadableDatabase().query(
                            MovieContract.MovieData.TABLE_NAME,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);

                    returnedData = new String[cursor.getCount()][5];

                    for (int i = 0; i < returnedData.length; i++) {
                        cursor.moveToNext();
                        returnedData[i][0] = cursor.getString(cursor.getColumnIndex("title"));
                        returnedData[i][1] = cursor.getString(cursor.getColumnIndex("poster"));
                        returnedData[i][2] = cursor.getString(cursor.getColumnIndex("overview"));
                        returnedData[i][3] = cursor.getString(cursor.getColumnIndex("releaseDate"));
                        returnedData[i][4] = cursor.getString(cursor.getColumnIndex("rating"));
                    }

                    return returnedData;
                }
            }

            @Override
            public void deliverResult(String[][] data) {
                Log.d(TAG, "deliverResult got called");
                results = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[][]> loader, String[][] data) {
        Log.d(TAG, "onLoadFinished got called");
        progressBar.setVisibility(View.INVISIBLE);

        if (data == null) {
            showErrorMessage();
        } else {
            showRecyclerView();
            adapter.setMovieData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[][]> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
