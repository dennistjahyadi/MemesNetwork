package com.example.acer.memesnetwork.adapter.holders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.acer.memesnetwork.R;
import com.example.acer.memesnetwork.components.TextViewFaSolid;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.visibility_utils.items.ListItemData;


public class VideoViewHolder extends RecyclerView.ViewHolder {

    public final RelativeLayout relativeLayout;

    public final VideoPlayerView mPlayer;
    public final ImageView mCover;
    public final TextViewFaSolid tvIconSound;
    public final TextView tvTitle,tvLabelNoAudio;

    public VideoViewHolder(View view) {
        super(view);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvLabelNoAudio = view.findViewById(R.id.tvLabelNoAudio);
        mPlayer = view.findViewById(R.id.player);
        mCover = view.findViewById(R.id.cover);
        tvIconSound = view.findViewById(R.id.tvIconSound);
        mPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!mPlayer.hasAudio()){
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mPlayer.isAllVideoMute()) {
                        mPlayer.unMuteVideo();
                        tvIconSound.setText(v.getContext().getResources().getText(R.string.fa_volume_up));
                    } else {
                        mPlayer.muteVideo();
                        tvIconSound.setText(v.getContext().getResources().getText(R.string.fa_volume_mute));
                    }
                }
                return true;
            }
        });

    }
}
