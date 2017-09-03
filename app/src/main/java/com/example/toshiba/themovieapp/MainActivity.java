package com.example.toshiba.themovieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import com.example.toshiba.themovieapp.utilities.JsonUtils;
import com.example.toshiba.themovieapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements MovieDbAdapter.ItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SORT_BY_POPULAR = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";
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

        int spanCount = 3;
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        setupPreferences();
        callMovieDbAsyncTask(sortBy);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart got called");
        if (PREFERENCE_HAS_BEEN_UPDATED) {
            callMovieDbAsyncTask(sortBy);
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
        }
    }

    private void callMovieDbAsyncTask(String sortBy) {
        showRecyclerView();
        URL url = NetworkUtils.getUrl(sortBy);
        new MovieDbTask().execute(url);
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
            }

            PREFERENCE_HAS_BEEN_UPDATED = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private class MovieDbTask extends AsyncTask<URL, Void, String[][]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[][] doInBackground(URL... urls) {
            URL url = urls[0];

            try {
                String rawJson = NetworkUtils.getHttpResponse(url);
                return JsonUtils.parseRawJson(rawJson);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            progressBar.setVisibility(View.INVISIBLE);

            if (strings != null) {
                adapter.setMovieData(strings);
            } else {
                showErrorMessage();
            }
        }
    }
}
