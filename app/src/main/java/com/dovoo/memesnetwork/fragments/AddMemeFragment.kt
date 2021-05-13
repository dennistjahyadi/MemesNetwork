package com.dovoo.memesnetwork.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoHelper.getMediaPath
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentAddMemeBinding
import com.dovoo.memesnetwork.model.Memes
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.github.javiersantos.bottomdialogs.BottomDialog
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*


class AddMemeFragment : Fragment() {
    private var _binding: FragmentAddMemeBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    lateinit var player: SimpleExoPlayer

    var selectedMediaUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayerFactory.newSimpleInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMemeBinding.inflate(inflater, container, false)
        binding.playerView.player = player

        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.linAddImage.setOnClickListener {
            showUploadDialog()
        }

        binding.linBtnOk.setOnClickListener {
            if (selectedMediaUri.toString().contains("image")) {
                //handle image
                showLoadingUpload(true)
                val myOptions = RequestOptions()
                    .fitCenter() // or centerCrop
                    .override(1600, 1600)

                Glide.with(requireContext())
                    .asBitmap()
                    .apply(myOptions)
                    .load(selectedMediaUri)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            uploadMemes(resource, null)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                    })

            } else if (selectedMediaUri.toString().contains("video")) {
                //handle video
                showLoadingUpload(true)
                uploadMemes(null, selectedMediaUri)
            } else {
                Toast.makeText(requireContext(), "Please select the content", Toast.LENGTH_LONG)
                    .show()
            }
        }

        return binding.root
    }

    private fun showUploadDialog() {
        val customView =
            LayoutInflater.from(requireContext()).inflate(R.layout.view_select_meme_dialog, null);

        val linBtnImage: LinearLayout = customView.findViewById(R.id.linBtnImage)
        val linBtnVideo: LinearLayout = customView.findViewById(R.id.linBtnVideo)
        linBtnImage.setOnClickListener {
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            startActivityForResult(pickIntent, IMAGE_PICKER_SELECT)
        }
        linBtnVideo.setOnClickListener {
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/*"
            startActivityForResult(pickIntent, IMAGE_PICKER_SELECT)
        }
        val bottomDialog = BottomDialog.Builder(requireContext())
            .setCustomView(customView)
        bottomDialog.show()
    }

    fun uploadMemes(bitmap: Bitmap?, videoUri: Uri?) {
        // Create a storage reference from our app
        val storageRef = (activity as DefaultActivity).storage.reference
        val userId = GlobalFunc.getLoggedInUserId(requireContext())
        val memesRef = storageRef.child("memes/${userId}/${System.currentTimeMillis()}")
        lateinit var uploadTask: UploadTask
        if (bitmap != null) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            uploadTask = memesRef.putBytes(baos.toByteArray())
        } else if (videoUri != null) {
            uploadTask = memesRef.putFile(videoUri!!)
        }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
                showLoadingUpload(false)
            }
            memesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                if (bitmap != null) {
                    saveMemesDb(bitmap.width, bitmap.height, downloadUri.toString(), true)

                } else if (videoUri != null) {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(requireContext(), videoUri)
                    val videoWidth =
                        Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!)
                    val videoHeight =
                        Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!)
                    retriever.release()
                    saveMemesDb(videoWidth, videoHeight, downloadUri.toString(), false)
                }

            } else {
                showLoadingUpload(false)
            }
        }
    }

    fun saveMemesDb(width: Int, height: Int, memeUrl: String, isPhoto: Boolean) {
        val userId = GlobalFunc.getLoggedInUserId(requireContext())
        val desc = binding.etDesc.text.toString()
        val postSection = binding.etSection.text.toString()
        val image700 = Memes.Image700(width, height, memeUrl, memeUrl)
        val image460sv = Memes.Image460sv(width, height, memeUrl, memeUrl, 1, 0)
        val data = Memes.MemesImage(image700, image460sv)

        generalViewModel.insertMemes(userId, desc, isPhoto, postSection, Gson().toJson(data))
    }

    private fun showLoadingUpload(show: Boolean) {
        binding.loadingUpload.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            selectedMediaUri = data?.data!!
            showPreview()

        }
    }

    private fun showPreview() {
        if (selectedMediaUri.toString().contains("image")) {
            //handle image
            val myOptions = RequestOptions()
                .fitCenter() // or centerCrop
                .override(1600, 1600)

            Glide.with(requireContext())
                .asBitmap()
                .apply(myOptions)
                .load(selectedMediaUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        Glide.with(requireContext()).load(resource).into(binding.ivPreview)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })
            binding.playerView.visibility = View.GONE
            binding.ivPreview.visibility = View.VISIBLE
        } else if (selectedMediaUri.toString().contains("video")) {
            //handle video
            GlobalScope.launch {
                // run in background as it
                var path = ""
                val job = async { getMediaPath(requireContext(), selectedMediaUri!!) }
                path = job.await()
                val desFile = saveVideoFile(path)
                VideoCompressor.start(
                    context = requireContext(), // => This is required if srcUri is provided. If not, it can be ignored or null.
                    srcUri = selectedMediaUri, // => Source can be provided as content uri, it requires context.
                    srcPath = null, // => This could be ignored or null if srcUri and context are provided.
                    destPath = desFile!!.path,
                    listener = object : CompressionListener {
                        override fun onProgress(percent: Float) {
                            // Update UI with progress value

                        }

                        override fun onStart() {
                            // Compression start
                        }

                        override fun onSuccess() {
                            // On Compression success
                            val newSizeValue = desFile.length()

                            println("Size after compression: ${newSizeValue}")
                            Glide.with(requireContext()).load(desFile).into(binding.ivPreview)
                            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                                context,
                                Util.getUserAgent(context, "yourApplicationName")
                            )
                            val compressedVideo = Uri.fromFile(desFile)
                            val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(compressedVideo)

// Prepare the player with the source.
                            player.prepare(videoSource)
                            player.playWhenReady = true
                            binding.playerView.visibility = View.VISIBLE
                            binding.ivPreview.visibility = View.GONE

                        }

                        override fun onFailure(failureMessage: String) {
                            // On Failure
                        }

                        override fun onCancelled() {
                            // On Cancelled
                        }

                    }, quality = VideoQuality.MEDIUM,
                    isMinBitRateEnabled = false,
                    keepOriginalResolution = false
                )

            }
        }
    }

    private fun saveVideoFile(filePath: String?): File? {
        filePath?.let {
            val videoFile = File(filePath)
            val videoFileName = "${System.currentTimeMillis()}_${videoFile.name}"
            val folderName = Environment.DIRECTORY_MOVIES
            if (Build.VERSION.SDK_INT >= 30) {

                val values = ContentValues().apply {

                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        videoFileName
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Images.Media.RELATIVE_PATH, folderName)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val collection =
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val fileUri = requireContext().contentResolver.insert(collection, values)

                fileUri?.let {
                    requireContext().contentResolver.openFileDescriptor(fileUri, "rw")
                        .use { descriptor ->
                            descriptor?.let {
                                FileOutputStream(descriptor.fileDescriptor).use { out ->
                                    FileInputStream(videoFile).use { inputStream ->
                                        val buf = ByteArray(4096)
                                        while (true) {
                                            val sz = inputStream.read(buf)
                                            if (sz <= 0) break
                                            out.write(buf, 0, sz)
                                        }
                                    }
                                }
                            }
                        }

                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    requireContext().contentResolver.update(fileUri, values, null, null)

                    return File(getMediaPath(requireContext(), fileUri))
                }
            } else {

                val downloadsPath = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val desFile = File(downloadsPath, videoFileName)

                if (desFile.exists())
                    desFile.delete()

                try {
                    desFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return desFile
            }
        }
        return null
    }

    override fun onStop() {
        super.onStop()
        player.stop(true)
    }

    override fun onPause() {
        super.onPause()
        player.stop(true)
    }

    companion object {
        val IMAGE_PICKER_SELECT = 0x1242
    }
}