package com.dovoo.memesnetwork.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.databinding.FragmentMemesDetailMemesBinding
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlin.properties.Delegates


class MemesDetailMemesFragment : Fragment() {
    private var _binding: FragmentMemesDetailMemesBinding? = null
    private val binding get() = _binding!!
    var finalWidth by Delegates.notNull<Float>()
    var finalHeight by Delegates.notNull<Float>()
    val videoItem by lazy {
        arguments?.getParcelable<DirectLinkItemTest>("current_video_item")!!
    }
    var maxHeightVideo by Delegates.notNull<Float>()
    lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalWidth =
            requireContext().resources.displayMetrics.widthPixels.toFloat() // default phone width
        finalHeight =
            requireContext().resources.displayMetrics.heightPixels.toFloat() // default phone heights
        maxHeightVideo =
            requireContext().resources.displayMetrics.heightPixels.toFloat() * 0.8f // set default maximum video size in phone
        player = ExoPlayerFactory.newSimpleInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemesDetailMemesBinding.inflate(inflater, container, false)
        binding.playerView.player = player

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
        val layoutParams = binding.relativeLayout.layoutParams
        layoutParams.width = finalWidth.toInt()
        layoutParams.height = finalHeight.toInt()
        binding.relativeLayout.layoutParams = layoutParams
        binding.tvTitle.text = videoItem.getmTitle()

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
            binding.playerView.visibility = View.VISIBLE
            binding.cover.visibility = View.GONE
        } else {
            binding.playerView.visibility = View.GONE
            binding.cover.visibility = View.VISIBLE
            Glide.with(requireContext()).load(videoItem.getmCoverUrl()).into(binding.cover)
            PhotoViewAttacher(binding.cover)
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        player.stop(true)
    }

    companion object {
        fun newInstance(currentVideoItem: DirectLinkItemTest): MemesDetailMemesFragment {
            val args = Bundle()

            val fragment = MemesDetailMemesFragment()
            args.putParcelable("current_video_item", currentVideoItem)
            fragment.arguments = args
            return fragment
        }
    }
}