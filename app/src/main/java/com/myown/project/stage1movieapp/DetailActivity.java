/** Copyright (C) 2017 Mustafa Kabaktepe */

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
public class DetailActivity extends AppCompatActivity
{
   public static final String TITLE_EXTRA = "TITLE_EXTRA";
   public static final String RELEASE_DATE_EXTRA = "RELEASE_DATE_EXTRA";
   public static final String POSTER_EXTRA = "POSTER_EXTRA";
   public static final String PLOT_EXTRA = "PLOT_EXTRA";
   public static final String VOTE_EXTRA = "VOTE_EXTRA";

   private String mTitle;
   private String mReleaseDate;
   private String mPosterPath;
   private String mPlotSynopsis;
   private String mVoteAverage;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_detail);

      Intent intent = getIntent();

      if (intent.hasExtra(TITLE_EXTRA))
      {
         mTitle = intent.getStringExtra(DetailActivity.TITLE_EXTRA);
         mReleaseDate = intent.getStringExtra(DetailActivity.RELEASE_DATE_EXTRA);
         mPosterPath = intent.getStringExtra(DetailActivity.POSTER_EXTRA);
         mPlotSynopsis = intent.getStringExtra(DetailActivity.PLOT_EXTRA);
         mVoteAverage = intent.getStringExtra(DetailActivity.VOTE_EXTRA);
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
      Picasso.with(this).load(uri).into(imageView);
   }
}
