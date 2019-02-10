package com.example.acer.memesnetwork.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.memesnetwork.R;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;


public class VideoViewHolder extends RecyclerView.ViewHolder{

    public final VideoPlayerView mPlayer;
    public final ImageView mCover;

    public VideoViewHolder(View view) {
        super(view);
        mPlayer =  view.findViewById(R.id.player);
        mCover =  view.findViewById(R.id.cover);
    }
}
