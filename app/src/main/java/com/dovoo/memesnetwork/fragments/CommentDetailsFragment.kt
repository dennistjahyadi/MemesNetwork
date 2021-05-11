package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.CommentOnlyRecyclerViewAdapter
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentCommentDetailsBinding
import com.dovoo.memesnetwork.model.Comment
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.Utils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommentDetailsFragment : Fragment() {
    private var _binding: FragmentCommentDetailsBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val mainComment by lazy {
        arguments?.getParcelable<Comment>("main_comment")
    }
    private val commentList: ArrayList<Comment> = ArrayList()
    private lateinit var commentRecyclerViewAdapter: CommentOnlyRecyclerViewAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

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
        _binding = FragmentCommentDetailsBinding.inflate(inflater, container, false)
        binding.linBtnBack.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("openCommentTab", true)
            findNavController().popBackStack()
        }

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
        initMainComment()
        fetchComments(0)
        return binding.root
    }

    private fun initMainComment() {
        if (mainComment?.user?.photo_url?.trim().isNullOrEmpty()) {
            Glide.with(requireContext()).load(R.drawable.funny_user2).into(binding.ivPicture)
        } else {
            Glide.with(requireContext()).load(mainComment?.user?.photo_url).centerCrop()
                .into(binding.ivPicture)
        }
        binding.tvUsername.text = mainComment?.user?.username
        try {
            if (!mainComment?.created_at.isNullOrEmpty()) {
                val createdAtDate = sdf.parse(mainComment!!.created_at)
                val currentDate = sdf.parse(mainComment!!.current_datetime)
                val createAtMiliseconds = createdAtDate.time
                val currentTimeMiliseconds = currentDate.time
                val thedate = DateUtils.getRelativeTimeSpanString(
                    createAtMiliseconds,
                    currentTimeMiliseconds,
                    DateUtils.MINUTE_IN_MILLIS
                )
                binding.tvCreatedDate.text = thedate
            } else {
                binding.tvCreatedDate.text = "null"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun sendComment() {
        binding.btnSend.isEnabled = false
        binding.progressBar.loadingBar.visibility = View.VISIBLE
        val messages = binding.etComment.text.toString()
        generalViewModel.sendComment(
            mainComment!!.meme_id, GlobalFunc.getLoggedInUserId(
                requireContext()
            ), messages, mainComment?.id
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
            mainComment!!.meme_id,
            mainComment?.id,
            "desc"
        )
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