/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity shows a grid view with movies and gives the user the ability to order by popularity or rating.
 */
public class MainDiscoveryActivity extends AppCompatActivity {
    private static final String TAG = MainDiscoveryActivity.class.getSimpleName();

    private ProgressBar mProgressBar;
    private TextView mMessage;
    private MoviesRecyclerViewAdapter mMoviesAdapter;
    private List<Movie> mMovieList;
    private String mSortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindiscovery);

        mProgressBar = (ProgressBar) findViewById(R.id.movies_loading_progress);
        mMessage = (TextView) findViewById(R.id.movies_loading_textview);

        mMovieList = new ArrayList<>();

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        mMoviesAdapter = new MoviesRecyclerViewAdapter(mMovieList);
        rv.setAdapter(mMoviesAdapter);

        // set default query
        mSortType = NetworkUtils.POPULAR;

        loadMoviesData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sort_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuActionId = item.getItemId();
        boolean consumed = false;

        if (menuActionId == R.id.sort_action_popular) {
            consumed = true;
            reloadIfNeeded(NetworkUtils.POPULAR);
        } else if (menuActionId == R.id.sort_action_rating) {
            consumed = true;
            reloadIfNeeded(NetworkUtils.RATING);
        }

        return consumed || super.onOptionsItemSelected(item);
    }

    private void reloadIfNeeded(String sortTypeClicked) {
        if (!sortTypeClicked.equals(mSortType)) {
            mSortType = sortTypeClicked;
            loadMoviesData();
        }
    }

    private void loadMoviesData() {
        new FetchMoviesTask().execute(mSortType);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        @Override
        protected void onPreExecute() {
            mMoviesAdapter.clearMovies();
            mMoviesAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.VISIBLE);
            mMessage.setText(R.string.loading_movies_text);
            mMessage.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            URL requestUrl = NetworkUtils.buildMovieDBUrl(params[0]);
            try {
                String jsonMovies = NetworkUtils.doRequest(requestUrl);

                if (jsonMovies != null) {
                    return JsonUtil.getMoviesListByJsonData(jsonMovies);
                }
            } catch (JSONException e) {
                // Probably bad json string allow retry
                Log.w(TAG, "Parsing json string failed");
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mProgressBar.setVisibility(View.INVISIBLE);

            if (movies != null) {
                if (movies.size() > 0) {
                    mMessage.setVisibility(View.INVISIBLE);
                    mMovieList = movies;
                    mMoviesAdapter.addAll(mMovieList);
                    mMoviesAdapter.notifyDataSetChanged();
                } else {
                    mMessage.setText(R.string.no_movies_found);
                }
            } else {
                mMessage.setText(R.string.error_while_retrieving_data);
            }
        }
    }
}
