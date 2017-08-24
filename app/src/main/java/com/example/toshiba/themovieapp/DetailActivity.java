package com.example.toshiba.themovieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

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
}
