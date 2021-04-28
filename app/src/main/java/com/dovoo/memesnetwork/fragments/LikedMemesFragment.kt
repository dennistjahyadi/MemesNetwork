package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.MemesRecyclerViewAdapter
import com.dovoo.memesnetwork.adapter.holders.MemesViewHolder
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.components.MyLinearLayoutManager
import com.dovoo.memesnetwork.databinding.FragmentLikedMemesBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.squareup.picasso.Picasso
import im.ene.toro.widget.PressablePlayerSelector
import org.json.JSONException
import java.util.ArrayList

class LikedMemesFragment : Fragment() {
    private var _binding: FragmentLikedMemesBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    private val directLinkItemTestList: ArrayList<DirectLinkItemTest> = ArrayList()
    private lateinit var adapter: MemesRecyclerViewAdapter
    private lateinit var layoutManager: MyLinearLayoutManager
    private lateinit var selector: PressablePlayerSelector

    val likeOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MemesViewHolder
        if (!SharedPreferenceUtils.getPrefs(requireContext()).getBoolean(
                SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
                false
            )
        ) {
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        } else {
            doLike(
                memesViewHolder.data.id,
                memesViewHolder.data,
                memesViewHolder.ivBtnLike,
                memesViewHolder.tvTotalLike
            )
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedMemesBinding.inflate(inflater, container, false)

        layoutManager = MyLinearLayoutManager(context)

        selector = PressablePlayerSelector(binding.playerContainer)
        binding.playerContainer.layoutManager = layoutManager
        binding.playerContainer.playerSelector = selector
        adapter = MemesRecyclerViewAdapter(
            requireContext(),
            selector,
            directLinkItemTestList,
            FrameLayout(requireContext()),
            likeOnClickListener
        )
        binding.playerContainer.adapter = adapter
        binding.playerContainer.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchData(totalItemsCount)
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.visibility = View.GONE
            fetchData(0)
        }
        generalViewModel.likedMemes.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {

                    // do anything with response
                    try {
                        it.data?.memes?.forEach { meme ->
                            val directLinkItem = DirectLinkItemTest(meme, Picasso.get())
                            directLinkItemTestList.add(directLinkItem)
                        }
                        adapter.notifyDataSetChanged()
                        binding.swipeRefreshLayout.isEnabled = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.swipeRefreshLayout.visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                Status.ERROR -> {
                    println("BBBBB: " + it.error?.message)
                }
                else -> {
                }
            }
        })
        if (directLinkItemTestList.isEmpty()) fetchData(0)
        return binding.root
    }


    private fun fetchData(offset: Int) {
        if (offset == 0) {
            directLinkItemTestList.clear()
        }
        val userId = SharedPreferenceUtils.getPrefs(requireContext())
            .getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0)
        generalViewModel.fetchLikedMemes(offset, userId)
    }

    private fun doLike(
        memeId: Int,
        data: DirectLinkItemTest,
        ivLike: ImageView,
        tvTotalLike: TextView
    ) {
        val userId =
            SharedPreferenceUtils.getPrefs(requireContext())
                .getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, -1)
        var isLiked = data.data!!["is_liked"] as Int
        val mutableData = data.data!!.toMutableMap()

        if (isLiked == 1) {
            isLiked = 0
            ivLike.setImageResource(R.drawable.ic_thumbs_up)

        } else {
            isLiked = 1
            ivLike.setImageResource(R.drawable.ic_thumbs_up_active)
        }
        ivLike.isEnabled = false
        mutableData.put("is_liked", isLiked)
        val totLike = mutableData["total_like"] as String
        mutableData.put("total_like", (totLike.toInt() + 1).toString())
        tvTotalLike.text = mutableData["total_like"] as String

        generalViewModel.insertLike(memeId, userId, isLiked).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    ivLike.isEnabled = true

                }
                Status.ERROR -> {
                    ivLike.isEnabled = true
                    if (isLiked == 1) {
                        ivLike.setImageResource(R.drawable.ic_thumbs_up)
                        mutableData.put("is_liked", 0)
                    } else {
                        ivLike.setImageResource(R.drawable.ic_thumbs_up_active)
                        mutableData.put("is_liked", 1)
                    }
                    val totLike2 = mutableData["total_like"] as String
                    mutableData.put("total_like", (totLike2.toInt() - 1).toString())
                    tvTotalLike.text = mutableData["total_like"] as String
                }
            }
        })
    }
}