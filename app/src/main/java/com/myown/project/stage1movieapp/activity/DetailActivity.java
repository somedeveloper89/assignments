/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myown.project.stage1movieapp.R;
import com.myown.project.stage1movieapp.adapter.ReviewRecyclerViewAdapter;
import com.myown.project.stage1movieapp.adapter.VideoRecyclerViewAdapter;
import com.myown.project.stage1movieapp.data.MovieContract;
import com.myown.project.stage1movieapp.model.Movie;
import com.myown.project.stage1movieapp.model.Review;
import com.myown.project.stage1movieapp.model.Video;
import com.myown.project.stage1movieapp.task.GenericRequestTask;
import com.myown.project.stage1movieapp.util.JsonUtil;
import com.myown.project.stage1movieapp.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This activity shows the details of a selected movie.
 */
public class DetailActivity extends AppCompatActivity implements GenericRequestTask.GenericRequestListener {
    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final String MOVIE_EXTRA = "MOVIE_EXTRA";
    private static final int LOAD_VIDEOS = 1;
    private static final int LOAD_REVIEWS = 2;

    @BindView(R.id.detailscreen_movie_poster)
    ImageView mImageView;
    @BindView(R.id.detailscreen_release_date)
    TextView mRelease;
    @BindView(R.id.detailscreen_vote_average)
    TextView mVote;
    @BindView(R.id.detailscreen_plot_synopsis)
    TextView mPlot;
    @BindView(R.id.favorite)
    ImageView mFavoritesImage;
    @BindView(R.id.favorite_text)
    TextView mFavoriteText;
    @BindView(R.id.videos_recyclerview)
    RecyclerView mVideoRecyclerView;
    @BindView(R.id.reviews_recyclerview)
    RecyclerView mReviewsRecyclerView;

    private Movie mCurrentMovie;
    private int taskLoaderFor;
    private VideoRecyclerViewAdapter mVideoAdapter;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private Video mFirstVideoToShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        if (intent.hasExtra(MOVIE_EXTRA)) {
            mCurrentMovie = intent.getParcelableExtra(MOVIE_EXTRA);
            fillMovieDetails();
        }

        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVideoAdapter = new VideoRecyclerViewAdapter();
        mVideoRecyclerView.setAdapter(mVideoAdapter);

        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewAdapter = new ReviewRecyclerViewAdapter();
        mReviewsRecyclerView.setAdapter(mReviewAdapter);

        loadVideos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_share) {
            if (mFirstVideoToShare != null) {
                startActivity(shareFirstTrailer());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent shareFirstTrailer() {
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(VideoRecyclerViewAdapter.YOUTUBE_URL + mFirstVideoToShare.getKey())
                .getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }

        return intent;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentMovie = savedInstanceState.getParcelable(MOVIE_EXTRA);
            fillMovieDetails();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentMovie != null) {
            outState.putParcelable(MOVIE_EXTRA, mCurrentMovie);
        }
        super.onSaveInstanceState(outState);
    }

    private boolean isMovieFavorite() {
        String[] selectionArgs = {String.valueOf(mCurrentMovie.getId())};
        String selectionClause = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        String[] projection = {MovieContract.MovieEntry._ID};

        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                selectionClause,
                selectionArgs,
                null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                // We have a match, which means this movie is in the favorites list.
                return true;
            }
            cursor.close();
        }

        return false;
    }

    private void updateFavoriteStatus() {
        boolean favorite = isMovieFavorite();
        mFavoritesImage.setImageResource(favorite ? R.drawable.ic_filled_star : R.drawable.ic_empty_star);
        mFavoriteText.setText(favorite ? getString(R.string.favorite_textview_remove) : getString(R.string.favorite_textview_add));
    }

    private void fillMovieDetails() {
        updateFavoriteStatus();

        setTitle(mCurrentMovie.getTitle());
        mRelease.setText(getString(R.string.movie_releaseDate_label, mCurrentMovie.getReleaseDate()));
        mVote.setText(getString(R.string.movie_vote_average_label, String.valueOf(mCurrentMovie.getVoteAverage())));
        mPlot.setText(mCurrentMovie.getOverview());

        Uri uri = NetworkUtils.relativeToAbsoluteImageUrl(mCurrentMovie.getPosterRelativePath());
        Picasso.with(this).load(uri).resize(this.getResources().getInteger(R.integer.image_width),
                this.getResources().getInteger(R.integer.image_height)).into(mImageView);
    }

    @OnClick(R.id.favorite)
    void onClickFavorite(View view) {
        // Prevent multiple clicks and allow the data to be saved into the database.
        mFavoritesImage.setEnabled(false);

        if (isMovieFavorite()) {
            removeMovieFromFavorites();
        } else {
            addMovieToFavorites();
        }

        updateFavoriteStatus();
        // Enable the favorites button again.
        mFavoritesImage.setEnabled(true);
    }

    @OnClick(R.id.show_trailers)
    void onClickShowTrailers(TextView view) {
        if (mVideoRecyclerView.getVisibility() == View.VISIBLE) {
            view.setText(getString(R.string.videos_title_show));
            mVideoRecyclerView.setVisibility(View.GONE);
        } else {
            view.setText(getString(R.string.videos_title_hide));
            mVideoRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.show_reviews)
    void onClickShowReviews(TextView view) {
        if (mReviewsRecyclerView.getVisibility() == View.VISIBLE) {
            view.setText(getString(R.string.reviews_title_show));
            mReviewsRecyclerView.setVisibility(View.GONE);
        } else {
            view.setText(getString(R.string.reviews_title_hide));
            mReviewsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void addMovieToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mCurrentMovie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mCurrentMovie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mCurrentMovie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, mCurrentMovie.getPosterRelativePath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mCurrentMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mCurrentMovie.getVoteAverage());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(this, R.string.movie_added_to_favorites_list, Toast.LENGTH_SHORT).show();
        }
    }

    private void removeMovieFromFavorites() {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(mCurrentMovie.getId())).build();

        int rowsDeleted = getContentResolver().delete(uri,
                null,
                null);

        if (rowsDeleted == 1) {
            Toast.makeText(this, R.string.movie_removed_from_favorites_list, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVideos() {
        taskLoaderFor = LOAD_VIDEOS;
        String url = NetworkUtils.buildUrlForMovieVideo(mCurrentMovie.getId());
        new GenericRequestTask(this, true).execute(url);
    }

    private void loadReviews() {
        taskLoaderFor = LOAD_REVIEWS;
        String url = NetworkUtils.buildUrlForMovieReviews(mCurrentMovie.getId());
        new GenericRequestTask(this, true).execute(url);
    }

    @Override
    public void onPreExecute() {
        // NOOP
    }

    @Override
    public void onPostExecute(String json) {
        if (json != null) {
            if (taskLoaderFor == LOAD_VIDEOS) {
                try {
                    List<Video> videoList = JsonUtil.getVideosListByJson(json);
                    Log.d(TAG, "number of videos available for this movie " + videoList.size());
                    mFirstVideoToShare = videoList.get(0);
                    mVideoAdapter.addAll(videoList);

                    // Continue loading reviews.
                    loadReviews();
                } catch (JSONException e) {
                    Log.e(TAG, "Exception: ", e);
                }
            } else if (taskLoaderFor == LOAD_REVIEWS) {
                try {
                    List<Review> reviewList = JsonUtil.getReviewsListByJson(json);
                    Log.d(TAG, "number of reviews available for this movie " + reviewList.size());
                    mReviewAdapter.addAll(reviewList);
                } catch (JSONException e) {
                    Log.e(TAG, "Exception: ", e);
                }
            }
        }
    }
}
