package com.example.acer.memesnetwork.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.memesnetwork.adapter.holders.VideoViewHolder;
import com.example.acer.memesnetwork.adapter.items.BaseVideoItem;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

import java.util.List;

/**
 * Created by danylo.volokh on 9/20/2015.
 */
public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private final VideoPlayerManager mVideoPlayerManager;
    private final List<BaseVideoItem> mList;
    private final Context mContext;
    float finalWidth;
    float finalHeight;

    float maxHeightVideo;

    public VideoRecyclerViewAdapter(VideoPlayerManager videoPlayerManager, Context context, List<BaseVideoItem> list) {
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
        finalWidth = mContext.getResources().getDisplayMetrics().widthPixels;  // default phone width
        finalHeight = mContext.getResources().getDisplayMetrics().heightPixels; // default phone heights
        maxHeightVideo = (float) mContext.getResources().getDisplayMetrics().heightPixels * 0.8f; // set default maximum video size in phone
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        BaseVideoItem videoItem = mList.get(position);
        View resultView = videoItem.createView(viewGroup, mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);
        return new VideoViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder viewHolder, int position) {
        BaseVideoItem videoItem = mList.get(position);

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

        ViewGroup.LayoutParams layoutParams = viewHolder.relativeLayout.getLayoutParams();
        layoutParams.width = (int) finalWidth;
        layoutParams.height = (int) finalHeight;
        viewHolder.relativeLayout.setLayoutParams(layoutParams);
//        ViewGroup.LayoutParams layoutParams2 = viewHolder.mCover.getLayoutParams();
//        layoutParams2.width = (int) finalWidth;
//        layoutParams2.height = (int) finalHeight;
//        viewHolder.mCover.setLayoutParams(layoutParams2);
        videoItem.update(position, viewHolder, mVideoPlayerManager);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}