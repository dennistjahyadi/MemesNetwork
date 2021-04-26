package com.dovoo.memesnetwork.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentProfileBinding
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.android.gms.tasks.OnCompleteListener
import java.io.ByteArrayOutputStream
import java.util.*


class ProfileFragment : Fragment() {

    private val RESULT_LOAD_IMG = 0X123
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        checkUsername()
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvBtnLogout.setOnClickListener {
            doGoogleLogout()
        }
        binding.ivProfilepicEdit.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
        }

        return binding.root
    }

    private fun checkUsername(){
        val username = SharedPreferenceUtils.getPrefs(requireContext()).getString(
            SharedPreferenceUtils.PREFERENCES_USER_NAME,
            null
        )
        if(username.isNullOrEmpty()){
            findNavController().navigate(R.id.action_profileFragment_to_insertUsernameFragment)
        }
    }

    private fun doGoogleLogout() {

        (activity as DefaultActivity).mGoogleSignInClient.signOut()
            .addOnCompleteListener(activity as DefaultActivity, OnCompleteListener<Void?> {
                if (it.isComplete) {
                    showDialogLogout()
                }
            })
    }

    fun showDialogLogout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.logout_msg))
            .setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                SharedPreferenceUtils.removeUserPrefs(requireContext())
                findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    fun uploadProfilePic(bitmap: Bitmap){
        // Create a storage reference from our app
        val storageRef = (activity as DefaultActivity).storage.reference

        val profilePicRef = storageRef.child("profilepicture/" + UUID.randomUUID())

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

        var uploadTask = profilePicRef.putBytes(baos.toByteArray())

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            profilePicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                updateProfilePicDb(downloadUri.toString())
            } else {
                // Handle failures
                // ...
            }
        }
    }

    private fun updateProfilePicDb(url:String){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK){

            val myOptions = RequestOptions()
                .fitCenter() // or centerCrop
                .override(800, 800)

            Glide.with(requireContext())
                .asBitmap()
                .apply(myOptions)
                .load(data?.data)
                .into(object: CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        uploadProfilePic(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }

                })


        }
    }
}