package com.dovoo.memesnetwork.testing;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.adapter.MemesRecyclerViewAdapter;
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest;
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.ene.toro.widget.Container;
import im.ene.toro.widget.PressablePlayerSelector;

public class TestingActivity extends AppCompatActivity {
    private Container container;
    private LinearLayoutManager layoutManager;
    private MemesRecyclerViewAdapter adapter;
    private PressablePlayerSelector selector;
    private List<DirectLinkItemTest> directLinkItemTestList = new ArrayList<>();
    private String section = null;
    private FrameLayout loadingBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        container = findViewById(R.id.player_container);
        loadingBar = findViewById(R.id.loadingBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fab = findViewById(R.id.fab);

        layoutManager = new LinearLayoutManager(this);
        container.setLayoutManager(layoutManager);
        selector = new PressablePlayerSelector(container);
        container.setPlayerSelector(selector);

        adapter = new MemesRecyclerViewAdapter(getApplicationContext(), selector, directLinkItemTestList, loadingBar);
        container.setAdapter(adapter);
        container.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchData(totalItemsCount);
            }
        });
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
                RecyclerView.SmoothScroller smoothScrollerToTop = new LinearSmoothScroller(getApplicationContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
                smoothScrollerToTop.setTargetPosition(0);

                layoutManager.startSmoothScroll(smoothScrollerToTop);
            }
        });
        fetchData(0);
    }

    private void fetchData(int offset) {
        loadingBar.setVisibility(View.VISIBLE);

//        for (int i = 0; i < 100; i++) {
//         //   mList.add(new DirectLinkVideoItem("asddas", "https://img-9gag-fun.9cache.com/photo/aA3yqzp_460sv.mp4", mVideoPlayerManager, Picasso.get(), "https://img-9gag-fun.9cache.com/photo/aA3yqzp_460s.jpg"));
//            mList.add(new DirectLinkVideoItem("asddas", "http://192.168.1.8:8000/sources/apm9AD8_460sv.mp4", mVideoPlayerManager, Picasso.get(), "http://192.168.1.8:8000/sources/apm9AD8_460s.jpg"));
//
//        }
        if (offset == 0) {
            directLinkItemTestList.clear();
        }

        Map<String, String> param = new HashMap<>();
        param.put("offset", offset + "");
        param.put("user_id", SharedPreferenceUtils.getPrefs(getApplicationContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0) + "");

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

                                directLinkItemTestList.add(new DirectLinkItemTest(id, category, title, videoUrl, data, Picasso.get(), coverUrl, width, height, hasAudio, isVideo));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setEnabled(true);
                        adapter.notifyDataSetChanged();
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


    @Override
    protected void onDestroy() {
        layoutManager = null;
        adapter = null;
        selector = null;
        super.onDestroy();
    }
}
