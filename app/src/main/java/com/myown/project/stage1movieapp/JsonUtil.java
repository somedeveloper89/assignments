/** Copyright (C) 2017 Mustafa Kabaktepe */

package com.myown.project.stage1movieapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for working with json data.
 */
public class JsonUtil
{
   /**
    * Returns a list of list of movie objects parsed out of the supplied json string.
    *
    * @param jsonData string json data.
    * @return <code>List<Movie></code> list of movie objects.
    * @throws JSONException
    */
   public static List<Movie> getMoviesListByJsonData(String jsonData) throws JSONException
   {
      final String results = "results";
      final String id = "id";
      final String posterpath = "poster_path";
      final String overview = "overview";
      final String release_date = "release_date";
      final String title = "title";
      final String vote_average = "vote_average";

      JSONObject jsonObject = new JSONObject(jsonData);
      JSONArray jsonResultArray = jsonObject.getJSONArray(results);

      List<Movie> moviesList = new ArrayList<>();

      for (int i = 0; i < jsonResultArray.length(); i++)
      {
         JSONObject currentObject = jsonResultArray.getJSONObject(i);
         Movie movie = new Movie(currentObject.getInt(id), currentObject.getString(title),
               currentObject.getString(posterpath), currentObject.getString(release_date),
               currentObject.getDouble(vote_average), currentObject.getString(overview));
         moviesList.add(movie);
      }

      return moviesList;
   }
}
