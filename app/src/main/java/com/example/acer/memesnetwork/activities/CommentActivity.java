package com.example.acer.memesnetwork.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.example.acer.memesnetwork.R;
import com.example.acer.memesnetwork.adapter.CommentRecyclerViewAdapter;
import com.example.acer.memesnetwork.adapter.VideoRecyclerViewAdapter;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private RecyclerView rvComment;
    private List<Map<String,Object>> itemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_comment);
        init();
    }

    private void init(){

        rvComment = findViewById(R.id.rvComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(getApplicationContext(),itemList,VideoRecyclerViewAdapter.currentVideoItem);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setAdapter(commentRecyclerViewAdapter);


        fetchData();
    }

    private void fetchData(){

    }
}
