package com.dovoo.memesnetwork.adapter.holders

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.ads.AdView
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoPlayerDispatcher
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.exoplayer.Playable
import im.ene.toro.helper.ToroPlayerHelper
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import im.ene.toro.widget.PressablePlayerSelector

class MemesViewHolder(itemView: View, selector: PressablePlayerSelector?) :
    RecyclerView.ViewHolder(itemView), ToroPlayer {
    var helper: ToroPlayerHelper? = null
    var mediaUri: Uri? = null
    var playerView: PlayerView
    var relativeLayout: RelativeLayout
    var linBtnLike: LinearLayout
    var linBtnComment: LinearLayout
    var mCover: ImageView
    var tvCategory: TextView
    var ivIconSound: ImageView
    var tvTitle: TextView
    var tvUsername: TextView
    var ivProfilePic: ImageView
    var tvLabelNoAudio: TextView
    var ivBtnLike: ImageView
    var tvTotalLike: TextView
    var tvTotalComment: TextView
    var ivBtnShare: ImageView
    var layoutUser: ConstraintLayout
    var loadingBarVideo: SpinKitView
    var adView: AdView
    var listener: Playable.EventListener? = null
    lateinit var data: DirectLinkItemTest
    override fun getPlayerView(): View {
        return playerView
    }

    override fun getCurrentPlaybackInfo(): PlaybackInfo {
        return if (helper != null) helper!!.latestPlaybackInfo else PlaybackInfo()
    }

    override fun initialize(container: Container, playbackInfo: PlaybackInfo) {
        if (helper == null) {
            mediaUri?.let {
                helper = ExoPlayerViewHelper(this, it)
                (helper as ExoPlayerViewHelper).addEventListener(listener!!)
                helper!!.initialize(container, playbackInfo)
            }
        }
    }

    override fun play() {
        if (helper != null && mediaUri != null) {
            helper!!.play()
            mCover.visibility = View.GONE
            if (playerView.player != null) {
                if (GlobalFunc.isMute) {
                    ivIconSound.setImageResource(R.drawable.ic_volume_mute)
                    playerView.player.audioComponent!!.volume = 0f
                } else {
                    ivIconSound.setImageResource(R.drawable.ic_volume_up)
                    playerView.player.audioComponent!!.volume = 1f
                }
                if (playerView.player.repeatMode != Player.REPEAT_MODE_ALL) {
                    playerView.player.repeatMode = Player.REPEAT_MODE_ALL
                }
            }
        } else {
            mCover.visibility = View.VISIBLE
        }
    }

    override fun pause() {
        if (helper != null) helper!!.pause()
    }

    override fun isPlaying(): Boolean {
        return helper != null && helper!!.isPlaying
    }

    override fun release() {
        mCover.visibility = View.VISIBLE
        if (helper != null) {
            (helper as ExoPlayerViewHelper).removeEventListener(listener)
            helper?.release()
            helper = null
        }
    }

    override fun wantsToPlay(): Boolean {
        return ToroUtil.visibleAreaOffset(this, itemView.parent) >= 0.85
    }

    override fun getPlayerOrder(): Int {
        return adapterPosition
    }

    override fun toString(): String {
        return "ExoPlayer{" + hashCode() + " " + adapterPosition + "}"
    }

    fun bind(directLinkItemTest: DirectLinkItemTest) {
        if (directLinkItemTest.getmDirectUrl() != null) {
            mediaUri = Uri.parse(directLinkItemTest.getmDirectUrl())
            listener = object : Playable.DefaultEventListener() {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    if (playbackState == 2) {
                        loadingBarVideo.visibility = View.VISIBLE
                    } else {
                        loadingBarVideo.visibility = View.GONE
                    }
                }

                override fun onRenderedFirstFrame() {
                    super.onRenderedFirstFrame()
                    mCover.visibility = View.GONE
                }
            }
        } else {
            mediaUri = null
            loadingBarVideo.visibility = View.GONE
            mCover.visibility = View.VISIBLE
        }
    }

    companion object {
        const val LAYOUT_RES = R.layout.row_meme
    }

    init {
        playerView = itemView.findViewById(R.id.playerView)
        relativeLayout = itemView.findViewById(R.id.relativeLayout)
        tvTotalLike = itemView.findViewById(R.id.tvTotalLike)
        tvTotalComment = itemView.findViewById(R.id.tvTotalComment)
        ivBtnLike = itemView.findViewById(R.id.tvBtnLike)
        loadingBarVideo = itemView.findViewById(R.id.loadingBarVideo)
        tvUsername = itemView.findViewById(R.id.tvUsername)
        layoutUser = itemView.findViewById(R.id.layout_user)
        ivProfilePic = itemView.findViewById(R.id.ivProfilePic)
        linBtnLike = itemView.findViewById(R.id.linBtnLike)
        linBtnLike.tag = this
        linBtnComment = itemView.findViewById(R.id.linBtnComment)
        linBtnComment.tag = this
        ivBtnShare = itemView.findViewById(R.id.tvBtnShare)
        tvTitle = itemView.findViewById(R.id.tvTitle)
        tvTitle.tag = this
        tvCategory = itemView.findViewById(R.id.tvCategory)
        tvLabelNoAudio = itemView.findViewById(R.id.tvLabelNoAudio)
        mCover = itemView.findViewById(R.id.cover)
        ivIconSound = itemView.findViewById(R.id.ivIconSound)
        tvUsername.tag = this
        ivProfilePic.tag = this
        adView = itemView.findViewById(R.id.adView)
        playerView.setOnTouchListener(OnTouchListener { _, event ->
            if (playerView.player == null) {
                return@OnTouchListener false
            }
            playerView.performClick()
            if (event.action == MotionEvent.ACTION_UP) {
                Handler(Looper.getMainLooper()).post {
                    if (GlobalFunc.isMute) {
                        playerView.player.audioComponent!!.volume = 1f
                        ivIconSound.setImageResource(R.drawable.ic_volume_up)
                        GlobalFunc.isMute = false
                    } else {
                        playerView.player.audioComponent!!.volume = 0f
                        ivIconSound.setImageResource(R.drawable.ic_volume_mute)

                        GlobalFunc.isMute = true
                    }
                }
            }
            true
        })
        if (selector != null) playerView.setControlDispatcher(ExoPlayerDispatcher(selector, this))
    }
}