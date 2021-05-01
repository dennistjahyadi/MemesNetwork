package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentMyMemesBinding
import com.dovoo.memesnetwork.model.Memes
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.util.*

class MyMemesFragment : Fragment() {
    private var _binding: FragmentMyMemesBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    private val memesList: ArrayList<DirectLinkItemTest> = ArrayList()
    lateinit var adapter: MyMemesAdapter

    val memesOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MyMemesAdapter.MyMemesViewHolder

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MyMemesAdapter(requireContext(), memesList, memesOnClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyMemesBinding.inflate(inflater, container, false)
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
        generalViewModel.myMemes.observe(viewLifecycleOwner, {
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
        generalViewModel.fetchMyMemes(offset, userId, null)
    }

    class MyMemesAdapter(
        val context: Context,
        val memesList: List<DirectLinkItemTest>,
        val onClickListener: View.OnClickListener
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class MyMemesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivImage: ImageView
            lateinit var data: Memes

            init {
                ivImage = itemView.findViewById(R.id.iv_image)
                itemView.tag = this
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.view_mymemes_item,
                parent,
                false
            )
            return MyMemesViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as MyMemesViewHolder
            val data = memesList[position]
            holder.itemView.setOnClickListener(onClickListener)
            Glide.with(context).load(data.getmCoverUrl()).centerCrop().into(holder.ivImage)
        }

        override fun getItemCount(): Int {
            return memesList.size
        }

    }
}