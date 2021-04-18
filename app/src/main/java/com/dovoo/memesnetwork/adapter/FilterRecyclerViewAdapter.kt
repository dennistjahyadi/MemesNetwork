package com.dovoo.memesnetwork.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.FilterRecyclerViewAdapter.MyViewHolder

class FilterRecyclerViewAdapter(
    private val activity: Activity,
    private val itemList: List<Map<String, Any>>
) : RecyclerView.Adapter<MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var linBtnSection: LinearLayout
        var tvSection: TextView

        init {
            linBtnSection = itemView.findViewById(R.id.linBtnSection)
            tvSection = itemView.findViewById(R.id.tvSection)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.row_filter, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        val obj = itemList[i]
        viewHolder.tvSection.text = obj["name"] as String?
        viewHolder.linBtnSection.setOnClickListener {
            val t: Thread = object : Thread() {
                override fun run() {
                    val data = Intent()
                    data.putExtra("name", obj["name"] as String?)
                    activity.setResult(Activity.RESULT_OK, data)
                    activity.finish()
                }
            }
            t.start()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}