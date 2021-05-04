package com.dovoo.memesnetwork.adapter.items

import android.graphics.Rect
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.model.Memes
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize

@Parcelize
class DirectLinkItemTest(
    var id: Int,
    var mCategory: String,
    var mTitle: String,
    var mDirectUrl: String?,
    var mCoverUrl: String,
    var mWidth: Int,
    var mHeight: Int,
    var isHasAudio: Boolean = false,
    var isVideo: Boolean = true,
    var totalLike: Int = 0,
    var totalComment: Int = 0,
    var isLiked: Int?
) : Parcelable {
    private val mCurrentViewRect = Rect()

    constructor(meme: Memes) : this(
        meme.id,
        meme.post_section,
        meme.title,
        meme.images.image460sv?.url,
        meme.images.image700.url,
        meme.images.image700.width,
        meme.images.image700.height,
        meme.hasAudio(),
        meme.isVideo(),
        meme.total_like,
        meme.total_comment,
        meme.is_liked
    )

    fun getmDirectUrl(): String? {
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
        return Picasso.get()
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