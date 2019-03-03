package com.example.acer.memesnetwork.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.acer.memesnetwork.R;

import java.util.List;
import java.util.Map;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Map<String, Object>> itemList;
    private Context context;

    public CommentRecyclerViewAdapter(Context context, List<Map<String, Object>> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    class MyViewHolderHeader extends RecyclerView.ViewHolder {
        public MyViewHolderHeader(View itemView) {
            super(itemView);

        }
    }

    class MyViewHolderItem extends RecyclerView.ViewHolder {
        public MyViewHolderItem(View itemView) {
            super(itemView);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_header_comment, parent, false);
            return new MyViewHolderHeader(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
            return new MyViewHolderItem(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolderHeader) {
            MyViewHolderHeader vhHeader = (MyViewHolderHeader) holder;

        } else if (holder instanceof MyViewHolderItem) {
            final Map<String, Object> obj = itemList.get(position-1);
            MyViewHolderItem vhItem = (MyViewHolderItem) holder;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == TYPE_HEADER;
    }

    @Override
    public int getItemCount() {
        return itemList.size() + 1;
    }
}