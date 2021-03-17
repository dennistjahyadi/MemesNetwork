package com.volokh.danylo.video_player_manager.manager;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

/**
 * This is basic interface for Items in Adapter of the list. Regardless of is it {@link android.widget.ListView}
 * or {@link androidx.recyclerview.widget.RecyclerView}
 */
public interface VideoItem {
   // void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager);
    void playNewVideo(MetaData currentItemMetaData, View newActiveView, VideoPlayerManager<MetaData> videoPlayerManager);
    void stopPlayback(VideoPlayerManager videoPlayerManager);
}
