package com.example.toshiba.themovieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.themovieapp.data.MovieContract;
import com.example.toshiba.themovieapp.utilities.JsonUtils;
import com.example.toshiba.themovieapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<String[]>> {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final int LOADER_NUM = 11;
    private static final String KEY = "1";
    private static final String BUNDLE_URL_KEY = "key_url";


    private ReviewAdapter reviewAdapter;
    private RecyclerView recyclerView;
    private String title;
    private String poster;
    private String overView;
    private String releaseData;
    private String voteAverage;
    private String id;
    private String youtubeKey1 = "";
    private String youtubeKey2 = "";
    private Parcelable parcelable;

    private Button btn_fav;
    private Button btn_un_fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_constraint);

        reviewAdapter = new ReviewAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_reviews);
        final TextView titleTextView = (TextView) findViewById(R.id.movie_title);
        final ImageView posterImageView = (ImageView) findViewById(R.id.movie_poster);
        final TextView overViewTextView = (TextView) findViewById(R.id.movie_overview);
        final TextView releaseDataTextView = (TextView) findViewById(R.id.movie_release_date);
        final TextView voteAverageTextView = (TextView) findViewById(R.id.movie_rating);
        btn_fav = (Button) findViewById(R.id.movie_fav_button);
        btn_un_fav = (Button) findViewById(R.id.movie_un_fav_button);

        Intent intent = getIntent();
        String[] movieInformation = intent.getStringArrayExtra(Intent.EXTRA_TEXT);

        title = movieInformation[0];
        poster = movieInformation[1];
        overView = movieInformation[2];
        releaseData = movieInformation[3];
        voteAverage = movieInformation[4];
        id = movieInformation[5];
        Log.d(TAG, id);

        Picasso.with(this).load(IMAGE_BASE_URL + poster).into(posterImageView);

        titleTextView.setText(title);
        overViewTextView.setText(overView);
        releaseDataTextView.setText(releaseData);
        voteAverageTextView.setText(voteAverage + "/10");

        int count = checkDbForItem();
        setCorrectBtn(count);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(reviewAdapter);

        if (savedInstanceState != null) {
            recyclerView.getLayoutManager()
                    .onRestoreInstanceState(savedInstanceState.getParcelable(KEY));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            parcelable = savedInstanceState.getParcelable(KEY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ( youtubeKey1.equals("") && youtubeKey2.equals("") ) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_URL_KEY, id);

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String[][]> loader = loaderManager.getLoader(LOADER_NUM);

            if (loader == null)
                loaderManager.initLoader(LOADER_NUM, bundle, this);
            else
                loaderManager.restartLoader(LOADER_NUM, bundle, this);
        }
    }

    private void watchTrailer(String trailerPath) {
        Uri uri = Uri.parse(trailerPath);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    private final void setCorrectBtn(int count) {
        if (count > 0) {
            btn_fav.setVisibility(View.INVISIBLE);
            btn_un_fav.setVisibility(View.VISIBLE);
        } else {
            btn_un_fav.setVisibility(View.INVISIBLE);
            btn_fav.setVisibility(View.VISIBLE);
        }
    }

    private final ContentValues setDetailContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieData.COLUMN_TITLE, title);
        cv.put(MovieContract.MovieData.COLUMN_POSTER, poster);
        cv.put(MovieContract.MovieData.COLUMN_OVERVIEW, overView);
        cv.put(MovieContract.MovieData.COLUMN_RELEASE_DATE, releaseData);
        cv.put(MovieContract.MovieData.COLUMN_RATING, voteAverage);
        cv.put(MovieContract.MovieData.COLUMN_ID, id);
        return cv;
    }

    public void addToFav(View view) {
        Uri uri = getContentResolver().insert(MovieContract.MovieData.CONTENT_URI, setDetailContentValues());
        if (uri != null) {
            btn_fav.setVisibility(View.INVISIBLE);
            btn_un_fav.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Added to favorite", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromFav(View view) {
        Uri uri = MovieContract.MovieData.CONTENT_URI.buildUpon().appendPath(id).build();
        int rowDeleted = getContentResolver().delete(uri, null, null);
        if (rowDeleted > 0) {
            btn_un_fav.setVisibility(View.INVISIBLE);
            btn_fav.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Favorite removed", Toast.LENGTH_SHORT).show();
        }
    }

    public void playTrailer1(View view) {
        if (isOnline())
            watchTrailer(YOUTUBE_URL + youtubeKey1);
        else
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
    }

    public void playTrailer2(View view) {
        if (isOnline())
            watchTrailer(YOUTUBE_URL + youtubeKey2);
        else
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
    }

    private int checkDbForItem() {
        Uri uri = MovieContract.MovieData.CONTENT_URI.buildUpon().appendPath(id).build();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        return cursor.getCount();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            if (!youtubeKey1.equals("")) {
                ShareCompat.IntentBuilder
                        .from(this)
                        .setType("text/plain")
                        .setChooserTitle(title + " trailer:")
                        .setText(YOUTUBE_URL + youtubeKey1)
                        .startChooser();
            } else {
                Toast.makeText(this, "Path empty, please try again", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<String[]>> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<String[]>>(this) {

            ArrayList<String[]> results;

            @Override
            protected void onStartLoading() {
                if (args == null)
                    return;
                if (results != null) {
                    deliverResult(results);
                } else
                    forceLoad();
            }

            @Override
            public ArrayList<String[]> loadInBackground() {
                String id = args.getString(BUNDLE_URL_KEY);
                URL urlTrailer = NetworkUtils.getTrailerUrl(id);
                URL urlReview = NetworkUtils.getReviewUrl(id);
                ArrayList<String[]> arrayList = new ArrayList<>();
                try {
                    String rawJsonTrailer = NetworkUtils.getHttpResponse(urlTrailer);
                    String rawJsonReviews = NetworkUtils.getHttpResponse(urlReview);
                    String[] trailerArray = JsonUtils.parsedJsonTrailers(rawJsonTrailer);
                    String[] reviewArray = JsonUtils.parseReviewJson(rawJsonReviews);
                    arrayList.add(trailerArray);
                    arrayList.add(reviewArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return arrayList;
            }

            @Override
            public void deliverResult(ArrayList<String[]> data) {
                results = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String[]>> loader, ArrayList<String[]> data) {
        String[] stringArrayTrailer = null;
        String[] stringArrayReview = null;
        if (data.size() == 2) {
            stringArrayTrailer = data.get(0);
            stringArrayReview = data.get(1);
        }
        if (stringArrayTrailer != null) {
            youtubeKey1 = stringArrayTrailer[0];
            youtubeKey2 = stringArrayTrailer[1];
        }
        if (stringArrayTrailer != null) {
            reviewAdapter.setReviewData(stringArrayReview);
            if (parcelable != null)
                recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String[]>> loader) {

    }
}
