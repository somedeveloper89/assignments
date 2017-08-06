/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
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
    public static String post(URL url, String json) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
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
