package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.CommentOnlyRecyclerViewAdapter
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentMemesDetailCommentsBinding
import com.dovoo.memesnetwork.model.Comment
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.Utils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import java.util.*


class MemesDetailCommentFragment : Fragment() {
    private var _binding: FragmentMemesDetailCommentsBinding? = null
    private val binding get() = _binding!!
    private val commentList: ArrayList<Comment> = ArrayList()
    val generalViewModel: GeneralViewModel by viewModels()

    private lateinit var commentRecyclerViewAdapter: CommentOnlyRecyclerViewAdapter
    val currentVideoItem by lazy {
        arguments?.getParcelable<DirectLinkItemTest>("current_video_item")
    }
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemesDetailCommentsBinding.inflate(inflater, container, false)
        binding.etComment.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (!SharedPreferenceUtils.getPrefs(requireContext()).getBoolean(
                        SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
                        false
                    )
                ) {
                    findNavController().navigate(R.id.action_memesDetailFragment_to_loginFragment)
                }
            }
        }
        commentRecyclerViewAdapter = CommentOnlyRecyclerViewAdapter(
            requireContext(), commentList
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
        if (commentList.isEmpty()) fetchComments(0)
        return binding.root
    }

    private fun fetchComments(offset: Int) {
        if (offset == 0) commentList.clear()
        generalViewModel.fetchComments(offset, null, currentVideoItem!!.id, "desc")
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.comments?.let { comments ->
                            comments.forEach { comment ->
                                comment.current_datetime = it.data.current_datetime
                            }
                            commentList.addAll(comments)

                            commentRecyclerViewAdapter.notifyDataSetChanged()
                            if (offset == 0) linearLayoutManager.scrollToPositionWithOffset(0, 0);
                        }
                    }
                    Status.ERROR -> {
                        println("bbbbb")
                    }
                }
            })
    }

    private fun sendComment() {
        binding.btnSend.isEnabled = false
        binding.progressBar.loadingBar.visibility = View.VISIBLE
        val messages = binding.etComment.text.toString()
        generalViewModel.sendComment(
            currentVideoItem!!.id, GlobalFunc.getLoggedInUserId(
                requireContext()
            ), messages, null
        ).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnSend.isEnabled = true
                    binding.progressBar.loadingBar.visibility = View.GONE
                    binding.etComment.setText("")
                    Utils.hideKeyboard(requireActivity())
                    fetchComments(0)
                }
                Status.ERROR -> {
                    binding.btnSend.isEnabled = true
                    binding.progressBar.loadingBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    companion object {
        fun newInstance(currentVideoItem: DirectLinkItemTest): MemesDetailCommentFragment {
            val args = Bundle()

            val fragment = MemesDetailCommentFragment()
            args.putParcelable("current_video_item", currentVideoItem)
            fragment.arguments = args
            return fragment
        }
    }
}