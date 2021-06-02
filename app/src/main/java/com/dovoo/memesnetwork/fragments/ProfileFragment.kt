package com.dovoo.memesnetwork.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
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


class ProfileFragment : Fragment(), View.OnClickListener {

    private val RESULT_LOAD_IMG = 0X123
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val listFragment: ArrayList<Fragment> = ArrayList()
    private lateinit var profileViewPagerAdapter: ProfileViewPagerAdapter
    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback
    var lastPageIndex = 0
    private var currentPagePosition = 0

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
        if(!GlobalFunc.isLogin(requireContext())) findNavController().popBackStack()
        else checkUsername()

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
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPagePosition = position
            }
        })
        binding.lblFollowing.setOnClickListener(this)
        binding.lblFollowers.setOnClickListener(this)
        binding.tvFollowing.setOnClickListener(this)
        binding.tvFollowers.setOnClickListener(this)
        binding.tvUsername.setOnClickListener(this)
        binding.ivUsernameEdit.setOnClickListener(this)
        binding.ivProfile.setOnClickListener(this)
        binding.ivProfilepicEdit.setOnClickListener(this)

        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivSettings.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        val photoUrl = SharedPreferenceUtils.getPrefs(requireContext()).getString(
            SharedPreferenceUtils.PREFERENCES_USER_PHOTO_URL,
            null
        )

        if (photoUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(R.drawable.funny_user2)
                .into(binding.ivProfile)
        } else {
            Glide.with(requireContext())
                .load(photoUrl)
                .into(binding.ivProfile)
        }
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

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.lbl_following, R.id.tv_following -> {
                val i = Bundle()
                i.putBoolean("isFollowing", true)
                findNavController().navigate(
                    R.id.action_profileFragment_to_userFollowingsFragment,
                    i
                )
            }
            R.id.lbl_followers, R.id.tv_followers -> {
                val i = Bundle()
                i.putBoolean("isFollowing", false)
                findNavController().navigate(
                    R.id.action_profileFragment_to_userFollowingsFragment,
                    i
                )
            }
            R.id.iv_profilepic_edit, R.id.ivProfile -> {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
            }
            R.id.tvUsername, R.id.ivUsernameEdit -> {
                findNavController().navigate(R.id.action_profileFragment_to_updateUsernameFragment)

            }
        }
    }

    override fun onStart() {
        super.onStart()
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
        Handler(Looper.getMainLooper()).postDelayed({
            binding.viewPager.setCurrentItem(lastPageIndex, false)
        }, 100)
    }

    override fun onStop() {
        super.onStop()
        lastPageIndex = currentPagePosition
        binding.viewPager.adapter = null
    }


}