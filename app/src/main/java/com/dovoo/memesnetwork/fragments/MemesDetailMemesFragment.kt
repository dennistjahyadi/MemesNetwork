package com.dovoo.memesnetwork.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.BuildConfig
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.databinding.FragmentMemesDetailMemesBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.Utils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import java.io.File
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

    val generalViewModel: GeneralViewModel by viewModels()


    val likeOnClickListener = View.OnClickListener {
        if (!GlobalFunc.isLogin(requireContext())
        ) {
            findNavController().navigate(R.id.action_memesDetailFragment_to_loginFragment)
        } else {
            doLike(
                videoItem.id,
                videoItem,
                binding.tvBtnLike,
                binding.tvTotalLike,
                binding.linBtnLike
            )
        }
    }
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
        binding.tvBtnLike.setOnClickListener(likeOnClickListener)
        binding.linBtnShare.setOnClickListener(View.OnClickListener {
            if (!Utils.checkPermissionStorage(requireContext())
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please allow storage permission",
                    Toast.LENGTH_LONG
                )
                    .show()
                return@OnClickListener
            }
            binding.loading.loadingBar.visibility = View.VISIBLE
            binding.tvBtnShare.isEnabled = false

            val isVideo = videoItem.getmDirectUrl() != null
            var theUrl: String? = videoItem.getmCoverUrl()
            if (videoItem.getmDirectUrl() != null) {
                theUrl = videoItem.getmDirectUrl()
            }
            FileLoader.with(requireContext())
                .load(theUrl) //2nd parameter is optioal, pass true to force load from network
                .fromDirectory("memesnetwork", FileLoader.DIR_CACHE)
                .asFile(object : FileRequestListener<File?> {
                    override fun onLoad(request: FileLoadRequest, response: FileResponse<File?>) {
                        val loadedFile = response.body
                        val share = Intent(Intent.ACTION_SEND)
                        if (!isVideo) {
                            share.type = "image/jpg"
                        } else {
                            share.type = "video/*"
                        }
                        val uri: Uri
                        uri = if (Build.VERSION.SDK_INT <= 24) {
                            Uri.fromFile(loadedFile)
                        } else {
                            FileProvider.getUriForFile(
                                requireContext(),
                                BuildConfig.APPLICATION_ID + ".provider",
                                loadedFile!!
                            )
                        }
                        var shareMessage =
                            "\nWith MemesNetwork everything is laughable, download here\n\n"
                        shareMessage = """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                        share.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        share.putExtra(Intent.EXTRA_STREAM, uri)
                        share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION  or Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        startActivity(Intent.createChooser(share, "Share :"))

                        binding.tvBtnShare.isEnabled = true
                        binding.loading.loadingBar.visibility = View.GONE

                    }

                    override fun onError(request: FileLoadRequest, t: Throwable) {
                        binding.tvBtnShare.isEnabled = true
                        binding.loading.loadingBar.visibility = View.GONE

                        Toast.makeText(requireContext(), "Cannot sharing file", Toast.LENGTH_LONG)
                            .show()
                    }
                })
        })

        var isLiked = videoItem.isLiked

        if (isLiked == 1) {
            binding.tvBtnLike.setImageResource(R.drawable.ic_thumbs_up_active)
        } else {
            binding.tvBtnLike.setImageResource(R.drawable.ic_thumbs_up)
        }
        binding.tvTotalLike.text = (videoItem.totalLike).toString()

        return binding.root
    }

    private fun doLike(
        memeId: Int,
        data: DirectLinkItemTest,
        ivLike: ImageView,
        tvTotalLike: TextView,
        linBtnLike: LinearLayout
    ) {
        val userId =
            SharedPreferenceUtils.getPrefs(requireContext())
                .getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, -1)
        var isLiked = data.isLiked

        if (isLiked == 1) {
            isLiked = 0
            ivLike.setImageResource(R.drawable.ic_thumbs_up)
        } else {
            isLiked = 1
            ivLike.setImageResource(R.drawable.ic_thumbs_up_active)
        }
        linBtnLike.isEnabled = false
        data.isLiked = isLiked
        val totLike = data.totalLike
        data.totalLike = if(isLiked==1) totLike+1 else totLike-1
        tvTotalLike.text = (data.totalLike).toString()

        generalViewModel.insertLike(memeId, userId, isLiked).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    linBtnLike.isEnabled = true
                }
                Status.ERROR -> {
                    linBtnLike.isEnabled = true
                    if (isLiked == 1) {
                        ivLike.setImageResource(R.drawable.ic_thumbs_up)
                        data.isLiked = 0
                    } else {
                        ivLike.setImageResource(R.drawable.ic_thumbs_up_active)
                        data.isLiked = 1
                    }
                    val totLike2 = data.totalLike
                    data.totalLike = (totLike2 - 1)
                    tvTotalLike.text = (data.totalLike).toString()
                }
            }
        })
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