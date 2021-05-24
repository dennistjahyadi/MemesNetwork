package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentUserFollowingsBinding
import com.dovoo.memesnetwork.model.FollowingData
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.model.User
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class UserFollowingsFragment : Fragment() {
    private var _binding: FragmentUserFollowingsBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val isFollowing by lazy {
        arguments?.getBoolean("isFollowing")
    }
    val userId by lazy {
        arguments?.getInt("user_id")
    }
    val listData: ArrayList<FollowingData> = ArrayList()
    lateinit var adapter: UserFollowingsAdapter

    val userOnClickListener = View.OnClickListener {
        val data = (it.tag as UserFollowingsAdapter.UserFollowingsViewHolder).data

        val bundle = bundleOf("user_id" to data.id)
        findNavController().navigate(R.id.action_userFollowingsFragment_to_userFragment, bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = UserFollowingsAdapter(requireContext(), listData, userOnClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFollowingsBinding.inflate(inflater, container, false)
        if (isFollowing == true) binding.tvTitle.text = getString(R.string.followings)
        else binding.tvTitle.text = getString(R.string.followers)
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchData(totalItemsCount)
            }
        })
        if (listData.isEmpty()) fetchData(0)
        return binding.root
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) listData.clear()
        if (isFollowing == true) {
            generalViewModel.fetchFollowings(offset, userId!!).observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.followings?.let {
                            listData.addAll(it)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
        } else {
            generalViewModel.fetchFollowers(offset, userId!!).observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.followers?.let {
                            listData.addAll(it)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
        }
    }

    class UserFollowingsAdapter(
        val context: Context,
        val listData: ArrayList<FollowingData>,
        val userOnClickListener: View.OnClickListener
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class UserFollowingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivProfilePic: ImageView
            val tvUsername: TextView
            val linBtnFollow: LinearLayout
            val tvFollow: TextView
            lateinit var data: User

            init {
                ivProfilePic = itemView.findViewById(R.id.ivProfilePic)
                tvUsername = itemView.findViewById(R.id.tvUsername)
                linBtnFollow = itemView.findViewById(R.id.linBtnFollow)
                tvFollow = itemView.findViewById(R.id.tvFollow)
                itemView.tag = this

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return UserFollowingsViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_user_followings, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as UserFollowingsViewHolder
            val data = listData[position]
            if (data.user_following_obj != null) {
                holder.data = data.user_following_obj!!
            } else {
                holder.data = data.user_follower_obj!!
            }
            holder.tvUsername.text = holder.data.username
            Glide.with(context).load(holder.data.photo_url).placeholder(R.drawable.funny_user2)
                .into(holder.ivProfilePic)
            holder.itemView.setOnClickListener(userOnClickListener)

        }

        override fun getItemCount(): Int {
            return listData.size
        }

    }
}