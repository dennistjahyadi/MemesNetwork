package com.dovoo.memesnetwork.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.activities.CommentActivity;
import com.dovoo.memesnetwork.adapter.holders.VideoViewHolder;
import com.dovoo.memesnetwork.adapter.items.BaseVideoItem;
import com.dovoo.memesnetwork.adapter.items.DirectLinkVideoItem;
import com.dovoo.memesnetwork.components.TextViewFaSolid;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.Map;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Map<String, Object>> itemList;
    private BaseVideoItem videoItem;
    private Context context;
    public SimpleExoPlayer player;
    float finalWidth;
    float finalHeight;
    float maxHeightVideo;

    public CommentRecyclerViewAdapter(Context context, List<Map<String, Object>> itemList,BaseVideoItem videoItem) {
        this.context = context;
        this.itemList = itemList;
        this.videoItem = videoItem;
        finalWidth = context.getResources().getDisplayMetrics().widthPixels;  // default phone width
        finalHeight = context.getResources().getDisplayMetrics().heightPixels; // default phone heights
        maxHeightVideo = (float) context.getResources().getDisplayMetrics().heightPixels * 0.8f; // set default maximum video size in phone
        player = ExoPlayerFactory.newSimpleInstance(context);
    }

    class MyViewHolderHeader extends RecyclerView.ViewHolder {
        public final RelativeLayout relativeLayout;
        public final PlayerView playerView;
        public final ImageView mCover;
        public final TextViewFaSolid tvIconSound;
        public final TextView tvTitle,tvLabelNoAudio;
        public MyViewHolderHeader(View view) {
            super(view);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvLabelNoAudio = view.findViewById(R.id.tvLabelNoAudio);
            playerView = view.findViewById(R.id.playerView);
            mCover = view.findViewById(R.id.cover);
            tvIconSound = view.findViewById(R.id.tvIconSound);
            playerView.setPlayer(player);

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
            if (videoItem.getContentHeight() > videoItem.getContentWidth()) {
                // if video is potrait
                float ratio = (float) videoItem.getContentHeight() / videoItem.getContentWidth();

                finalHeight = finalWidth * ratio;
                // if final height higher or same with phone height, we have to decrease it to make the video fit in phone. It will show around 3/4 phone screen
                if (finalHeight >= maxHeightVideo) {
                    finalHeight = maxHeightVideo * 0.7f;
                }

            } else if (videoItem.getContentHeight() < videoItem.getContentWidth()) {
                // if video is landscape
                float ratio = (float) videoItem.getContentWidth() / videoItem.getContentHeight();

                finalHeight = finalWidth / ratio;
            } else {
                // if video is square
                finalHeight = finalWidth;
            }

            ViewGroup.LayoutParams layoutParams = vhHeader.relativeLayout.getLayoutParams();
            layoutParams.width = (int) finalWidth;
            layoutParams.height = (int) finalHeight;
            vhHeader.relativeLayout.setLayoutParams(layoutParams);

            DirectLinkVideoItem directLinkVideoItem = (DirectLinkVideoItem) videoItem;

            vhHeader.tvTitle.setText(directLinkVideoItem.getmTitle());
//            vhHeader.mCover.setVisibility(View.VISIBLE);
//
//            Picasso.get().load(directLinkVideoItem.getmCoverUrl())
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .resize(0, vhHeader.mCover.getHeight())
//                    .into(vhHeader.mCover);
// Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "yourApplicationName"));
// This is the MediaSource representing the media to be played.

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(directLinkVideoItem.getmDirectUrl()));

// Prepare the player with the source.
            player.prepare(videoSource);
            player.setPlayWhenReady(true);

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

    private void sendComment(){

    }
}