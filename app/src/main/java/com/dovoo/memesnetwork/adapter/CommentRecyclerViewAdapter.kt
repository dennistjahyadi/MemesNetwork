package com.dovoo.memesnetwork.adapter

import android.content.Context
import android.net.Uri
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.model.Comment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommentRecyclerViewAdapter(
    private val context: Context,
    private val itemList: ArrayList<Comment>,
    private val videoItem: DirectLinkItemTest
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @JvmField
    var player: SimpleExoPlayer
    var finalWidth: Float
    var finalHeight: Float
    var maxHeightVideo: Float
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val funnyimgs =
        intArrayOf(R.drawable.funny_user1, R.drawable.funny_user2, R.drawable.funny_user3)

    internal inner class MyViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {
        val relativeLayout: RelativeLayout
        val playerView: PlayerView
        val mCover: ImageView
        val tvTitle: TextView

        init {
            relativeLayout = view.findViewById(R.id.relativeLayout)
            tvTitle = view.findViewById(R.id.tvTitle)
            playerView = view.findViewById(R.id.playerView)
            mCover = view.findViewById(R.id.cover)
            playerView.player = player
        }
    }

    internal inner class MyViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView
        var tvComment: TextView
        var tvCreatedDate: TextView
        var ivPicture: ImageView
        var paddingBottom: FrameLayout

        init {
            tvUsername = itemView.findViewById(R.id.tvUsername)
            tvComment = itemView.findViewById(R.id.tvComment)
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate)
            ivPicture = itemView.findViewById(R.id.ivPicture)
            paddingBottom = itemView.findViewById(R.id.paddingBottom)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_header_comment, parent, false)
            return MyViewHolderHeader(view)
        } else if (viewType == TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_comment, parent, false)
            return MyViewHolderItem(view)
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolderHeader) {
            val vhHeader = holder
            if (videoItem.getmHeight() > videoItem.getmWidth()) {
                // if video is potrait
                val ratio = videoItem.getmHeight().toFloat() / videoItem.getmWidth()
                finalHeight = finalWidth * ratio
                // if final height higher or same with phone height, we have to decrease it to make the video fit in phone. It will show around 3/4 phone screen
                if (finalHeight >= maxHeightVideo) {
                    finalHeight = maxHeightVideo * 0.7f
                }
            } else if (videoItem.getmHeight() < videoItem.getmWidth()) {
                // if video is landscape
                val ratio = videoItem.getmWidth().toFloat() / videoItem.getmHeight()
                finalHeight = finalWidth / ratio
            } else {
                // if video is square
                finalHeight = finalWidth
            }
            val layoutParams = vhHeader.relativeLayout.layoutParams
            layoutParams.width = finalWidth.toInt()
            layoutParams.height = finalHeight.toInt()
            vhHeader.relativeLayout.layoutParams = layoutParams
            vhHeader.tvTitle.text = videoItem.getmTitle()
            //            vhHeader.mCover.setVisibility(View.VISIBLE);
//
//            Picasso.get().load(directLinkVideoItem.getmCoverUrl())
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .resize(0, vhHeader.mCover.getHeight())
//                    .into(vhHeader.mCover);
// Produces DataSource instances through which media data is loaded.
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "yourApplicationName")
            )
            // This is the MediaSource representing the media to be played.
            if (videoItem.getmDirectUrl() != null) {
                val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoItem.getmDirectUrl()))

// Prepare the player with the source.
                player.prepare(videoSource)
                player.playWhenReady = true
                vhHeader.mCover.visibility = View.GONE
            } else {
                vhHeader.playerView.visibility = View.GONE
                vhHeader.mCover.visibility = View.VISIBLE
                videoItem.getmImageLoader().load(videoItem.getmCoverUrl()).into(vhHeader.mCover)
            }
        } else if (holder is MyViewHolderItem) {
            val obj = itemList[position - 1]
            val vhItem = holder
            if(position < itemList.size){
                vhItem.paddingBottom.visibility = View.GONE
            }else{
                vhItem.paddingBottom.visibility = View.VISIBLE
            }

            //vhItem.tvUsername.setText((String)obj.get("created_by"));
           // vhItem.tvUsername.text = obj["created_by"] as String?
            vhItem.tvComment.text = obj.messages
            Picasso.get().load(funnyimgs[Random().nextInt(3)]).into(vhItem.ivPicture)
            try {
                if (!obj.created_at.isNullOrEmpty()) {
                    val createdAtDate = sdf.parse(obj.created_at)
                    val currentDate = sdf.parse(obj.current_datetime)
                    val createAtMiliseconds = createdAtDate.time
                    val currentTimeMiliseconds = currentDate.time
                    val thedate = DateUtils.getRelativeTimeSpanString(
                        createAtMiliseconds,
                        currentTimeMiliseconds,
                        DateUtils.MINUTE_IN_MILLIS
                    )
                    vhItem.tvCreatedDate.text = thedate
                } else {
                    vhItem.tvCreatedDate.text = "null"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == TYPE_HEADER
    }

    override fun getItemCount(): Int {
        return itemList.size + 1
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    init {
        finalWidth = context.resources.displayMetrics.widthPixels.toFloat() // default phone width
        finalHeight =
            context.resources.displayMetrics.heightPixels.toFloat() // default phone heights
        maxHeightVideo =
            context.resources.displayMetrics.heightPixels.toFloat() * 0.8f // set default maximum video size in phone
        player = ExoPlayerFactory.newSimpleInstance(context)
    }
}