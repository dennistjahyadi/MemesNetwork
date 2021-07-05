package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.CommentOnlyRecyclerViewAdapter
import com.dovoo.memesnetwork.adapter.holders.MemesViewHolder
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
    var replyToData: Comment? = null
    var linearLayoutManager: LinearLayoutManager? = null
    val commentOnClickListener = View.OnClickListener {
        val data = (it.tag as CommentOnlyRecyclerViewAdapter.MyViewHolderItem).data
        val arguments = Bundle()
        arguments.putParcelable("main_comment", data)
        findNavController().navigate(R.id.action_memesDetailFragment_to_commentDetailsFragment, arguments)
    }

    val replyOnClickListener = View.OnClickListener {
        val data = (it.tag as CommentOnlyRecyclerViewAdapter.MyViewHolderItem).data
        replyToData = data
        binding.tvLblReply.text = getString(R.string.reply_to, data.user.username)
        binding.tvReplyToMsg.text = data.messages

        binding.linReplyTo.visibility = View.VISIBLE
    }

    val profileOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as CommentOnlyRecyclerViewAdapter.MyViewHolderItem
        val bundle = bundleOf("user_id" to memesViewHolder.data.user_id)
        findNavController().navigate(R.id.action_memesDetailFragment_to_userFragment, bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemesDetailCommentsBinding.inflate(inflater, container, false)
        binding.etComment.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (!GlobalFunc.isLogin(requireContext())) {
                    findNavController().navigate(R.id.action_memesDetailFragment_to_loginFragment)
                }
            }
        }
        commentRecyclerViewAdapter = CommentOnlyRecyclerViewAdapter(
            requireContext(), commentList, commentOnClickListener, replyOnClickListener, profileOnClickListener, true
        )
        val linearLayoutManager = LinearLayoutManager(requireContext())
        try {
            binding.rvComment.layoutManager = linearLayoutManager
            binding.rvComment.adapter = commentRecyclerViewAdapter
        }catch (ex: Exception){}

        val onLoad = object :
            EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchComments(totalItemsCount)
            }
        }
        binding.rvComment.removeOnScrollListener(onLoad)
        binding.rvComment.addOnScrollListener(onLoad)
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchComments(0)
        }
        binding.ivReplyToClose.setOnClickListener {
            replyToData = null
            binding.linReplyTo.visibility = View.GONE
        }
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
        binding.swipeRefreshLayout.isRefreshing=true
        if (offset == 0) commentList.clear()
        generalViewModel.fetchMainComments(offset, null, currentVideoItem!!.id, null, "desc")
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.comments?.let { comments ->
                            comments.forEach { comment ->
                                comment.current_datetime = it.data.current_datetime
                                comment.subcomments?.forEach { subcomment ->
                                    subcomment.current_datetime = it.data.current_datetime
                                }
                            }
                            commentList.addAll(comments)
                            binding.swipeRefreshLayout.isRefreshing=false
                            commentRecyclerViewAdapter.notifyDataSetChanged()
                            if (offset == 0) linearLayoutManager?.scrollToPositionWithOffset(0, 0);
                        }
                    }
                    Status.ERROR -> {
                        println("bbbbb")
                        binding.swipeRefreshLayout.isRefreshing=false
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
            ), messages, replyToData?.id
        ).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnSend.isEnabled = true
                    binding.progressBar.loadingBar.visibility = View.GONE
                    binding.etComment.setText("")
                    binding.linReplyTo.visibility = View.GONE
                    Utils.hideKeyboard(requireActivity())
                    if (replyToData == null) fetchComments(0)
                    else {
                        val arguments = Bundle()
                        arguments.putParcelable("main_comment", replyToData)
                        findNavController().navigate(R.id.action_memesDetailFragment_to_commentDetailsFragment, arguments)
                    }

                    replyToData = null

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