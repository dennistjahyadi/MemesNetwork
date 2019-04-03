package com.dovoo.memesnetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dovoo.memesnetwork.adapter.VideoRecyclerViewAdapter;
import com.dovoo.memesnetwork.adapter.items.BaseVideoItem;
import com.dovoo.memesnetwork.adapter.items.DirectLinkVideoItem;
import com.dovoo.memesnetwork.utils.Utils;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private final ArrayList<BaseVideoItem> mList = new ArrayList<>();

    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */
    private final ListItemsVisibilityCalculator mVideoVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    private RecyclerView rvMemes;
    private LinearLayoutManager mLayoutManager;
    private VideoRecyclerViewAdapter videoRecyclerViewAdapter;
    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private ItemsPositionGetter mItemsPositionGetter;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        rvMemes = findViewById(R.id.rvMemes);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvMemes.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rvMemes.setLayoutManager(mLayoutManager);

        videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, MainActivity.this, mList);

        rvMemes.setAdapter(videoRecyclerViewAdapter);

        rvMemes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if(!mList.isEmpty()){
                    // need to call this method from list view handler in order to have filled list
                    rvMemes.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mVideoVisibilityCalculator.onScrollStateIdle(
                                        mItemsPositionGetter,
                                        mLayoutManager.findFirstVisibleItemPosition(),
                                        mLayoutManager.findLastVisibleItemPosition());
                            } catch (ArrayIndexOutOfBoundsException e) {

                            }
                        }
                    });

                    mScrollState = scrollState;
                    try {
                        mVideoVisibilityCalculator.onScrollStateIdle(
                                mItemsPositionGetter,
                                mLayoutManager.findFirstVisibleItemPosition(),
                                mLayoutManager.findLastVisibleItemPosition());
                    } catch (ArrayIndexOutOfBoundsException e) {

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    // need to call this method from list view handler in order to have filled list
                    rvMemes.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mVideoVisibilityCalculator.onScrollStateIdle(
                                        mItemsPositionGetter,
                                        mLayoutManager.findFirstVisibleItemPosition(),
                                        mLayoutManager.findLastVisibleItemPosition());
                            } catch (ArrayIndexOutOfBoundsException e) {

                            }
                        }
                    });

                    try {
                        mVideoVisibilityCalculator.onScrollStateIdle(
                                mItemsPositionGetter,
                                mLayoutManager.findFirstVisibleItemPosition(),
                                mLayoutManager.findLastVisibleItemPosition());
                    } catch (ArrayIndexOutOfBoundsException e) {

                    }
                }
            }
        });
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, rvMemes);

        fetchData();
    }

    private void fetchData() {


//        for (int i = 0; i < 100; i++) {
//         //   mList.add(new DirectLinkVideoItem("asddas", "https://img-9gag-fun.9cache.com/photo/aA3yqzp_460sv.mp4", mVideoPlayerManager, Picasso.get(), "https://img-9gag-fun.9cache.com/photo/aA3yqzp_460s.jpg"));
//            mList.add(new DirectLinkVideoItem("asddas", "http://192.168.1.8:8000/sources/apm9AD8_460sv.mp4", mVideoPlayerManager, Picasso.get(), "http://192.168.1.8:8000/sources/apm9AD8_460s.jpg"));
//
//        }
        AndroidNetworking.get(Utils.API_URL + "index")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject result = response.getJSONObject(i);
                                String title = result.getString("title");
                                String type =  result.getString("type");
                                JSONObject imagesObject = new JSONObject(result.getString("images"));
                                String coverUrl = imagesObject.getJSONObject("image700").getString("url");
                                String category = result.getString("post_section");

                                String videoUrl = null;
                                boolean isVideo = false;
                                boolean hasAudio = false;
                                if(type.equalsIgnoreCase("animated")){
                                    isVideo = true;
                                    videoUrl = imagesObject.getJSONObject("image460sv").getString("url");
                                    hasAudio = (imagesObject.getJSONObject("image460sv").getInt("hasAudio")==1 ? true : false);
                                }

                                int width = imagesObject.getJSONObject("image700").getInt("width");
                                int height = imagesObject.getJSONObject("image700").getInt("height");

                                mList.add(new DirectLinkVideoItem(category, title, Utils.SOURCE_URL + videoUrl, mVideoPlayerManager, Picasso.get(), Utils.SOURCE_URL + coverUrl, width, height,hasAudio,isVideo));
                            }

                            videoRecyclerViewAdapter.notifyDataSetChanged();
                            if (!mList.isEmpty()) {
                                rvMemes.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mVideoVisibilityCalculator.onScrollStateIdle(
                                                mItemsPositionGetter,
                                                mLayoutManager.findFirstVisibleItemPosition(),
                                                mLayoutManager.findLastVisibleItemPosition());

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }


    private void fetchData2() {
        AndroidNetworking.get(Utils.API_URL + "index")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject result = response.getJSONObject(i);
                                String title = result.getString("title");

                                JSONObject imagesObject = new JSONObject(result.getString("images"));
                                String coverUrl = imagesObject.getJSONObject("image700").getString("url");
                                String videoUrl = imagesObject.getJSONObject("image460sv").getString("url");
                                //    mList.add(new DirectLinkVideoItem(title, Utils.API_URL + "sources/" + videoUrl, mVideoPlayerManager, Picasso.get(), Utils.API_URL + "sources/" + coverUrl));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }
}
