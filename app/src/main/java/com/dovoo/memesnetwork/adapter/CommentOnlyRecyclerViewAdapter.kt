package com.dovoo.memesnetwork.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.model.Comment
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommentOnlyRecyclerViewAdapter(
    val context: Context,
    private val itemList: ArrayList<Comment>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val funnyimgs =
        intArrayOf(R.drawable.funny_user1, R.drawable.funny_user2, R.drawable.funny_user3)

    internal inner class MyViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView
        var tvComment: TextView
        var tvCreatedDate: TextView
        var ivPicture: ImageView
        var paddingBottom: FrameLayout

        init {
            tvUsername = itemView.findViewById(R.id.tvUsername)
            tvComment = itemView.findViewById(R.id.tvComment)
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate)
            ivPicture = itemView.findViewById(R.id.ivPicture)
            paddingBottom = itemView.findViewById(R.id.paddingBottom)
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
        if (position < itemList.size) {
            holder.paddingBottom.visibility = View.GONE
        } else {
            holder.paddingBottom.visibility = View.VISIBLE
        }

        //vhItem.tvUsername.setText((String)obj.get("created_by"));
        // vhItem.tvUsername.text = obj["created_by"] as String?
        holder.tvUsername.text = obj.user.username

        holder.tvComment.text = obj.messages
        if(obj.user.photo_url?.trim().isNullOrEmpty()){
            Glide.with(context).load(funnyimgs[2]).into(holder.ivPicture)
        }else{
            Glide.with(context).load(obj.user.photo_url).centerCrop().into(holder.ivPicture)
        }
        try {
            if (!obj.created_at.isNullOrEmpty()) {
                val createdAtDate = sdf.parse(obj.created_at)
                val currentDate = sdf.parse(obj.current_datetime)
                val createAtMiliseconds = createdAtDate.time
                val currentTimeMiliseconds = currentDate.time
                val thedate = DateUtils.getRelativeTimeSpanString(
                    createAtMiliseconds,
                    currentTimeMiliseconds,
                    DateUtils.MINUTE_IN_MILLIS
                )
                holder.tvCreatedDate.text = thedate
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