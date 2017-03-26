/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie class represents a movie and it's data.
 */
public class Movie implements Parcelable {
    int id;
    String title;
    String posterRelativePath;
    String overview;
    String releaseDate;
    double voteAverage;

    public Movie(int id, String title, String posterRelativePath, String releaseDate, double voteAverage, String overview) {
        this.id = id;
        this.title = title;
        this.posterRelativePath = posterRelativePath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    private Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.posterRelativePath = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readDouble();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterRelativePath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeDouble(voteAverage);
    }
}
