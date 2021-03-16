package com.dovoo.memesnetwork.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dovoo.memesnetwork.R;

import java.util.List;
import java.util.Map;

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.MyViewHolder> {

    private List<Map<String, Object>> itemList;
    private Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linBtnSection;
        TextView tvSection;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linBtnSection = itemView.findViewById(R.id.linBtnSection);
            tvSection = itemView.findViewById(R.id.tvSection);
        }
    }

    public SectionRecyclerViewAdapter(Activity activity, List<Map<String, Object>> itemList) {
        this.activity = activity;
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
        final Map<String, Object> obj = itemList.get(i);
        viewHolder.tvSection.setText((String) obj.get("name"));
        viewHolder.linBtnSection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Thread t = new Thread() {
                    public void run() {
                        Intent data = new Intent();
                        data.putExtra("name", (String) obj.get("name"));
                        activity.setResult(Activity.RESULT_OK, data);
                        activity.finish();
                    }
                };
                t.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
