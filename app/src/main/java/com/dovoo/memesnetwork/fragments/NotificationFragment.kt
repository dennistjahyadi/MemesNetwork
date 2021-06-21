package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.graphics.Typeface
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
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentNotificationBinding
import com.dovoo.memesnetwork.model.Notification
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    lateinit var adapter: NotificationAdapter
    val listNotification: ArrayList<Notification> = ArrayList()

    val notificationOnClick = View.OnClickListener {
        val viewHolder = it.tag as NotificationAdapter.NotificationViewHolder
        val data = viewHolder.data
        if (data.type == Notification.TYPE_FOLLOWING) {
            val bundle = bundleOf("user_id" to data.user_id_from)
            findNavController().navigate(R.id.action_notificationFragment_to_userFragment, bundle)
        } else if (data.type == Notification.TYPE_MEME_COMMENT) {
            try {
                val directLinkItemTest = DirectLinkItemTest(data.meme_obj!!)
                val bundle = bundleOf("item" to directLinkItemTest)
                findNavController().navigate(
                    R.id.action_notificationFragment_to_memesDetailFragment,
                    bundle
                )
            } catch (ex: Exception) {
            }
        } else if (data.type == Notification.TYPE_SUB_COMMENT) {
            val arguments = Bundle()
            arguments.putParcelable("main_comment", data.main_comment_obj)
            findNavController().navigate(
                R.id.action_notificationFragment_to_commentDetailsFragment,
                arguments
            )
        } else if (data.type == Notification.TYPE_MEME_LIKED) {
            try {
                val directLinkItemTest = DirectLinkItemTest(data.meme_obj!!)
                val bundle = bundleOf("item" to directLinkItemTest)
                findNavController().navigate(
                    R.id.action_notificationFragment_to_memesDetailFragment,
                    bundle
                )
            } catch (ex: Exception) {
            }
        }

    }

    val profileOnCLick = View.OnClickListener {
        val viewHolder = it.tag as NotificationAdapter.NotificationViewHolder
        val data = viewHolder.data
        val bundle = bundleOf("user_id" to data.user_id_from)
        findNavController().navigate(R.id.action_notificationFragment_to_userFragment, bundle)
    }

    val contentOnCLick = View.OnClickListener {
        val viewHolder = it.tag as NotificationAdapter.NotificationViewHolder
        val data = viewHolder.data
        try {
            val directLinkItemTest = DirectLinkItemTest(data.meme_obj!!)
            val bundle = bundleOf("item" to directLinkItemTest)
            findNavController().navigate(
                R.id.action_notificationFragment_to_memesDetailFragment,
                bundle
            )
        } catch (ex: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = NotificationAdapter(
            requireContext(),
            listNotification,
            notificationOnClick,
            profileOnCLick,
            contentOnCLick
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
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


        if (listNotification.isEmpty()) fetchData(0)
        return binding.root
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) {
            listNotification.clear()
            binding.loadingBar.visibility = View.VISIBLE
        }
        val userId = GlobalFunc.getLoggedInUserId(requireContext())
        generalViewModel.fetchNotifications(offset, userId).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    val currentDatetime = it.data?.current_datetime
                    it.data?.notifications?.let {
                        it.forEach { notif ->
                            notif.main_comment_obj?.current_datetime = currentDatetime
                            notif.current_comment_obj?.current_datetime = currentDatetime
                        }
                        listNotification.addAll(it)
                    }
                    binding.loadingBar.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    GlobalFunc.clearNotifCount(requireContext())
                }
                Status.ERROR -> {
                    binding.loadingBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    class NotificationAdapter(
        val context: Context,
        val list: ArrayList<Notification>,
        val itemOnClickListener: View.OnClickListener,
        val profileOnClickListener: View.OnClickListener,
        val contentOnClickListener: View.OnClickListener
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivProfilePic: ImageView
            val ivContent: ImageView
            val tvMessage: TextView
            val linBtnFollow: LinearLayout
            val tvFollow: TextView
            val tvSubMessage: TextView
            lateinit var data: Notification

            init {
                ivProfilePic = itemView.findViewById(R.id.ivProfilePic)
                ivContent = itemView.findViewById(R.id.ivContent)
                tvMessage = itemView.findViewById(R.id.tvMessage)
                linBtnFollow = itemView.findViewById(R.id.linBtnFollow)
                tvFollow = itemView.findViewById(R.id.tvFollow)
                tvSubMessage = itemView.findViewById(R.id.tvSubMessage)
                ivProfilePic.tag = this
                ivContent.tag = this
                tvMessage.tag = this
                tvSubMessage.tag = this
                linBtnFollow.tag = this
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return NotificationViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as NotificationViewHolder
            val data = list[position]
            holder.data = data
            holder.ivProfilePic.setOnClickListener(profileOnClickListener)
            holder.tvMessage.setOnClickListener(itemOnClickListener)
            holder.tvSubMessage.setOnClickListener(itemOnClickListener)
            holder.ivContent.setOnClickListener(contentOnClickListener)

            Glide.with(context).load(data.user_from_obj?.photo_url)
                .placeholder(R.drawable.funny_user2).into(holder.ivProfilePic)
            holder.tvMessage.text = data.messages
            if (data.type.equals(Notification.TYPE_FOLLOWING)) {
                holder.ivContent.visibility = View.GONE
                holder.linBtnFollow.visibility = View.VISIBLE
                val isFollowing = data.is_following?.let { it > 0 }?.run { false }
                if (isFollowing!!) {
                    holder.linBtnFollow.setBackgroundResource(R.drawable.rounded_border_white_bold)
                    holder.tvFollow.setText(R.string.followed)
                    holder.tvFollow.typeface = Typeface.DEFAULT_BOLD;
                } else {
                    holder.linBtnFollow.setBackgroundResource(R.drawable.rounded_border_white)
                    holder.tvFollow.setText(R.string.follow)
                    holder.tvFollow.typeface = Typeface.DEFAULT;
                }
            } else {
                holder.ivContent.visibility = View.VISIBLE
                holder.linBtnFollow.visibility = View.GONE
                Glide.with(context).load(data.meme_obj?.getCoverUrl()).into(holder.ivContent)
                if (data.type.equals(Notification.TYPE_MEME_COMMENT) || data.type.equals(
                        Notification.TYPE_SUB_COMMENT
                    )
                ) {
                    holder.tvSubMessage.text = data.current_comment_obj?.messages
                }
            }

        }

        override fun getItemCount(): Int {
            return list.size
        }

    }
}