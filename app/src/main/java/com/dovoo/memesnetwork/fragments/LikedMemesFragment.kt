package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentLikedMemesBinding
import com.dovoo.memesnetwork.model.Memes
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import org.json.JSONException
import java.util.*

class LikedMemesFragment : Fragment() {
    private var _binding: FragmentLikedMemesBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    private val memesList: ArrayList<DirectLinkItemTest> = ArrayList()
    lateinit var adapter: LikedMemesAdapter

    val memesOnClickListener = View.OnClickListener {
        if(parentFragment is ProfileFragment)  (parentFragment as ProfileFragment).lastPageIndex = 1
        val memesViewHolder = it.tag as LikedMemesAdapter.LikedMemesViewHolder
        val bundle = bundleOf("item" to memesViewHolder.data)
        findNavController().navigate(R.id.action_profileFragment_to_memesDetailFragment, bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = LikedMemesAdapter(requireContext(), memesList, memesOnClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedMemesBinding.inflate(inflater, container, false)
        val layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)

        val endlessSrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchData(totalItemsCount)
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.removeOnScrollListener(endlessSrollListener)
        binding.recyclerView.addOnScrollListener(endlessSrollListener)

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.visibility = View.GONE
            fetchData(0)
        }
        generalViewModel.likedMemes.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.loadingBar.visibility = View.GONE

                    // do anything with response
                    try {
                        it.data?.memes?.forEach { meme ->
                            memesList.add(DirectLinkItemTest(meme))
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
                    binding.loadingBar.visibility = View.GONE

                    println("BBBBB: " + it.error?.message)
                }
                else -> {
                }
            }
        })
        if (memesList.isEmpty()) fetchData(0)
        return binding.root
    }


    private fun fetchData(offset: Int) {
        if (offset == 0) {
            binding.loadingBar.visibility = View.VISIBLE
            memesList.clear()
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
        var isLiked = data.isLiked

        if (isLiked == 1) {
            isLiked = 0
            ivLike.setImageResource(R.drawable.ic_thumbs_up)

        } else {
            isLiked = 1
            ivLike.setImageResource(R.drawable.ic_thumbs_up_active)
        }
        ivLike.isEnabled = false
        data.isLiked = isLiked
        val totLike = data.totalLike
        data.totalLike = (totLike + 1)
        tvTotalLike.text = data.totalLike.toString()

        generalViewModel.insertLike(memeId, userId, isLiked).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    ivLike.isEnabled = true

                }
                Status.ERROR -> {
                    ivLike.isEnabled = true
                    if (isLiked == 1) {
                        ivLike.setImageResource(R.drawable.ic_thumbs_up)
                        data.isLiked = 0
                    } else {
                        ivLike.setImageResource(R.drawable.ic_thumbs_up_active)
                        data.isLiked = 1
                    }
                    val totLike2 = data.totalLike
                    data.totalLike = (totLike2 - 1)
                    tvTotalLike.text = data.totalLike.toString()
                }
            }
        })
    }

    class LikedMemesAdapter(
        val context: Context,
        val memesList: List<DirectLinkItemTest>,
        val onClickListener: View.OnClickListener
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class LikedMemesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivImage: ImageView
            lateinit var data: DirectLinkItemTest

            init {
                ivImage = itemView.findViewById(R.id.iv_image)
                itemView.tag = this
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.view_liked_memes_item,
                parent,
                false
            )
            return LikedMemesViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as LikedMemesViewHolder
            val data = memesList[position]
            holder.data = data
            holder.itemView.setOnClickListener(onClickListener)
            Glide.with(context).load(data.getmCoverUrl()).centerCrop().into(holder.ivImage)
        }

        override fun getItemCount(): Int {
            return memesList.size
        }

    }
}