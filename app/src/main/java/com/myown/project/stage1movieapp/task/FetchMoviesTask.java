/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.task;

import android.os.AsyncTask;
import android.util.Log;

import com.myown.project.stage1movieapp.model.Movie;
import com.myown.project.stage1movieapp.util.JsonUtil;
import com.myown.project.stage1movieapp.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * This class executes the service requests in the background and notifies it's listener about changes.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
    private static final String TAG = FetchMoviesTask.class.getSimpleName();

    private FetchMoviesListener mListener;

    public FetchMoviesTask(FetchMoviesListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mListener.clearMovies();
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        URL requestUrl = NetworkUtils.buildMovieDBUrl(params[0]);
        try {
            JSONObject emptyJson = new JSONObject();
            String jsonMovies = NetworkUtils.post(requestUrl, emptyJson.toString());

            if (jsonMovies != null) {
                return JsonUtil.getMoviesListByJsonData(jsonMovies);
            }
        } catch (IOException e) {
            Log.w(TAG, "Service request failed: " + e.getMessage());
        } catch (JSONException e) {
            // Probably bad json string allow retry
            Log.w(TAG, "Parsing json string failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        mListener.loadMovies(movies);
    }

    /**
     * The interface for listening to events within the FetchMoviesTask class.
     */
    public interface FetchMoviesListener {

        /**
         * Invoked when the get movies service call has succeeded.
         *
         * @param movies
         */
        void loadMovies(List<Movie> movies);

        /**
         * Invoked when the get movies service call is about to be executed.
         */
        void clearMovies();
    }
}
