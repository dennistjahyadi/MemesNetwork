package com.dovoo.memesnetwork.adapter.items;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dovoo.memesnetwork.R;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class DirectLinkItemTest {
    private String mDirectUrl;
    private String mTitle;
    private Picasso mImageLoader;
    private String mCoverUrl, mCategory;
    private Integer id;
    private boolean hasAudio = false;
    private boolean isVideo = true;
    private Map<String, Object> data;

    private final Rect mCurrentViewRect = new Rect();
    private Integer mWidth;
    private Integer mHeight;

    public DirectLinkItemTest(Integer id, String category, String title, String directUrl, Map<String,Object> data, Picasso imageLoader, String coverUrl, Integer width, Integer height, boolean hasAudio, boolean isVideo) {
        this.id = id;
        this.mWidth = width;
        this.mHeight = height;
        this.mDirectUrl = directUrl;
        this.mTitle = title;
        this.mImageLoader = imageLoader;
        this.mCoverUrl = coverUrl;
        this.hasAudio = hasAudio;
        this.isVideo = isVideo;
        this.mCategory = category;
        this.data = data;
    }


    public String getmDirectUrl() {
        return mDirectUrl;
    }

    public void setmDirectUrl(String mDirectUrl) {
        this.mDirectUrl = mDirectUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Picasso getmImageLoader() {
        return mImageLoader;
    }

    public void setmImageLoader(Picasso mImageLoader) {
        this.mImageLoader = mImageLoader;
    }

    public String getmCoverUrl() {
        return mCoverUrl;
    }

    public void setmCoverUrl(String mCoverUrl) {
        this.mCoverUrl = mCoverUrl;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Rect getmCurrentViewRect() {
        return mCurrentViewRect;
    }

    public Integer getmWidth() {
        return mWidth;
    }

    public void setmWidth(Integer mWidth) {
        this.mWidth = mWidth;
    }

    public Integer getmHeight() {
        return mHeight;
    }

    public void setmHeight(Integer mHeight) {
        this.mHeight = mHeight;
    }
    public View createView(final ViewGroup parent, int screenWidth, int screenHeight) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_meme, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        //layoutParams.height = screenHeight*80/100;

        /*
        final VideoViewHolder videoViewHolder = new VideoViewHolder(view);
        view.setTag(videoViewHolder);

        videoViewHolder.mPlayer.addMediaPlayerListener(new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
            @Override
            public void onVideoSizeChangedMainThread(int width, int height) {
            }

            @Override
            public void onVideoPreparedMainThread() {
                // When video is prepared it's about to start playback. So we hide the cover
                videoViewHolder.mCover.setVisibility(View.INVISIBLE);

                if (videoViewHolder.mPlayer.isAllVideoMute()) {
                    // sound is muted
                    videoViewHolder.tvIconSound.setText(parent.getContext().getResources().getText(R.string.fa_volume_mute));
                } else {
                    // sound is on
                    videoViewHolder.tvIconSound.setText(parent.getContext().getResources().getText(R.string.fa_volume_up));
                }
            }

            @Override
            public void onVideoCompletionMainThread() {
            }

            @Override
            public void onErrorMainThread(int what, int extra) {
                Log.v("Error State","errorr");
                mVideoPlayerManager.resetMediaPlayer();
            }

            @Override
            public void onBufferingUpdateMainThread(int percent) {
            }

            @Override
            public void onVideoStoppedMainThread() {
                // Show the cover when video stopped
                videoViewHolder.mCover.setVisibility(View.VISIBLE);
            }
        });
        */
        return view;
    }
}
