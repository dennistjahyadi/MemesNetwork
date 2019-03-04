package com.example.acer.memesnetwork.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.acer.memesnetwork.R;
import com.example.acer.memesnetwork.adapter.holders.VideoViewHolder;
import com.example.acer.memesnetwork.adapter.items.DirectLinkVideoItem;
import com.example.acer.memesnetwork.components.TextViewFaSolid;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.List;
import java.util.Map;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Map<String, Object>> itemList;
    private Context context;
    float finalWidth;
    float finalHeight;

    float maxHeightVideo;
    public CommentRecyclerViewAdapter(Context context, List<Map<String, Object>> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    class MyViewHolderHeader extends RecyclerView.ViewHolder {
        public final RelativeLayout relativeLayout;
        public final VideoPlayerView mPlayer;
        public final ImageView mCover;
        public final TextViewFaSolid tvIconSound;
        public final TextView tvTitle,tvLabelNoAudio;
        public MyViewHolderHeader(View view) {
            super(view);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvLabelNoAudio = view.findViewById(R.id.tvLabelNoAudio);
            mPlayer = view.findViewById(R.id.player);
            mCover = view.findViewById(R.id.cover);
            tvIconSound = view.findViewById(R.id.tvIconSound);
            mPlayer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!mPlayer.hasAudio()){
                        return false;
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        if (mPlayer.isAllVideoMute()) {
                            mPlayer.unMuteVideo();
                            tvIconSound.setText(v.getContext().getResources().getText(R.string.fa_volume_up));
                        } else {
                            mPlayer.muteVideo();
                            tvIconSound.setText(v.getContext().getResources().getText(R.string.fa_volume_mute));
                        }
                    }
                    return true;
                }
            });

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
//            if (((MyViewHolderHeader) holder).mPlayer.getContentHeight() > videoItem.getContentWidth()) {
//                // if video is potrait
//                float ratio = (float) videoItem.getContentHeight() / videoItem.getContentWidth();
//
//                finalHeight = finalWidth * ratio;
//                // if final height higher or same with phone height, we have to decrease it to make the video fit in phone. It will show around 3/4 phone screen
//                if (finalHeight >= maxHeightVideo) {
//                    finalHeight = maxHeightVideo * 0.7f;
//                }
//
//            } else if (videoItem.getContentHeight() < videoItem.getContentWidth()) {
//                // if video is landscape
//                float ratio = (float) videoItem.getContentWidth() / videoItem.getContentHeight();
//
//                finalHeight = finalWidth / ratio;
//            } else {
//                // if video is square
//                finalHeight = finalWidth;
//            }
//
//            ViewGroup.LayoutParams layoutParams = viewHolder.relativeLayout.getLayoutParams();
//            layoutParams.width = (int) finalWidth;
//            layoutParams.height = (int) finalHeight;
//            viewHolder.relativeLayout.setLayoutParams(layoutParams);
//
//            DirectLinkVideoItem directLinkVideoItem = (DirectLinkVideoItem) videoItem;
//
//            viewHolder.tvTitle.setText(directLinkVideoItem.getmTitle());
//            viewHolder.mCover.setVisibility(View.VISIBLE);
//            directLinkVideoItem.getmImageLoader().load(directLinkVideoItem.getmCoverUrl()).into(viewHolder.mCover);

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