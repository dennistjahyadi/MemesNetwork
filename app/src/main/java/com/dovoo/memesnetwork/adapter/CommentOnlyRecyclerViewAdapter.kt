package com.dovoo.memesnetwork.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.model.Comment
import com.dovoo.memesnetwork.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommentOnlyRecyclerViewAdapter(
    val context: Context,
    private val itemList: ArrayList<Comment>,
    val commentOnClickListener: View.OnClickListener?,
    val replyOnClickListener: View.OnClickListener?,
    val showReply: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val funnyimgs =
        intArrayOf(R.drawable.funny_user1, R.drawable.funny_user2, R.drawable.funny_user3)

    internal inner class MyViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView
        var tvComment: TextView
        var tvCreatedDate: TextView
        var ivPicture: ImageView
        var tvBtnReply: TextView
        var paddingBottom: FrameLayout
        var linSubcomments: LinearLayout
        lateinit var data: Comment

        init {
            tvUsername = itemView.findViewById(R.id.tvUsername)
            tvComment = itemView.findViewById(R.id.tvComment)
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate)
            ivPicture = itemView.findViewById(R.id.ivPicture)
            tvBtnReply = itemView.findViewById(R.id.tvBtnReply)
            paddingBottom = itemView.findViewById(R.id.paddingBottom)
            linSubcomments = itemView.findViewById(R.id.lin_sub_comments)
            tvBtnReply.tag = this
            tvComment.tag = this
            linSubcomments.tag = this
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_comment, parent, false)
        return MyViewHolderItem(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyViewHolderItem
        val obj = itemList[position]
        holder.data = obj
        if (position < itemList.size) {
            holder.paddingBottom.visibility = View.GONE
        } else {
            holder.paddingBottom.visibility = View.VISIBLE
        }

        //vhItem.tvUsername.setText((String)obj.get("created_by"));
        // vhItem.tvUsername.text = obj["created_by"] as String?
        holder.tvUsername.text = obj.user.username
        holder.tvComment.text = obj.messages
        if (showReply) holder.tvBtnReply.visibility = View.VISIBLE
        else holder.tvBtnReply.visibility = View.GONE

        if (obj.comment_id == null) holder.tvBtnReply.visibility = View.VISIBLE
        else holder.tvBtnReply.visibility = View.GONE

        holder.tvBtnReply.setOnClickListener(replyOnClickListener)
        holder.tvComment.setOnClickListener(commentOnClickListener)
        holder.linSubcomments.setOnClickListener(commentOnClickListener)
        if (obj.user.photo_url?.trim().isNullOrEmpty()) {
            Glide.with(context).load(funnyimgs[2]).into(holder.ivPicture)
        } else {
            Glide.with(context).load(obj.user.photo_url).centerCrop().into(holder.ivPicture)
        }
        obj.subcomments = obj.subcomments?.asReversed()
        obj.subcomments?.forEach { subcomment ->
            val view = LayoutInflater.from(context).inflate(R.layout.row_subcomment, null)
            val ivPicture: ImageView = view.findViewById(R.id.ivPicture)
            val tvUsername: TextView = view.findViewById(R.id.tvUsername)
            val tvComment: TextView = view.findViewById(R.id.tvComment)
            val tvCreatedDate: TextView = view.findViewById(R.id.tvCreatedDate)
            if (subcomment.user.photo_url?.trim().isNullOrEmpty()) {
                Glide.with(context).load(funnyimgs[2]).into(ivPicture)
            } else {
                Glide.with(context).load(subcomment.user.photo_url).centerCrop().into(ivPicture)
            }
            tvUsername.text = subcomment.user.username
            tvComment.text = subcomment.messages
            try {
                if (!obj.created_at.isNullOrEmpty()) {
                    tvCreatedDate.text = Utils.convertToTimeText(subcomment.created_at!!, subcomment.current_datetime!!)
                } else {
                    tvCreatedDate.text = "null"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            holder.linSubcomments.addView(view)
        }

        try {
            if (!obj.created_at.isNullOrEmpty()) {
                holder.tvCreatedDate.text = Utils.convertToTimeText(obj.created_at!!, obj.current_datetime!!)
            } else {
                holder.tvCreatedDate.text = "null"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}