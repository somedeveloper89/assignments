/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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

            Movie movie = intent.getParcelableExtra(MOVIE_EXTRA);
            fillMovieDetails(movie);
        }
    }

    private void fillMovieDetails(Movie movie) {
        mTitle.setText(getString(R.string.movie_title_label, movie.title));
        mRelease.setText(getString(R.string.movie_releaseDate_label, movie.releaseDate));
        mVote.setText(getString(R.string.movie_vote_average_label, String.valueOf(movie.voteAverage)));
        mPlot.setText(movie.overview);

        Uri uri = NetworkUtils.relativeToAbsoluteImageUrl(movie.posterRelativePath);
        Picasso.with(this).load(uri).resize(this.getResources().getInteger(R.integer.image_width),
                this.getResources().getInteger(R.integer.image_height)).into(mImageView);
    }
}
