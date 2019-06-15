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
import android.widget.FrameLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener;
import com.dovoo.memesnetwork.components.MyLinearLayoutManager;
import com.dovoo.memesnetwork.testing.DirectLinkItemTest;
import com.dovoo.memesnetwork.testing.TestingAdapter;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

public class NewestMemesFragment extends Fragment {

    private FrameLayout loadingBar;
    private String section = null;
    private AdView mAdView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    private Container container;
    private MyLinearLayoutManager layoutManager;
    private TestingAdapter adapter;
    private PressablePlayerSelector selector;
    private List<DirectLinkItemTest> directLinkItemTestList = new ArrayList<>();

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, viewGroup, false);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        container = view.findViewById(R.id.player_container);
        loadingBar = view.findViewById(R.id.loadingBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fab = view.findViewById(R.id.fab);

        layoutManager = new MyLinearLayoutManager(getContext());

        container.setLayoutManager(layoutManager);
        selector = new PressablePlayerSelector(container);
        container.setPlayerSelector(selector);

        adapter = new TestingAdapter(getContext(), selector, directLinkItemTestList,loadingBar);
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
                       //swipeRefreshLayout.setEnabled(false);
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

                layoutManager.startSmoothScroll(smoothScrollerToTop);
            }
        });
        fetchData(0);

        return view;
    }


    private void fetchData(int offset) {
        loadingBar.setVisibility(View.VISIBLE);

        if (offset == 0) {
            directLinkItemTestList.clear();
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

                                directLinkItemTestList.add(new DirectLinkItemTest(id, category, title, videoUrl, data, Picasso.get(), coverUrl, width, height, hasAudio, isVideo));
                            }
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setEnabled(true);
                            swipeRefreshLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingBar.setVisibility(View.GONE);
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
    public void onDestroyView() {
        layoutManager = null;
        adapter = null;
        selector = null;
        super.onDestroyView();
    }

}
