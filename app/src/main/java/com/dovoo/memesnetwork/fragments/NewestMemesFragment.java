package com.dovoo.memesnetwork.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.adapter.VideoRecyclerViewAdapter;
import com.dovoo.memesnetwork.adapter.items.BaseVideoItem;
import com.dovoo.memesnetwork.adapter.items.DirectLinkVideoItem;
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener;
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewestMemesFragment extends Fragment {

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
    private FrameLayout loadingBar;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    private String section = null;
    private AdView mAdView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            section = bundle.getString("section", null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container, false);
        loadingBar = view.findViewById(R.id.loadingBar);

        rvMemes = view.findViewById(R.id.rvMemes);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fab = view.findViewById(R.id.fab);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvMemes.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        rvMemes.setLayoutManager(mLayoutManager);

        videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, getActivity(), loadingBar, mList);

        rvMemes.setAdapter(videoRecyclerViewAdapter);

        rvMemes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if (!mList.isEmpty()) {
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
                if (!mList.isEmpty()) {
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

        rvMemes.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchData(totalItemsCount);
            }
        });


        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, rvMemes);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setEnabled(false);
                        fetchData(0);
                    }
                }
        );

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.SmoothScroller smoothScrollerToTop = new LinearSmoothScroller(getContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
                smoothScrollerToTop.setTargetPosition(0);

                mLayoutManager.startSmoothScroll(smoothScrollerToTop);
            }
        });

        new SimpleMainThreadMediaPlayerListener() {
            @Override
            public void onErrorMainThread(int what, int extra) {
                mVideoPlayerManager.resetMediaPlayer();
            }
        };

        fetchData(0);

        return view;
    }


    private void fetchData(int offset) {
        loadingBar.setVisibility(View.VISIBLE);

//        for (int i = 0; i < 100; i++) {
//         //   mList.add(new DirectLinkVideoItem("asddas", "https://img-9gag-fun.9cache.com/photo/aA3yqzp_460sv.mp4", mVideoPlayerManager, Picasso.get(), "https://img-9gag-fun.9cache.com/photo/aA3yqzp_460s.jpg"));
//            mList.add(new DirectLinkVideoItem("asddas", "http://192.168.1.8:8000/sources/apm9AD8_460sv.mp4", mVideoPlayerManager, Picasso.get(), "http://192.168.1.8:8000/sources/apm9AD8_460s.jpg"));
//
//        }
        if (offset == 0) {
            mList.clear();
        }

        Map<String, String> param = new HashMap<>();
        param.put("offset", offset + "");
        param.put("user_id", SharedPreferenceUtils.getPrefs(getContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0) + "");

        if (section != null && !section.equalsIgnoreCase("all")) {
            param.put("post_section", section);
        }

        AndroidNetworking.get(Utils.API_URL + "index")
                .addQueryParameter(param)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject result = response.getJSONObject(i);
                                Integer id = result.getInt("id");
                                String title = result.getString("title");
                                String type = result.getString("type");
                                JSONObject imagesObject = new JSONObject(result.getString("images"));
                                String coverUrl = imagesObject.getJSONObject("image700").getString("url");
                                String category = result.getString("post_section");
                                String videoUrl = null;
                                boolean isVideo = false;
                                boolean hasAudio = false;
                                if (type.equalsIgnoreCase("animated")) {
                                    isVideo = true;
                                    videoUrl = imagesObject.getJSONObject("image460sv").getString("url");
                                    hasAudio = (imagesObject.getJSONObject("image460sv").getInt("hasAudio") == 1 ? true : false);
                                }

                                int width = imagesObject.getJSONObject("image700").getInt("width");
                                int height = imagesObject.getJSONObject("image700").getInt("height");
                                Map<String, Object> data = new HashMap<>();
                                data.put("total_like", result.get("total_like"));
                                data.put("total_dislike", result.get("total_dislike"));
                                data.put("total_comment", result.get("total_comment"));
                                data.put("is_liked", result.get("is_liked"));

                                mList.add(new DirectLinkVideoItem(id, category, title, videoUrl, data, mVideoPlayerManager, Picasso.get(), coverUrl, width, height, hasAudio, isVideo));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingBar.setVisibility(View.GONE);


                        swipeRefreshLayout.setEnabled(true);
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
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        System.out.print("a");
                        loadingBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);

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

    public MetaData met;
    public VideoPlayerView videoPlayerView;
    public String videoUrl;

    @Override
    public void onResume() {
        super.onResume();
        if(videoRecyclerViewAdapter.getItemCount()>0){
            resumeVideoUsingStupidMethod();
        }
    }

    private void resumeVideoUsingStupidMethod() {
        // i just don't know to play the video,
        // this shit did the trick. just do fast scroll (LOL)
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rvMemes.scrollBy(0, 1300);
        rvMemes.scrollBy(0, -1300);

    }


    @Override
    public void onPause() {
        super.onPause();
        mVideoPlayerManager.stopAnyPlayback();
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }

}
