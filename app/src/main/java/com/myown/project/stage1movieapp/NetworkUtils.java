/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    private static final String MY_API_KEY = "ADD-YOUR-API-KEY-HERE";
    static final String POPULAR = "popular";
    static final String RATING = "top_rated";
    private static final String MOVIE_DB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String RECOMMENDED_IMAGE_SIZE = "w185";

    private static final int DEFAULT_CONNECTION_TIMEOUT = 60 * 5;

    /**
     * Build the URL to talk to the movie database.
     *
     * @param query String query
     * @return URL object for the request.
     */
    public static URL buildMovieDBUrl(String query) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon().appendPath(query.equals(POPULAR) ? POPULAR : RATING)
                .appendQueryParameter(API_KEY, MY_API_KEY).build();

        URL url;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            // this shouldn't happen
            throw new RuntimeException(e);
        }

        Log.d(TAG, url.toString());

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String doRequest(URL url) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();

                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                return scanner.hasNext() ? scanner.next() : null;
            }
        } catch (IOException e) {
            // failed so just return null so we can retry
            Log.w(TAG, e.getMessage(), e);
        } finally {
            connection.disconnect();
        }

        return null;
    }

    /**
     * Resolve the absolute path to the movie poster image.
     *
     * @param posterRelativePath the relative path.
     * @return the actual path.
     */
    public static Uri relativeToAbsoluteImageUrl(String posterRelativePath) {
        return Uri.parse(MOVIE_DB_IMAGE_BASE_URL).buildUpon().appendPath(RECOMMENDED_IMAGE_SIZE)
                .appendEncodedPath(posterRelativePath).build();
    }
}
