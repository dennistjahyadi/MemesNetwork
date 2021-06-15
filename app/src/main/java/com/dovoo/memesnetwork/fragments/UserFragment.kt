package com.dovoo.memesnetwork.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.dovoo.memesnetwork.databinding.FragmentUserBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.model.UserOtherDetails
import com.dovoo.memesnetwork.model.UserOtherResponse
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import org.json.JSONException
import java.util.*

class UserFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val userId by lazy {
        arguments?.getInt("user_id")!!
    }
    private val memesList: ArrayList<DirectLinkItemTest> = ArrayList()
    var adapter: MyMemesFragment.MyMemesAdapter? = null

    val memesOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MyMemesFragment.MyMemesAdapter.MyMemesViewHolder
        val bundle = bundleOf("item" to memesViewHolder.data)
        findNavController().navigate(R.id.action_userFragment_to_memesDetailFragment, bundle)
    }

    var currentUserResp: UserOtherResponse? = null

    var isFollowing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        if(GlobalFunc.isLogin(requireContext())){
            if (userId == GlobalFunc.getLoggedInUserId(requireContext())) binding.linBtnFollow.visibility =
                View.GONE
            else binding.linBtnFollow.visibility = View.VISIBLE
        }else{
            binding.linBtnFollow.visibility = View.GONE
        }
        adapter = MyMemesFragment.MyMemesAdapter(requireContext(), memesList, memesOnClickListener)

        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
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
        binding.lblFollowing.setOnClickListener(this)
        binding.lblFollowers.setOnClickListener(this)
        binding.tvFollowing.setOnClickListener(this)
        binding.tvFollowers.setOnClickListener(this)

        binding.linBtnFollow.setOnClickListener {
            isFollowing = !isFollowing
            updateUI()
            follow()
        }

        generalViewModel.userMemes.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {

                    // do anything with response
                    try {
                        it.data?.memes?.forEach { meme ->
                            memesList.add(DirectLinkItemTest(meme))
                        }
                        adapter?.notifyDataSetChanged()
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
        getUser()
        if (memesList.isEmpty()) fetchData(0)

        return binding.root
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) {
            memesList.clear()
        }
        generalViewModel.fetchUserMemes(offset, userId, null)
    }

    private fun getUser() {
        generalViewModel.getUser(requireContext(), userId).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    currentUserResp = it.data
                    isFollowing = currentUserResp!!.is_following
                    updateUI()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun follow() {
        val currentUserId = GlobalFunc.getLoggedInUserId(requireContext())
        generalViewModel.setFollowing(currentUserId, userId).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                    isFollowing = !isFollowing
                    updateUI()
                }
            }
        })
    }

    private fun updateUI() {
        currentUserResp?.let { resp ->
            binding.tvUsername.text = resp.user.username
            if (resp.user.photo_url.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(R.drawable.funny_user2)
                    .into(binding.ivProfile)
            } else {
                Glide.with(requireContext())
                    .load(resp.user.photo_url)
                    .placeholder(R.drawable.funny_user2)
                    .into(binding.ivProfile)
            }

            binding.tvTotalMemes.text = resp.total_memes.toString()
            binding.tvFollowers.text = resp.total_follower.toString()
            binding.tvFollowing.text = resp.total_following.toString()
        }

        if (isFollowing) {
            binding.linBtnFollow.setBackgroundResource(R.drawable.rounded_border_white_bold)
            binding.tvFollow.setText(R.string.followed)
            binding.tvFollow.typeface = Typeface.DEFAULT_BOLD;
        } else {
            binding.linBtnFollow.setBackgroundResource(R.drawable.rounded_border_white)
            binding.tvFollow.setText(R.string.follow)
            binding.tvFollow.typeface = Typeface.DEFAULT;
        }


    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.lbl_following, R.id.tv_following -> {
                val i = Bundle()
                i.putBoolean("isFollowing", true)
                i.putInt("user_id", userId)
                findNavController().navigate(R.id.action_userFragment_to_userFollowingsFragment, i)
            }
            R.id.lbl_followers, R.id.tv_followers -> {
                val i = Bundle()
                i.putBoolean("isFollowing", false)
                i.putInt("user_id", userId)
                findNavController().navigate(R.id.action_userFragment_to_userFollowingsFragment, i)
            }
        }
    }
}