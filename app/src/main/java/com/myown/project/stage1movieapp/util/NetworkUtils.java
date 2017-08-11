/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.util;

import android.net.Uri;
import android.util.Log;

import com.myown.project.stage1movieapp.model.ListType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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
    private static final String MY_API_KEY = "adfdf732e6f9efd3a29c020e4e2e7509";
    public static final String POPULAR = "popular";
    public static final String RATING = "top_rated";
    private static final String MOVIE_DB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String RECOMMENDED_IMAGE_SIZE = "w185";
    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";

    private static final int CONNECTION_TIMEOUT_IN_SECONDS = 15;

    /**
     * Build the URL to talk to the movie database.
     *
     * @param listType the list type to query for.
     * @return String url for the request.
     */
    public static String buildMovieDBUrl(ListType listType) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon().appendPath(listType.equals(ListType.POPULAR) ? POPULAR : RATING)
                .appendQueryParameter(API_KEY, MY_API_KEY).build();
        return convertUriToURL(uri).toString();
    }

    private static URL convertUriToURL(Uri uri) {
        URL url;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            // this shouldn't happen
            throw new RuntimeException(e);
        }
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
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .build();

        Log.d(TAG, "Posting to: " + url.toString());

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

    /**
     * Build the URL to get the video's for a movie.
     *
     * @param movieId the unique id of the movie.
     * @return String url for the request.
     */
    public static String buildUrlForMovieVideo(int movieId) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon().appendPath(String.valueOf(movieId))
                .appendPath(VIDEOS).build();
        return convertUriToURL(uri).toString();
    }

    /**
     * Build the URL to get the reviews for a movie.
     *
     * @param movieId the unique id of the movie.
     * @return String url for the request.
     */
    public static String buildUrlForMovieReviews(int movieId) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon().appendPath(String.valueOf(movieId))
                .appendPath(REVIEWS).build();
        return convertUriToURL(uri).toString();
    }
}
