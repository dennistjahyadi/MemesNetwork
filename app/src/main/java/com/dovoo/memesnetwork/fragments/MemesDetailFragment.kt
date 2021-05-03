package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.CommentRecyclerViewAdapter
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentMemesDetailsBinding
import com.dovoo.memesnetwork.model.Comment
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.AdUtils.loadAds
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import java.util.*

class MemesDetailFragment: Fragment() {
    private var _binding: FragmentMemesDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var  commentRecyclerViewAdapter:CommentRecyclerViewAdapter
    private val commentList: ArrayList<Comment> = ArrayList()
    val memeId by lazy {
        arguments?.getInt("meme_id")
    }

    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemesDetailsBinding.inflate(inflater, container, false)
        loadAds(requireContext(), binding.adView)

        binding.etComment.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (!getPrefs(requireContext()).getBoolean(
                        SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
                        false
                    )
                ) {
                    findNavController().navigate(R.id.action_memesDetailFragment_to_loginFragment)
                }
            }
        }
        binding.linBtnBack.setOnClickListener { findNavController().popBackStack() }
        val linearLayoutManager = LinearLayoutManager(requireContext())
        commentRecyclerViewAdapter = CommentRecyclerViewAdapter(
            requireContext(), commentList,
            GlobalFunc.currentVideoItem!!
        )
        binding.rvComment.layoutManager = linearLayoutManager
        binding.rvComment.adapter = commentRecyclerViewAdapter

        val onLoad = object :
            EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchComments(totalItemsCount)
            }
        }
        binding.rvComment.removeOnScrollListener(onLoad)
        binding.rvComment.addOnScrollListener(onLoad)
        binding.btnSend.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(binding.etComment.text)) {
                return@OnClickListener
            }
            sendComment()
        })
        if(commentList.isEmpty()) fetchComments(0)
        return binding.root
    }

    private fun fetchComments(offset: Int){
        if(offset==0) commentList.clear()
        generalViewModel.fetchComments(offset, null, memeId).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.comments?.let { comments ->
                        comments.forEach {  comment ->
                            comment.current_datetime = it.data.current_datetime
                        }
                        commentList.addAll(comments)

                        commentRecyclerViewAdapter.notifyDataSetChanged()
                    }
                }
                Status.ERROR -> {
                    println("bbbbb")
                }
            }
        })
    }

    private fun sendComment(){

    }

}