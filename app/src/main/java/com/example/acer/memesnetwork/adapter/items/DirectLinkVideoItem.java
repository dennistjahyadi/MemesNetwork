package com.example.acer.memesnetwork.adapter.items;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.acer.memesnetwork.adapter.holders.VideoViewHolder;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

/**
 * Use this class if you have direct path to the video source
 */
public class DirectLinkVideoItem extends BaseVideoItem {

    private final String mDirectUrl;
    private final String mTitle;
    private final Picasso mImageLoader;
    private final String mCoverUrl;


    public DirectLinkVideoItem(String title, String directUr, VideoPlayerManager videoPlayerManager, Picasso imageLoader, String coverUrl, Integer width, Integer height) {
        super(videoPlayerManager, width, height);
        mDirectUrl = directUr;
        mTitle = title;
        mImageLoader = imageLoader;
        mCoverUrl = coverUrl;
    }

    @Override
    public void update(int position, VideoViewHolder viewHolder, VideoPlayerManager videoPlayerManager) {
        viewHolder.tvTitle.setText(mTitle);
        viewHolder.mCover.setVisibility(View.VISIBLE);
        mImageLoader.load(mCoverUrl).into(viewHolder.mCover);


    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mDirectUrl);
    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }
}
