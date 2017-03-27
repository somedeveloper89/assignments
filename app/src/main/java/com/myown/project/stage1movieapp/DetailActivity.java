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

/**
 * This activity shows the details of a selected movie.
 */
public class DetailActivity extends AppCompatActivity {
    public static final String MOVIE_EXTRA = "MOVIE_EXTRA";

    private String mTitle;
    private String mReleaseDate;
    private String mPosterPath;
    private String mPlotSynopsis;
    private String mVoteAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        if (intent.hasExtra(MOVIE_EXTRA)) {

            Movie movie = intent.getParcelableExtra(MOVIE_EXTRA);

            mTitle = movie.title;
            mReleaseDate = movie.releaseDate;
            mPosterPath = movie.posterRelativePath;
            mPlotSynopsis = movie.overview;
            mVoteAverage = String.valueOf(movie.voteAverage);
        }

        ImageView imageView = (ImageView) findViewById(R.id.detailscreen_movie_poster);
        TextView titleText = (TextView) findViewById(R.id.detailscreen_title);
        TextView releaseText = (TextView) findViewById(R.id.detailscreen_release_date);
        TextView voteText = (TextView) findViewById(R.id.detailscreen_vote_average);
        TextView plotText = (TextView) findViewById(R.id.detailscreen_plot_synopsis);

        titleText.setText(getString(R.string.movie_title_label, mTitle));
        releaseText.setText(getString(R.string.movie_releaseDate_label, mReleaseDate));
        voteText.setText(getString(R.string.movie_vote_average_label, mVoteAverage));
        plotText.setText(mPlotSynopsis);

        Uri uri = NetworkUtils.relativeToAbsoluteImageUrl(mPosterPath);
        Picasso.with(this).load(uri).resize(R.integer.image_width, R.integer.image_height).into(imageView);
    }
}
