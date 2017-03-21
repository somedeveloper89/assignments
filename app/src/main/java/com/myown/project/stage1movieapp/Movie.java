/** Copyright (C) 2017 Mustafa Kabaktepe */

package com.myown.project.stage1movieapp;

/**
 * Movie class represents a movie and it's data.
 */
public class Movie
{
   int id;
   String title;
   String posterRelativePath;
   String overview;
   String releaseDate;
   double voteAverage;

   public Movie(int id, String title, String posterRelativePath, String releaseDate, double voteAverage, String overview)
   {
      this.id = id;
      this.title = title;
      this.posterRelativePath = posterRelativePath;
      this.releaseDate = releaseDate;
      this.voteAverage = voteAverage;
      this.overview = overview;
   }
}
