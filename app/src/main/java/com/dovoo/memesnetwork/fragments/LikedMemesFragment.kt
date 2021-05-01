package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
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
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.util.*

class LikedMemesFragment : Fragment() {
    private var _binding: FragmentLikedMemesBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    private val memesList: ArrayList<DirectLinkItemTest> = ArrayList()
    lateinit var adapter: LikedMemesAdapter

    val memesOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as LikedMemesAdapter.LikedMemesViewHolder

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

                    // do anything with response
                    try {
                        it.data?.memes?.forEach { meme ->
                            memesList.add(DirectLinkItemTest(meme, Picasso.get()))
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
        if (memesList.isEmpty()) fetchData(0)
        return binding.root
    }


    private fun fetchData(offset: Int) {
        if (offset == 0) {
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

    class LikedMemesAdapter(
        val context: Context,
        val memesList: List<DirectLinkItemTest>,
        val onClickListener: View.OnClickListener
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class LikedMemesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivImage: ImageView
            lateinit var data: Memes

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
            holder.itemView.setOnClickListener(onClickListener)
            Glide.with(context).load(data.getmCoverUrl()).centerCrop().into(holder.ivImage)
        }

        override fun getItemCount(): Int {
            return memesList.size
        }

    }
}