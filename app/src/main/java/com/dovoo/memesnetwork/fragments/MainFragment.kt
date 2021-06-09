package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.util.Log
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
import com.dovoo.memesnetwork.databinding.FragmentMainBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import im.ene.toro.widget.PressablePlayerSelector
import org.json.JSONException
import java.util.*

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var selectedSection: String? = null
    private lateinit var layoutManager: MyLinearLayoutManager
    private lateinit var adapter: MemesRecyclerViewAdapter
    private lateinit var selector: PressablePlayerSelector
    private val directLinkItemTestList: ArrayList<DirectLinkItemTest> = ArrayList()
    val generalViewModel: GeneralViewModel by viewModels()

    val likeOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MemesViewHolder
        if (!getPrefs(requireContext()).getBoolean(
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
                memesViewHolder.tvTotalLike,
                memesViewHolder.linBtnLike
            )
        }
    }

    val itemOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MemesViewHolder
        val bundle = bundleOf("item" to memesViewHolder.data)
        findNavController().navigate(R.id.action_mainFragment_to_memesDetailFragment, bundle)
    }

    val profileOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MemesViewHolder
        val bundle = bundleOf("user_id" to memesViewHolder.data.user!!.id)
        findNavController().navigate(R.id.action_mainFragment_to_userFragment, bundle)
    }

    private fun doLike(
        memeId: Int,
        data: DirectLinkItemTest,
        ivLike: ImageView,
        tvTotalLike: TextView,
        linBtnLike: LinearLayout
    ) {
        val userId =
            getPrefs(requireContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, -1)
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
        data.totalLike = if(isLiked==1) totLike+1 else totLike-1
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
        _binding = FragmentMainBinding.inflate(inflater, viewGroup, false)

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
        generalViewModel.memesHome.observe(viewLifecycleOwner, {
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
        binding.includeToolbar.ivBtnProfile.setOnClickListener {
            if (GlobalFunc.isLogin(requireContext())) findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
            else findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        binding.fabAdd.setOnClickListener {
            if (GlobalFunc.isLogin(requireContext())) findNavController().navigate(R.id.action_mainFragment_to_addMemeFragment)
            else findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        binding.includeToolbar.linBtnFilter.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_filterFragment)
        }
        binding.includeToolbar.conNotification.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_notificationFragment)
        }
        binding.tvMenuFollowing.setOnClickListener {
            if (GlobalFunc.isLogin(requireContext())) findNavController().navigate(R.id.action_mainFragment_to_followingMemesFragment)
            else findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        if (directLinkItemTestList.isEmpty()) fetchData(0)

        onResult()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(GlobalFunc.getNotifCount(requireContext())>0){
            binding.includeToolbar.dotBadge.visibility = View.VISIBLE
        }else{
            binding.includeToolbar.dotBadge.visibility = View.GONE
        }
    }

    private fun onResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedSection")
            ?.observe(viewLifecycleOwner, {
                selectedSection = it
                fetchData(0)
            })
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("loginSuccess")
            ?.observe(viewLifecycleOwner, {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(
                                "TAG",
                                "Fetching FCM registration token failed",
                                task.exception
                            )
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                            val token = task.result
                            if (GlobalFunc.isLogin(requireContext())) {
                                val userId = GlobalFunc.getLoggedInUserId(requireContext())
                                try {
                                    generalViewModel.setFirebaseToken(userId, token!!)
                                } catch (ex: Exception) {
                                }
                            }

                    })
                if (it) fetchData(0)
            })
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) {
            directLinkItemTestList.clear()
        }
        val userId = getPrefs(requireContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0)
        generalViewModel.fetchMemesHome(offset, userId, selectedSection)
    }

    override fun onDestroyView() {
//        layoutManager = null
//        adapter = null
//        selector = null
        super.onDestroyView()
    }

}