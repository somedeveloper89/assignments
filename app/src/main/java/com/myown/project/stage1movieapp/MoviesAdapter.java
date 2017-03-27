/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Supplies movie data to the view.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    private MoviesAdapterOnClickHandler mClickHandler;

    /**
     * Constructor for initialization.
     *
     * @param context the context.
     * @param movies  list of movies.
     */
    public MoviesAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Registers a MoviesAdapterOnClickHandler listener.
     *
     * @param mClickHandler a class that implements the <code>MoviesAdapterOnClickHandler</code>
     */
    public void setmClickHandler(MoviesAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(parent.getContext());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onMovieClicked(movie);
                }
            });
            imageView.setLayoutParams(new GridView.LayoutParams(R.integer.image_width, R.integer.image_height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.default_padding);
            imageView.setPadding(padding, padding, padding, padding);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri uri = NetworkUtils.relativeToAbsoluteImageUrl(movie.posterRelativePath);
        Picasso.with(getContext()).load(uri).into(imageView);

        return imageView;
    }

    /**
     * To notify the listener when an event has occurred.
     */
    public interface MoviesAdapterOnClickHandler {
        /**
         * Invoked when the user clicks on the poster of a movie.
         *
         * @param movie return the movie object that has been clicked.
         */
        void onMovieClicked(Movie movie);
    }
}
