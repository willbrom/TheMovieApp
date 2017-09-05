package com.example.toshiba.themovieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.themovieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

    private String title;
    private String poster;
    private String overView;
    private String releaseData;
    private String voteAverage;

    private TextView titleTextView;
    private ImageView posterImageView;
    private TextView overViewTextView;
    private TextView releaseDataTextView;
    private TextView voteAverageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTextView = (TextView) findViewById(R.id.tv_movie_title);
        posterImageView = (ImageView) findViewById(R.id.iv_poster);
        overViewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        releaseDataTextView = (TextView) findViewById(R.id.tv_movie_date);
        voteAverageTextView = (TextView) findViewById(R.id.tv_movie_rating);

        Intent intent = getIntent();
        String[] movieInformation = intent.getStringArrayExtra(Intent.EXTRA_TEXT);

        title = movieInformation[0];
        poster = movieInformation[1];
        overView = movieInformation[2];
        releaseData = movieInformation[3];
        voteAverage = movieInformation[4];

        Picasso.with(this).load(IMAGE_BASE_URL + poster).into(posterImageView);

        titleTextView.setText(title);
        overViewTextView.setText(overView);
        releaseDataTextView.setText(releaseData);
        voteAverageTextView.setText(voteAverage);

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

        if (itemId == R.id.action_add_fav) {
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieData.COLUMN_TITLE, title);
            cv.put(MovieContract.MovieData.COLUMN_POSTER, poster);
            cv.put(MovieContract.MovieData.COLUMN_OVERVIEW, overView);
            cv.put(MovieContract.MovieData.COLUMN_RELEASE_DATE, releaseData);
            cv.put(MovieContract.MovieData.COLUMN_RATING, voteAverage);

            Uri uri = getContentResolver().insert(MovieContract.MovieData.CONTENT_URI, cv);
            Log.d(TAG, "Here is the uri: " + uri.toString());

            if (uri != null)
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
