package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentMyCommentsBinding
import com.dovoo.memesnetwork.model.Comment
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyCommentsFragment : Fragment() {
    private var _binding: FragmentMyCommentsBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val commentList: ArrayList<Comment> = ArrayList()
    lateinit var adapter: MyCommentsAdapter

    val onClickListener = View.OnClickListener {
        if(parentFragment is ProfileFragment)  (parentFragment as ProfileFragment).lastPageIndex = 2
        val viewHolder = it.tag as MyCommentsAdapter.MyCommentsViewHolder
        val data = viewHolder.data
        viewHolder.itemView.isClickable = false
        viewHolder.itemView.isEnabled = false
        generalViewModel.getMeme(data.meme_id).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    try {
                        val bundle = bundleOf(
                            "item" to DirectLinkItemTest(it.data!!.meme),
                            "defaultCommentPage" to true
                        )
                        findNavController().navigate(
                            R.id.action_profileFragment_to_memesDetailFragment,
                            bundle
                        )

                    } catch (ex: Exception) {
                    }
                    viewHolder.itemView.isClickable = true
                    viewHolder.itemView.isEnabled = true
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                    viewHolder.itemView.isClickable = true
                    viewHolder.itemView.isEnabled = true
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MyCommentsAdapter(requireContext(), commentList, onClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCommentsBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        val endlessSrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchComments(totalItemsCount)
            }
        }
        binding.recyclerView.removeOnScrollListener(endlessSrollListener)
        binding.recyclerView.addOnScrollListener(endlessSrollListener)

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.visibility = View.GONE
            fetchComments(0)
        }

        if (commentList.isEmpty()) fetchComments(0)
        return binding.root
    }

    private fun fetchComments(offset: Int) {
        if (offset == 0) commentList.clear()
        generalViewModel.fetchComments(
            offset,
            GlobalFunc.getLoggedInUserId(requireContext()),
            null,
            null,
            "desc"
        ).observe(viewLifecycleOwner, {
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
                        binding.swipeRefreshLayout.isEnabled = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.swipeRefreshLayout.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    }
                }
                Status.ERROR -> {
                    binding.swipeRefreshLayout.isEnabled = true
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.swipeRefreshLayout.visibility = View.VISIBLE
                    println("bbbbb")
                }
            }
        })
    }

    class MyCommentsAdapter(
        val context: Context,
        val commentList: List<Comment>,
        val onClickListener: View.OnClickListener
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        class MyCommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvComment: TextView
            val tvCreatedDate: TextView
            lateinit var data: Comment

            init {
                tvComment = itemView.findViewById(R.id.tvComment)
                tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate)
                itemView.tag = this
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.row_comment_history,
                parent,
                false
            )
            return MyCommentsViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as MyCommentsViewHolder
            val data = commentList[position]
            holder.data = data
            holder.itemView.setOnClickListener(onClickListener)
            holder.tvComment.text = data.messages
            try {
                if (data.created_at != null) {
                    val createdAtDate: Date = sdf.parse(data.created_at)
                    val currentDate: Date = sdf.parse(data.current_datetime)
                    val createAtMiliseconds = createdAtDate.time
                    val currentTimeMiliseconds = currentDate.time
                    val thedate = DateUtils.getRelativeTimeSpanString(
                        createAtMiliseconds,
                        currentTimeMiliseconds,
                        DateUtils.MINUTE_IN_MILLIS
                    )
                    holder.tvCreatedDate.text = thedate
                } else {
                    holder.tvCreatedDate.text = ""
                }
            } catch (e: ParseException) {
                holder.tvCreatedDate.text = ""
                e.printStackTrace()
            }
        }

        override fun getItemCount(): Int {
            return commentList.size
        }

    }
}