package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.databinding.FragmentMemesDetailsBinding
import com.dovoo.memesnetwork.utils.AdUtils.loadAds
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MemesDetailFragment : Fragment() {
    private var _binding: FragmentMemesDetailsBinding? = null
    private val binding get() = _binding!!
    val currentVideoItem by lazy {
        arguments?.getParcelable<DirectLinkItemTest>("item")
    }
    val listFragment: ArrayList<Fragment> = ArrayList()

    private lateinit var memesDetailViewPagerAdapter: MemesDetailViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragments()
        memesDetailViewPagerAdapter = MemesDetailViewPagerAdapter(this, listFragment)
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

        binding.linBtnBack.setOnClickListener { findNavController().popBackStack() }

        binding.viewPager.adapter = memesDetailViewPagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.memes)
                1 -> tab.text = getString(R.string.comments)
                else -> tab.text = getString(R.string.unknown)
            }
        }.attach()
        return binding.root
    }


    class MemesDetailViewPagerAdapter(fragment: Fragment, val listFragment: ArrayList<Fragment>) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = listFragment.size

        override fun createFragment(position: Int): Fragment {
            return listFragment[position]
        }

    }

}


