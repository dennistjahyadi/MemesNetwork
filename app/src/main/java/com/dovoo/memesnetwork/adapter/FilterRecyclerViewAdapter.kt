package com.dovoo.memesnetwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.FilterRecyclerViewAdapter.MyViewHolder
import com.dovoo.memesnetwork.model.Section

class FilterRecyclerViewAdapter(
    private val context: Context,
    private val itemList: List<Section>,
    private val onClickListener: View.OnClickListener
) : RecyclerView.Adapter<MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var linBtnSection: LinearLayout
        var tvSection: TextView
        lateinit var data: Section

        init {
            linBtnSection = itemView.findViewById(R.id.linBtnFilter)
            tvSection = itemView.findViewById(R.id.tvSection)
            linBtnSection.tag = this
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.row_filter, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        val obj = itemList[i]
        viewHolder.data = obj
        viewHolder.tvSection.text = obj.name
        viewHolder.linBtnSection.setOnClickListener(onClickListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}