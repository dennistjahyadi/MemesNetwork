package com.dovoo.memesnetwork.adapter.items

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.model.Memes
import com.squareup.picasso.Picasso
import java.util.HashMap

class DirectLinkItemTest {
    private var mDirectUrl: String
    private var mTitle: String
    private var mImageLoader: Picasso
    private var mCoverUrl: String
    private var mCategory: String
    var id: Int
    var isHasAudio = false
    var isVideo = true
    var data: Map<String, Any>? = null
    private val mCurrentViewRect = Rect()
    private var mWidth: Int
    private var mHeight: Int

    constructor(
        id: Int,
        category: String,
        title: String,
        directUrl: String,
        data: Map<String, Any>?,
        imageLoader: Picasso,
        coverUrl: String,
        width: Int,
        height: Int,
        hasAudio: Boolean,
        isVideo: Boolean
    ) {
        this.id = id
        mWidth = width
        mHeight = height
        mDirectUrl = directUrl
        mTitle = title
        mImageLoader = imageLoader
        mCoverUrl = coverUrl
        isHasAudio = hasAudio
        this.isVideo = isVideo
        mCategory = category
        this.data = data
    }

    constructor(meme: Memes, imageLoader: Picasso) {
        var isVideo = false
        var hasAudio = false
        if (meme.type.equals("animated", ignoreCase = true)) {
            isVideo = true
            hasAudio = meme.images.image460sv.hasAudio == 1
        }
        val mData: MutableMap<String, Any> = HashMap()
        mData["total_like"] = meme.total_like
        mData["total_comment"] = meme.total_comment
        mData["is_liked"] = meme.is_liked?:0
        id = meme.id
        mWidth = meme.images.image700.width
        mHeight = meme.images.image700.height
        mDirectUrl = meme.images.image460sv.url
        mTitle = meme.title
        mImageLoader = imageLoader
        mCoverUrl = meme.images.image700.url
        mCategory = meme.post_section
        isHasAudio = hasAudio;
        this.isVideo = isVideo;
        this.data = mData
    }

    fun getmDirectUrl(): String {
        return mDirectUrl
    }

    fun setmDirectUrl(mDirectUrl: String) {
        this.mDirectUrl = mDirectUrl
    }

    fun getmTitle(): String {
        return mTitle
    }

    fun setmTitle(mTitle: String) {
        this.mTitle = mTitle
    }

    fun getmImageLoader(): Picasso {
        return mImageLoader
    }

    fun setmImageLoader(mImageLoader: Picasso) {
        this.mImageLoader = mImageLoader
    }

    fun getmCoverUrl(): String {
        return mCoverUrl
    }

    fun setmCoverUrl(mCoverUrl: String) {
        this.mCoverUrl = mCoverUrl
    }

    fun getmCategory(): String {
        return mCategory
    }

    fun setmCategory(mCategory: String) {
        this.mCategory = mCategory
    }

    fun getmCurrentViewRect(): Rect {
        return mCurrentViewRect
    }

    fun getmWidth(): Int {
        return mWidth
    }

    fun setmWidth(mWidth: Int) {
        this.mWidth = mWidth
    }

    fun getmHeight(): Int {
        return mHeight
    }

    fun setmHeight(mHeight: Int) {
        this.mHeight = mHeight
    }

    fun createView(parent: ViewGroup, screenWidth: Int, screenHeight: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_meme, parent, false)
        val layoutParams = view.layoutParams
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
        */return view
    }
}