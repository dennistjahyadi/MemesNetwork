package com.dovoo.memesnetwork.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentProfileBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {

    private val RESULT_LOAD_IMG = 0X123
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val listFragment: ArrayList<Fragment> = ArrayList()
    private lateinit var profileViewPagerAdapter: ProfileViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragments()
        profileViewPagerAdapter = ProfileViewPagerAdapter(this, listFragment)
    }

    private fun initFragments() {
        listFragment.add(MyMemesFragment())
        listFragment.add(LikedMemesFragment())
        listFragment.add(MyCommentsFragment())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        checkUsername()

        binding.viewPager.adapter = profileViewPagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.my_memes)
                1 -> tab.text = getString(R.string.liked)
                2 -> tab.text = getString(R.string.comments)
                else -> tab.text = getString(R.string.unknown)
            }
        }.attach()

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
        val photoUrl = SharedPreferenceUtils.getPrefs(requireContext()).getString(
            SharedPreferenceUtils.PREFERENCES_USER_PHOTO_URL,
            null
        )
        Glide.with(requireContext())
            .load(photoUrl)
            .into(binding.ivProfile)

        return binding.root
    }

    private fun checkUsername() {
        val username = SharedPreferenceUtils.getPrefs(requireContext()).getString(
            SharedPreferenceUtils.PREFERENCES_USER_NAME,
            null
        )
        if (username.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_profileFragment_to_insertUsernameFragment)
        } else binding.tvUsername.text = username
    }

    private fun doGoogleLogout() {
        (activity as DefaultActivity).mGoogleSignInClient.signOut()
            .addOnCompleteListener(activity as DefaultActivity) {
                if (it.isComplete) {
                    showDialogLogout()
                }
            }
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

    fun uploadProfilePic(bitmap: Bitmap) {
        // Create a storage reference from our app
        val storageRef = (activity as DefaultActivity).storage.reference
        val userId = GlobalFunc.getLoggedInUserId(requireContext())
        val profilePicRef = storageRef.child("profilepicture/${userId}")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

        var uploadTask = profilePicRef.putBytes(baos.toByteArray())

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
                showLoadingUpload(false)
            }
            profilePicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                updateProfilePicDb(downloadUri.toString())
            } else {
                showLoadingUpload(false)
            }
        }
    }

    private fun updateProfilePicDb(url: String) {
        generalViewModel.updateProfilePic(GlobalFunc.getLoggedInUserId(requireContext()), url)
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        SharedPreferenceUtils.setPrefs(
                            requireContext(),
                            SharedPreferenceUtils.PREFERENCES_USER_PHOTO_URL,
                            it.data?.user?.photo_url
                        )

                        Glide.with(requireContext())
                            .load(it.data?.user?.photo_url)
                            .into(binding.ivProfile)
                        showLoadingUpload(false)
                    }
                    Status.ERROR -> {
                        showLoadingUpload(false)
                    }
                }
            })
    }

    private fun showLoadingUpload(show: Boolean) {
        binding.loadingUpload.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK) {
            showLoadingUpload(true)
            val myOptions = RequestOptions()
                .fitCenter() // or centerCrop
                .override(800, 800)

            Glide.with(requireContext())
                .asBitmap()
                .apply(myOptions)
                .load(data?.data)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        uploadProfilePic(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })


        }
    }

    class ProfileViewPagerAdapter(fragment: Fragment, val listFragment: ArrayList<Fragment>) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = listFragment.size

        override fun createFragment(position: Int): Fragment {
            return listFragment[position]
        }

    }

}