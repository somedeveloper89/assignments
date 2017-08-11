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
import android.widget.TextView;

import com.myown.project.stage1movieapp.R;
import com.myown.project.stage1movieapp.model.Video;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Supplies video data to the view.
 */
public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.ViewHolder> {
    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private List<Video> mVideos;

    /**
     * Constructor for initialization.
     */
    public VideoRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Video video = mVideos.get(position);
        holder.mTitle.setText(video.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchVideo(video, v.getContext());
            }
        });
    }

    private void launchVideo(Video video, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + video.getKey()));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mVideos != null ? mVideos.size() : 0;
    }

    /**
     * Load the videos list.
     *
     * @param videos list of movies.
     */
    public void addAll(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer)
        View mView;
        @BindView(R.id.trailer_title)
        TextView mTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
