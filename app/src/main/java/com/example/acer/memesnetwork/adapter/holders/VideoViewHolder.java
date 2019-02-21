package com.example.acer.memesnetwork.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.memesnetwork.R;
import com.example.acer.memesnetwork.components.TextViewFaSolid;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;


public class VideoViewHolder extends RecyclerView.ViewHolder{

    public final VideoPlayerView mPlayer;
    public final ImageView mCover;
    public final TextViewFaSolid tvIconSound;
    public final TextView tvTitle;

    public VideoViewHolder(View view) {
        super(view);
        tvTitle = view.findViewById(R.id.tvTitle);
        mPlayer =  view.findViewById(R.id.player);
        mCover =  view.findViewById(R.id.cover);
        tvIconSound = view.findViewById(R.id.tvIconSound);
        mPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(mPlayer.isAllVideoMute()){
                        mPlayer.unMuteVideo();
                        tvIconSound.setText(v.getContext().getResources().getText(R.string.fa_volume_up));
                    }else{
                        mPlayer.muteVideo();
                        tvIconSound.setText(v.getContext().getResources().getText(R.string.fa_volume_mute));
                    }

                }
                return true;
            }
        });
    }
}
