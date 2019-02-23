package com.example.acer.memesnetwork.adapter.items;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.acer.memesnetwork.R;
import com.example.acer.memesnetwork.adapter.holders.VideoViewHolder;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Use this class if you have direct path to the video source
 */
public class DirectLinkVideoItem extends BaseVideoItem {

    private String mDirectUrl;
    private String mTitle;
    private Picasso mImageLoader;
    private String mCoverUrl;
    private boolean hasAudio = false;
    private boolean isVideo = true;


    public DirectLinkVideoItem(String title, String directUrl, VideoPlayerManager videoPlayerManager, Picasso imageLoader, String coverUrl, Integer width, Integer height) {
        super(videoPlayerManager, width, height);
        this.mDirectUrl = directUrl;
        this.mTitle = title;
        this.mImageLoader = imageLoader;
        this.mCoverUrl = coverUrl;

    }

    public DirectLinkVideoItem(String title, String directUrl, VideoPlayerManager videoPlayerManager, Picasso imageLoader, String coverUrl, Integer width, Integer height, boolean hasAudio, boolean isVideo) {
        super(videoPlayerManager, width, height);
        this.mDirectUrl = directUrl;
        this.mTitle = title;
        this.mImageLoader = imageLoader;
        this.mCoverUrl = coverUrl;
        this.hasAudio = hasAudio;
        this.isVideo = isVideo;
    }

    public DirectLinkVideoItem(VideoPlayerManager videoPlayerManager, Picasso imageLoader, JSONObject data) {
        super(videoPlayerManager);
        try {
            this.mTitle = data.getString("title");
            JSONObject imagesObject = new JSONObject(data.getString("images"));
            this.mCoverUrl = imagesObject.getJSONObject("image700").getString("url");
            this.mDirectUrl = imagesObject.getJSONObject("image460sv").getString("url");
            int width = imagesObject.getJSONObject("image700").getInt("width");
            int height = imagesObject.getJSONObject("image700").getInt("height");
            this.setContentWidth(width);
            this.setContentHeight(height);
            this.mImageLoader = imageLoader;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(int position, VideoViewHolder viewHolder, VideoPlayerManager videoPlayerManager) {
        viewHolder.tvTitle.setText(mTitle);
        viewHolder.mCover.setVisibility(View.VISIBLE);
        mImageLoader.load(mCoverUrl).into(viewHolder.mCover);
        if (isVideo && mDirectUrl != null) {
            // if video = set audio visibility true
            if (hasAudio) {
                viewHolder.mPlayer.setHasAudio(true);
                viewHolder.tvIconSound.setVisibility(View.VISIBLE);
                viewHolder.tvLabelNoAudio.setVisibility(View.GONE);
                if (viewHolder.mPlayer.isAllVideoMute()) {
                    viewHolder.tvIconSound.setText(viewHolder.itemView.getContext().getResources().getText(R.string.fa_volume_mute));
                } else {
                    viewHolder.tvIconSound.setText(viewHolder.itemView.getContext().getResources().getText(R.string.fa_volume_up));
                }

            } else {
                viewHolder.mPlayer.setHasAudio(false);
                viewHolder.tvIconSound.setVisibility(View.GONE);
                viewHolder.tvLabelNoAudio.setVisibility(View.VISIBLE);
            }

        } else {
            // picture = set audio visibility false
            viewHolder.tvIconSound.setVisibility(View.GONE);
            viewHolder.tvLabelNoAudio.setVisibility(View.GONE);
        }
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, View view, VideoPlayerManager<MetaData> videoPlayerManager) {
        if (isVideo && mDirectUrl != null) {
            VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
            VideoPlayerView player = viewHolder.mPlayer;
            videoPlayerManager.playNewVideo(currentItemMetaData, player, mDirectUrl);
        }
    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }
}
