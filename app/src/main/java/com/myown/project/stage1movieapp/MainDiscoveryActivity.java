/** Copyright (C) 2017 Mustafa Kabaktepe */

package com.myown.project.stage1movieapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This activity shows a grid view with movies and gives the user the ability to order by popularity or rating.
 */
public class MainDiscoveryActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler
{
   private static final String TAG = MainDiscoveryActivity.class.getSimpleName();

   private ProgressBar mProgressBar;
   private TextView mMessage;
   private MoviesAdapter mMoviesAdapter;
   private List<Movie> mMovieList;
   private String mSortType;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_maindiscovery);

      mProgressBar = (ProgressBar) findViewById(R.id.movies_loading_progress);
      mMessage = (TextView) findViewById(R.id.movies_loading_textview);

      ListView listView = (ListView) findViewById(R.id.movies_listview);
      mMovieList = new ArrayList<>();

      mMoviesAdapter = new MoviesAdapter(this, mMovieList);
      listView.setAdapter(mMoviesAdapter);
      mMoviesAdapter.setmClickHandler(this);

      // set default query
      mSortType = NetworkUtils.POPULAR;

      loadMoviesData();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      MenuInflater menuInflater = getMenuInflater();
      menuInflater.inflate(R.menu.menu_sort_item, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      int menuActionId = item.getItemId();
      boolean consumed = false;

      if (menuActionId == R.id.sort_action_popular)
      {
         consumed = true;
         reloadIfNeeded(NetworkUtils.POPULAR);
      }
      else if (menuActionId == R.id.sort_action_rating)
      {
         consumed = true;
         reloadIfNeeded(NetworkUtils.RATING);
      }

      return consumed || super.onOptionsItemSelected(item);
   }

   private void reloadIfNeeded(String sortTypeClicked)
   {
      if (!sortTypeClicked.equals(mSortType))
      {
         mSortType = sortTypeClicked;
         loadMoviesData();
      }
   }

   private void loadMoviesData()
   {
      new FetchMoviesTask().execute(mSortType);
   }

   @Override
   public void onMovieClicked(Movie movie)
   {
      Intent intent = new Intent(this, DetailActivity.class);
      intent.putExtra(DetailActivity.TITLE_EXTRA, movie.title);
      intent.putExtra(DetailActivity.RELEASE_DATE_EXTRA, movie.releaseDate);
      intent.putExtra(DetailActivity.POSTER_EXTRA, movie.posterRelativePath);
      intent.putExtra(DetailActivity.PLOT_EXTRA, movie.overview);
      intent.putExtra(DetailActivity.VOTE_EXTRA, String.valueOf(movie.voteAverage));

      startActivity(intent);
   }

   class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>>
   {
      @Override
      protected void onPreExecute()
      {
         mMoviesAdapter.clear();
         mMoviesAdapter.notifyDataSetChanged();
         mProgressBar.setVisibility(View.VISIBLE);
         mMessage.setText(R.string.loading_movies_text);
         mMessage.setVisibility(View.VISIBLE);
      }

      @Override
      protected List<Movie> doInBackground(String... params)
      {
         URL requestUrl = NetworkUtils.buildMovieDBUrl(params[0]);
         try
         {
            String jsonMovies = NetworkUtils.doRequest(requestUrl);

            if (jsonMovies != null)
            {
               List<Movie> movies = JsonUtil.getMoviesListByJsonData(jsonMovies);
               return movies;
            }
         }
         catch (JSONException e)
         {
            // Probably bad json string allow retry
            Log.w(TAG, "Parsing json string failed");
         }
         return null;
      }

      @Override
      protected void onPostExecute(List<Movie> movies)
      {
         mProgressBar.setVisibility(View.INVISIBLE);

         if (movies != null)
         {
            if (movies.size() > 0)
            {
               mMessage.setVisibility(View.INVISIBLE);
               mMovieList = movies;
               mMoviesAdapter.addAll(mMovieList);
               mMoviesAdapter.notifyDataSetChanged();
            }
            else
            {
               mMessage.setText(R.string.no_movies_found);
            }
         }
         else
         {
            mMessage.setText(R.string.error_while_retrieving_data);
         }
      }
   }
}
