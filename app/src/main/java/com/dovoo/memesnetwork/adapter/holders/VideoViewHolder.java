package com.dovoo.memesnetwork.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.components.TextViewFaSolid;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;


public class VideoViewHolder extends RecyclerView.ViewHolder {

    public final RelativeLayout relativeLayout;
    public final LinearLayout linBtnLike,linBtnDislike,linBtnComment;
    public final VideoPlayerView mPlayer;
    public final ImageView mCover;
    public final TextViewFaSolid tvIconSound;
    public final TextView tvCategory,tvTitle,tvLabelNoAudio,tvBtnLike,tvBtnDislike,tvTotalLike,tvTotalDislike,tvTotalComment;

    public VideoViewHolder(View view) {
        super(view);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        tvTotalLike = view.findViewById(R.id.tvTotalLike);
        tvTotalDislike = view.findViewById(R.id.tvTotalDislike);
        tvTotalComment = view.findViewById(R.id.tvTotalComment);
        tvBtnLike = view.findViewById(R.id.tvBtnLike);
        tvBtnDislike = view.findViewById(R.id.tvBtnDislike);
        linBtnLike = view.findViewById(R.id.linBtnLike);
        linBtnDislike = view.findViewById(R.id.linBtnDislike);
        linBtnComment = view.findViewById(R.id.linBtnComment);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCategory = view.findViewById(R.id.tvCategory);
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
