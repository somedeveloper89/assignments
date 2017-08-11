/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.myown.project.stage1movieapp.R;
import com.myown.project.stage1movieapp.activity.DetailActivity;
import com.myown.project.stage1movieapp.model.Movie;
import com.myown.project.stage1movieapp.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Supplies movie data to the view.
 */
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {
    private List<Movie> mMovies;

    /**
     * Constructor for initialization.
     */
    public MoviesRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);

        Context context = holder.mImageView.getContext();
        Uri uri = NetworkUtils.relativeToAbsoluteImageUrl(movie.getPosterRelativePath());
        Picasso.with(context).load(uri).resize(context.getResources().getInteger(R.integer.image_width),
                context.getResources().getInteger(R.integer.image_height)).into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(DetailActivity.MOVIE_EXTRA, movie);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies != null ? mMovies.size() : 0;
    }

    /**
     * Clear the list of movies.
     */
    public void clearMovies() {
        if (mMovies != null) {
            mMovies.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * Load the movies list.
     *
     * @param movies list of movies.
     */
    public void addAll(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_image)
        ImageView mImageView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
