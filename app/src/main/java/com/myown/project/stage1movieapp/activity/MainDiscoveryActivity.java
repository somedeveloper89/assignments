/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myown.project.stage1movieapp.R;
import com.myown.project.stage1movieapp.adapter.MoviesRecyclerViewAdapter;
import com.myown.project.stage1movieapp.model.Movie;
import com.myown.project.stage1movieapp.task.FetchMoviesTask;
import com.myown.project.stage1movieapp.util.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity shows a grid view with movies and gives the user the ability to order by popularity or rating.
 */
public class MainDiscoveryActivity extends AppCompatActivity implements FetchMoviesTask.FetchMoviesListener {
    @BindView(R.id.movies_loading_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.movies_loading_textview)
    TextView mMessage;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private MoviesRecyclerViewAdapter mMoviesAdapter;
    private String mSortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindiscovery);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mMoviesAdapter = new MoviesRecyclerViewAdapter(null);
        mRecyclerView.setAdapter(mMoviesAdapter);

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
        new FetchMoviesTask(this).execute(mSortType);
    }

    @Override
    public void loadMovies(List<Movie> movies) {
        mProgressBar.setVisibility(View.GONE);

        if (movies != null) {
            if (movies.size() > 0) {
                mMessage.setVisibility(View.GONE);
                mMoviesAdapter.addAll(movies);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mMessage.setText(R.string.no_movies_found);
            }
        } else {
            mMessage.setText(R.string.error_while_retrieving_data);
        }
    }

    @Override
    public void clearMovies() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mMoviesAdapter.clearMovies();
        mMessage.setText(R.string.loading_movies_text);
        mMessage.setVisibility(View.VISIBLE);
    }
}
