package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.adapter.CommentOnlyRecyclerViewAdapter
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentMemesDetailCommentsBinding
import com.dovoo.memesnetwork.model.Comment
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.Utils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import java.util.ArrayList

class CommentDetailsFragment : Fragment() {
    private var _binding: FragmentMemesDetailCommentsBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val currentComment by lazy {
        arguments?.getParcelable<Comment>("current_comment")
    }
    private val commentList: ArrayList<Comment> = ArrayList()
    private lateinit var commentRecyclerViewAdapter: CommentOnlyRecyclerViewAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
        commentRecyclerViewAdapter = CommentOnlyRecyclerViewAdapter(
            requireContext(), commentList, null, null, false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemesDetailCommentsBinding.inflate(inflater, container, false)

        binding.btnSend.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(binding.etComment.text)) {
                return@OnClickListener
            }
            sendComment()
        })
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
        return binding.root
    }

    private fun sendComment() {
        binding.btnSend.isEnabled = false
        binding.progressBar.loadingBar.visibility = View.VISIBLE
        val messages = binding.etComment.text.toString()
        generalViewModel.sendComment(
            currentComment!!.meme_id, GlobalFunc.getLoggedInUserId(
                requireContext()
            ), messages, currentComment?.id
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

    private fun fetchComments(offset: Int) {
        if (offset == 0) commentList.clear()
        generalViewModel.fetchComments(
            offset,
            null,
            currentComment!!.meme_id,
            currentComment?.id,
            "desc"
        )
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.comments?.let { comments ->
                            comments.forEach { comment ->
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

}