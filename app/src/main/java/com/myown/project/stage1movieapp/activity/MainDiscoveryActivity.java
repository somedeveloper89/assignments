/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.myown.project.stage1movieapp.R;
import com.myown.project.stage1movieapp.adapter.MoviesRecyclerViewAdapter;
import com.myown.project.stage1movieapp.data.MovieContract;
import com.myown.project.stage1movieapp.model.ListType;
import com.myown.project.stage1movieapp.model.Movie;
import com.myown.project.stage1movieapp.task.GenericRequestTask;
import com.myown.project.stage1movieapp.util.JsonUtil;
import com.myown.project.stage1movieapp.util.NetworkUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity shows a grid view with movies and gives the user the ability to order by popularity or rating.
 */
public class MainDiscoveryActivity extends AppCompatActivity implements GenericRequestTask.GenericRequestListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainDiscoveryActivity.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 0;
    private static final String EXTRA_CURRENT_LIST_TYPE = "EXTRA_CURRENT_LIST_TYPE";

    @BindView(R.id.movies_loading_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.movies_loading_textview)
    TextView mMessage;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private MoviesRecyclerViewAdapter mMoviesAdapter;
    private ListType mCurrentListType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindiscovery);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mMoviesAdapter = new MoviesRecyclerViewAdapter(null);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mCurrentListType = ListType.POPULAR;
    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadMovies();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentListType = (ListType) savedInstanceState.get(EXTRA_CURRENT_LIST_TYPE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_CURRENT_LIST_TYPE, mCurrentListType);
        super.onSaveInstanceState(outState);
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
            reloadIfNeeded(ListType.POPULAR);
        } else if (menuActionId == R.id.sort_action_rating) {
            consumed = true;
            reloadIfNeeded(ListType.RATING);
        } else if (menuActionId == R.id.favorites_action) {
            consumed = true;
            reloadIfNeeded(ListType.FAVORITE);
        }

        return consumed || super.onOptionsItemSelected(item);
    }

    private void reloadIfNeeded(ListType selectedListType) {
        if (!selectedListType.equals(mCurrentListType)) {
            mCurrentListType = selectedListType;
            reloadMovies();
        }
    }

    private void reloadMovies() {
        switch (mCurrentListType) {
            case POPULAR:
            case RATING:
                loadMoviesDataByAsyncTask();
                break;
            case FAVORITE:
                initFavoriteMoviesLoader();
        }
    }

    private void loadMoviesDataByAsyncTask() {
        String url = NetworkUtils.buildMovieDBUrl(mCurrentListType);
        new GenericRequestTask(this).execute(url);
    }

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

    public void clearMovies() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mMoviesAdapter.clearMovies();
        mMessage.setText(R.string.loading_movies_text);
        mMessage.setVisibility(View.VISIBLE);
    }

    private void initFavoriteMoviesLoader() {
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(MOVIE_LOADER_ID);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return movie data as a Cursor or null if an error occurs.
     * <p>
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                clearMovies();
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load the favorites data. " + e.getMessage());
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mCursor = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Movie> moviesList = new ArrayList<>();

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(data.getInt(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            movie.setOverview(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            movie.setPosterRelativePath(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)));
            movie.setReleaseDate(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movie.setTitle(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movie.setVoteAverage(data.getDouble(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            moviesList.add(movie);
        }

        loadMovies(moviesList);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // NOOP
    }

    @Override
    public void onPreExecute() {
        clearMovies();
    }

    @Override
    public void onPostExecute(String json) {
        if (json != null) {
            try {
                List<Movie> movies = JsonUtil.getMoviesListByJsonData(json);
                loadMovies(movies);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
