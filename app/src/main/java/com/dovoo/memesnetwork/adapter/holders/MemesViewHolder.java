package com.dovoo.memesnetwork.adapter.holders;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.components.TextViewFaSolid;
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest;
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerDispatcher;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.Playable;
import im.ene.toro.helper.ToroPlayerHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import im.ene.toro.widget.PressablePlayerSelector;

public class MemesViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {

    private static final String TAG = "Toro:Basic:Holder";

    public static final int LAYOUT_RES = R.layout.row_meme;

    ToroPlayerHelper helper;
    Uri mediaUri;

    PlayerView playerView;
    public RelativeLayout relativeLayout;
    public LinearLayout linBtnLike, linBtnDislike, linBtnComment;
    public ImageView mCover;
    public TextViewFaSolid tvIconSound;
    public TextView tvCategory, tvTitle, tvLabelNoAudio, tvBtnLike, tvBtnDislike, tvTotalLike, tvTotalDislike, tvTotalComment, tvBtnShare;

    public SpinKitView loadingBarVideo;

    public Playable.EventListener listener;

    public MemesViewHolder(final View itemView, PressablePlayerSelector selector) {
        super(itemView);
        playerView = itemView.findViewById(R.id.playerView);
        relativeLayout = itemView.findViewById(R.id.relativeLayout);
        tvTotalLike = itemView.findViewById(R.id.tvTotalLike);
        tvTotalDislike = itemView.findViewById(R.id.tvTotalDislike);
        tvTotalComment = itemView.findViewById(R.id.tvTotalComment);
        tvBtnLike = itemView.findViewById(R.id.tvBtnLike);
        loadingBarVideo = itemView.findViewById(R.id.loadingBarVideo);
        tvBtnDislike = itemView.findViewById(R.id.tvBtnDislike);
        linBtnLike = itemView.findViewById(R.id.linBtnLike);
        linBtnDislike = itemView.findViewById(R.id.linBtnDislike);
        linBtnComment = itemView.findViewById(R.id.linBtnComment);
        tvBtnShare = itemView.findViewById(R.id.tvBtnShare);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvCategory = itemView.findViewById(R.id.tvCategory);
        tvLabelNoAudio = itemView.findViewById(R.id.tvLabelNoAudio);
        mCover = itemView.findViewById(R.id.cover);
        tvIconSound = itemView.findViewById(R.id.tvIconSound);
        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (playerView.getPlayer() == null) {
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            if (GlobalFunc.isMute) {
                                playerView.getPlayer().getAudioComponent().setVolume(1);
                                tvIconSound.setText(itemView.getContext().getResources().getText(R.string.fa_volume_up));
                                GlobalFunc.isMute = false;
                            } else {
                                playerView.getPlayer().getAudioComponent().setVolume(0);
                                tvIconSound.setText(itemView.getContext().getResources().getText(R.string.fa_volume_mute));
                                GlobalFunc.isMute = true;
                            }

                        }
                    });

                }
                return true;
            }
        });

        if (selector != null)
            playerView.setControlDispatcher(new ExoPlayerDispatcher(selector, this));
    }


    @NonNull
    @Override
    public View getPlayerView() {
        return playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
        if (helper == null) {
            helper = new ExoPlayerViewHelper(this, mediaUri);
            ((ExoPlayerViewHelper) helper).addEventListener(listener);

        }
        helper.initialize(container, playbackInfo);
    }

    @Override
    public void play() {
        if (helper != null && this.mediaUri != null) {
            helper.play();
            mCover.setVisibility(View.GONE);
            if (playerView.getPlayer() != null) {

                if (GlobalFunc.isMute) {
                    tvIconSound.setText(itemView.getResources().getText(R.string.fa_volume_mute));
                    playerView.getPlayer().getAudioComponent().setVolume(0);
                } else {
                    tvIconSound.setText(itemView.getResources().getText(R.string.fa_volume_up));
                    playerView.getPlayer().getAudioComponent().setVolume(1);
                }

                if (playerView.getPlayer().getRepeatMode() != Player.REPEAT_MODE_ALL) {
                    playerView.getPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
                }

            }
        } else {
            mCover.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void pause() {
        if (helper != null) helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        mCover.setVisibility(View.VISIBLE);

        if (helper != null) {
            ((ExoPlayerViewHelper) helper).removeEventListener(listener);
            helper.release();
            helper = null;
        }
    }


    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    @Override
    public String toString() {
        return "ExoPlayer{" + hashCode() + " " + getAdapterPosition() + "}";
    }

    public void bind(DirectLinkItemTest directLinkItemTest) {
        if (directLinkItemTest.getmDirectUrl() != null) {
            this.mediaUri = Uri.parse(directLinkItemTest.getmDirectUrl());
            listener = new Playable.DefaultEventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == 2) {
                        loadingBarVideo.setVisibility(View.VISIBLE);
                    } else {
                        loadingBarVideo.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRenderedFirstFrame() {
                    super.onRenderedFirstFrame();
                    mCover.setVisibility(View.GONE);
                }
            };
        } else {
            this.mediaUri = null;
            loadingBarVideo.setVisibility(View.GONE);
            mCover.setVisibility(View.VISIBLE);
        }

    }
}