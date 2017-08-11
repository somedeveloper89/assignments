/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myown.project.stage1movieapp.R;
import com.myown.project.stage1movieapp.data.MovieContract;
import com.myown.project.stage1movieapp.model.Movie;
import com.myown.project.stage1movieapp.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This activity shows the details of a selected movie.
 */
public class DetailActivity extends AppCompatActivity {
    public static final String MOVIE_EXTRA = "MOVIE_EXTRA";

    @BindView(R.id.detailscreen_movie_poster)
    ImageView mImageView;
    @BindView(R.id.detailscreen_title)
    TextView mTitle;
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

    private Movie mCurrentMovie;

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

        mTitle.setText(getString(R.string.movie_title_label, mCurrentMovie.getTitle()));
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
}
