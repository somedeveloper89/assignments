/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.util;

import com.myown.project.stage1movieapp.model.Movie;
import com.myown.project.stage1movieapp.model.Review;
import com.myown.project.stage1movieapp.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for working with json data.
 */
public class JsonUtil {
    private static final String RESULTS = "results";
    private static final String ID = "id";

    /**
     * Returns a collection of movie objects.
     *
     * @param json string json data.
     * @return <code>List<Movie></code> list of movie objects.
     * @throws JSONException
     */
    public static List<Movie> getMoviesListByJson(String json) throws JSONException {
        final String id = "id";
        final String posterpath = "poster_path";
        final String overview = "overview";
        final String release_date = "release_date";
        final String title = "title";
        final String vote_average = "vote_average";

        JSONArray jsonResultArray = getCollectionFromJson(json);
        List<Movie> moviesList = new ArrayList<>();

        for (int i = 0; i < jsonResultArray.length(); i++) {
            JSONObject currentObject = jsonResultArray.getJSONObject(i);
            Movie movie = new Movie(currentObject.getInt(id), currentObject.getString(title),
                    currentObject.getString(posterpath), currentObject.getString(release_date),
                    currentObject.getDouble(vote_average), currentObject.getString(overview));
            moviesList.add(movie);
        }

        return moviesList;
    }

    /**
     * Returns a collection of video objects.
     *
     * @param json string json data.
     * @return <code>List<Video></code> list of video objects.
     * @throws JSONException
     */
    public static List<Video> getVideosListByJson(String json) throws JSONException {

        final String iso_639_1 = "iso_639_1";
        final String iso_3166_1 = "iso_3166_1";
        final String key = "key";
        final String name = "name";
        final String site = "site";
        final String size = "size";
        final String type = "type";

        JSONArray jsonArray = getCollectionFromJson(json);
        List<Video> videoList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentObject = jsonArray.getJSONObject(i);
            Video video = new Video(currentObject.getString(ID), currentObject.getString(iso_639_1),
                    currentObject.getString(iso_3166_1), currentObject.getString(key),
                    currentObject.getString(name), currentObject.getString(site),
                    currentObject.getInt(size), currentObject.getString(type));
            videoList.add(video);
        }

        return videoList;
    }

    /**
     * Returns a collection of review objects.
     *
     * @param json string json data.
     * @return <code>List<Review></code> list of review objects.
     * @throws JSONException
     */
    public static List<Review> getReviewsListByJson(String json) throws JSONException {
        final String author = "author";
        final String content = "content";
        final String url = "url";

        JSONArray jsonArray = getCollectionFromJson(json);
        List<Review> reviewList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentObject = jsonArray.getJSONObject(i);
            Review review = new Review(currentObject.getString(ID), currentObject.getString(author),
                    currentObject.getString(content), currentObject.getString(url));
            reviewList.add(review);
        }

        return reviewList;
    }

    private static JSONArray getCollectionFromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getJSONArray(RESULTS);
    }
}
