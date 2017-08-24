package com.example.toshiba.themovieapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class MainActivity extends AppCompatActivity implements MovieDbAdapter.ItemClickListener {

    private static final String SORT_BY_POPULARITY = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";
    private String sortBy;

    private RecyclerView recyclerView;
    private TextView textViewErrorMessage;
    private ProgressBar progressBar;
    private MovieDbAdapter adapter;

    public static int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        textViewErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        adapter = new MovieDbAdapter(this);

        int spanCount = 3;
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        if (check == 0)
            sortBy = SORT_BY_POPULARITY;
        else if (check == 1)
            sortBy = SORT_BY_TOP_RATED;

        callMovieDbAsyncTask(sortBy);
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
