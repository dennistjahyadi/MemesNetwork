package com.dovoo.memesnetwork.fragments

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dovoo.memesnetwork.databinding.CustomToolbarHomeBinding
import com.dovoo.memesnetwork.databinding.FragmentAddMemeBinding


class AddMemeFragment:Fragment() {
    private var _binding: FragmentAddMemeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMemeBinding.inflate(inflater, container, false)

        binding.linAddImage.setOnClickListener {
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/* video/*"
            startActivityForResult(pickIntent, IMAGE_PICKER_SELECT)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val selectedMediaUri: Uri = data?.data!!
            if (selectedMediaUri.toString().contains("image")) {
                //handle image

            } else if (selectedMediaUri.toString().contains("video")) {
                //handle video

            }
        }
    }

    companion object {
        val IMAGE_PICKER_SELECT = 0x1242
    }
}