package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.MemesRecyclerViewAdapter
import com.dovoo.memesnetwork.adapter.holders.MemesViewHolder
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.components.MyLinearLayoutManager
import com.dovoo.memesnetwork.databinding.FragmentFollowingMemesBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import im.ene.toro.widget.PressablePlayerSelector
import org.json.JSONException
import java.util.*

class FollowingMemesFragment : Fragment() {
    private var _binding: FragmentFollowingMemesBinding? = null
    private val binding get() = _binding!!
    private lateinit var layoutManager: MyLinearLayoutManager
    private lateinit var adapter: MemesRecyclerViewAdapter
    private lateinit var selector: PressablePlayerSelector
    private val directLinkItemTestList: ArrayList<DirectLinkItemTest> = ArrayList()
    val generalViewModel: GeneralViewModel by viewModels()

    val likeOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MemesViewHolder
        if (GlobalFunc.isLogin(requireContext())
        ) {
            doLike(
                memesViewHolder.data.id,
                memesViewHolder.data,
                memesViewHolder.ivBtnLike,
                memesViewHolder.tvTotalLike,
                memesViewHolder.linBtnLike
            )
        }
    }

    val itemOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MemesViewHolder
        val bundle = bundleOf("item" to memesViewHolder.data)
        findNavController().navigate(
            R.id.action_followingMemesFragment_to_memesDetailFragment,
            bundle
        )
    }

    val profileOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MemesViewHolder
        val bundle = bundleOf("user_id" to memesViewHolder.data.user!!.id)
        findNavController().navigate(R.id.action_followingMemesFragment_to_userFragment, bundle)
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
        data.totalLike = if (isLiked == 1) totLike + 1 else totLike - 1
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

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingMemesBinding.inflate(inflater, viewGroup, false)

        layoutManager = MyLinearLayoutManager(context)

        selector = PressablePlayerSelector(binding.playerContainer)
        binding.playerContainer.layoutManager = layoutManager
        binding.playerContainer.playerSelector = selector

        adapter = MemesRecyclerViewAdapter(
            requireContext(),
            selector,
            directLinkItemTestList,
            binding.loadingBar,
            likeOnClickListener,
            itemOnClickListener,
            profileOnClickListener
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
        generalViewModel.memesFollowing.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    // do anything with response
                    try {
                        it.data?.memes?.forEach { meme ->
                            val directLinkItem = DirectLinkItemTest(meme)
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
        binding.includeToolbar.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvMenuHome.setOnClickListener {
            findNavController().popBackStack()
        }
        if (directLinkItemTestList.isEmpty()) fetchData(0)

        return binding.root
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) {
            directLinkItemTestList.clear()
        }
        val userId = SharedPreferenceUtils.getPrefs(requireContext())
            .getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0)
        generalViewModel.fetchMemesFollowing(offset, userId)
    }

    override fun onDestroyView() {
//        layoutManager = null
//        adapter = null
//        selector = null
        super.onDestroyView()
    }
}