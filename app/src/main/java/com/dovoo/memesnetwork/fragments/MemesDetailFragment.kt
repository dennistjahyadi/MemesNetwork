package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.databinding.FragmentMemesDetailsBinding
import com.dovoo.memesnetwork.utils.AdUtils.loadAds
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*


class MemesDetailFragment : Fragment() {
    private var _binding: FragmentMemesDetailsBinding? = null
    private val binding get() = _binding!!
    val currentVideoItem by lazy {
        arguments?.getParcelable<DirectLinkItemTest>("item")
    }
    val defaultCommentPage by lazy {
        arguments?.getBoolean("defaultCommentPage")
    }
    val listFragment: ArrayList<Fragment> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragments()
    }

    private fun initFragments() {
        listFragment.add(MemesDetailMemesFragment.newInstance(currentVideoItem!!))
        listFragment.add(MemesDetailCommentFragment.newInstance(currentVideoItem!!))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemesDetailsBinding.inflate(inflater, container, false)
        loadAds(requireContext(), binding.adView)
        binding.viewPager.adapter = MemesDetailViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            listFragment
        )
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.memes)
                1 -> tab.text = getString(R.string.comments)
                else -> tab.text = getString(R.string.unknown)
            }
        }.attach()
        binding.linBtnBack.setOnClickListener { findNavController().popBackStack() }
        binding.viewPager.offscreenPageLimit = 2

        if(GlobalFunc.isLogin(requireContext()) && currentVideoItem?.user?.id == GlobalFunc.getLoggedInUserId(requireContext())){
            binding.tvDelete.visibility = View.VISIBLE
        }else{
            binding.tvDelete.visibility = View.GONE
        }
        binding.tvDelete.setOnClickListener {

        }

        onResult()

        return binding.root
    }

    private fun deleteMemes(){

    }

    override fun onStart() {
        super.onStart()
        if (defaultCommentPage == true) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.viewPager.setCurrentItem(1, false)
            }, 100)
        }
    }

    private fun onResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("openCommentTab")
            ?.observe(viewLifecycleOwner, {
                if (it) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.viewPager.setCurrentItem(1, false)
                    }, 100)
                }
            })
    }

    class MemesDetailViewPagerAdapter(
        fragment: FragmentManager,
        lifecycle: Lifecycle,
        val listFragment: ArrayList<Fragment>
    ) :
        FragmentStateAdapter(fragment, lifecycle) {

        override fun getItemCount(): Int = listFragment.size

        override fun createFragment(position: Int): Fragment {
            return listFragment[position]
        }

    }

    override fun onPause() {
        super.onPause()
      //  binding.viewPager.adapter = null
    }
}


