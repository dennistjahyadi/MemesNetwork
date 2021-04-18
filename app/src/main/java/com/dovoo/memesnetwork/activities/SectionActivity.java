package com.dovoo.memesnetwork.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.adapter.FilterRecyclerViewAdapter;
import com.dovoo.memesnetwork.utils.AdUtils;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SectionActivity extends AppCompatActivity {
    private RecyclerView rvSection;
    private Toolbar toolbar;
    private FilterRecyclerViewAdapter sectionRecyclerViewAdapter;
    private LinearLayout linBtnBack;
    private List<Map<String, Object>> itemList = new ArrayList<>();
    private AdView mAdView;
    public FrameLayout loadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mAdView = findViewById(R.id.adView);
        AdUtils.loadAds(getApplicationContext(),mAdView);


        rvSection = findViewById(R.id.rvSection);
        linBtnBack = findViewById(R.id.linBtnBack);
        loadingBar = findViewById(R.id.loadingBar);

//        sectionRecyclerViewAdapter = new FilterRecyclerViewAdapter(this, itemList, sectionO);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvSection.setAdapter(sectionRecyclerViewAdapter);
        rvSection.setLayoutManager(layoutManager);

        linBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fetchData();
    }



    private void fetchData() {
        loadingBar.setVisibility(View.VISIBLE);
//        AndroidNetworking.get(BuildConfig.API_URL + "sections")
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONArray(new JSONArrayRequestListener() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        // do anything with response
//                        try {
//                            Map<String,Object> map = new HashMap<>();
//                            map.put("name","ALL");
//                            itemList.add(map);
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject result = response.getJSONObject(i);
//                                itemList.add(Utils.toMap(result));
//                            }
//
//                            sectionRecyclerViewAdapter.notifyDataSetChanged();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        loadingBar.setVisibility(View.GONE);
//
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//                        loadingBar.setVisibility(View.GONE);
//                    }
//                });
    }
}
