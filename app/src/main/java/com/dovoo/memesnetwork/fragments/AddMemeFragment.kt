package com.dovoo.memesnetwork.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.databinding.FragmentAddMemeBinding
import com.dovoo.memesnetwork.model.Memes
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import java.io.ByteArrayOutputStream


class AddMemeFragment : Fragment() {
    private var _binding: FragmentAddMemeBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMemeBinding.inflate(inflater, container, false)

        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.linAddImage.setOnClickListener {
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/* video/*"
            startActivityForResult(pickIntent, IMAGE_PICKER_SELECT)
        }

        return binding.root
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
        } else  if (videoUri != null) {
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
            val selectedMediaUri: Uri = data?.data!!
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
            }
        }
    }

    companion object {
        val IMAGE_PICKER_SELECT = 0x1242
    }
}