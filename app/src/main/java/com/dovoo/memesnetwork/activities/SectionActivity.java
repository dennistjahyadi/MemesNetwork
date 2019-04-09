package com.dovoo.memesnetwork.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.adapter.SectionRecyclerViewAdapter;
import com.dovoo.memesnetwork.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SectionActivity extends AppCompatActivity {
    private RecyclerView rvSection;
    private Toolbar toolbar;
    private SectionRecyclerViewAdapter sectionRecyclerViewAdapter;
    private LinearLayout linBtnBack;
    private List<Map<String, Object>> itemList = new ArrayList<>();

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

        rvSection = findViewById(R.id.rvSection);
        linBtnBack = findViewById(R.id.linBtnBack);

        sectionRecyclerViewAdapter = new SectionRecyclerViewAdapter(getApplicationContext(), itemList);
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

        AndroidNetworking.get(Utils.API_URL + "sections")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject result = response.getJSONObject(i);
                                itemList.add(Utils.toMap(result));
                            }

                            sectionRecyclerViewAdapter.notifyDataSetChanged();
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
}
