package com.dovoo.memesnetwork.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.R;

import java.util.List;
import java.util.Map;

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.MyViewHolder> {

    private List<Map<String,Object>> itemList;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linBtnSection;
        TextView tvSection;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linBtnSection = itemView.findViewById(R.id.linBtnSection);
            tvSection = itemView.findViewById(R.id.tvSection);
        }
    }

    public SectionRecyclerViewAdapter(Context context, List<Map<String,Object>> itemList){
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_section, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        Map<String,Object> obj = itemList.get(i);
        viewHolder.tvSection.setText((String)obj.get("name"));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
