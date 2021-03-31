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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dovoo.memesnetwork.BuildConfig;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.adapter.CommentHistoryRecyclerViewAdapter;
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentHistoryActivity extends AppCompatActivity {

    private CommentHistoryRecyclerViewAdapter commentHistoryRecyclerViewAdapter;
    private RecyclerView rvComment;
    private Toolbar toolbar;
    private List<Map<String, Object>> itemList = new ArrayList<>();
    private FrameLayout loadingBar;
    private LinearLayout linBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_history);
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        loadingBar = findViewById(R.id.loadingBar);
        linBtnBack = findViewById(R.id.linBtnBack);

        rvComment = findViewById(R.id.rvComment);
        linBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentHistoryRecyclerViewAdapter = new CommentHistoryRecyclerViewAdapter(getApplicationContext(), itemList);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setAdapter(commentHistoryRecyclerViewAdapter);
        rvComment.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchData(totalItemsCount);
            }
        });
        fetchData(0);
    }

    private void fetchData(int offset) {
        if(offset==0){
            itemList.clear();
        }
        loadingBar.setVisibility(View.VISIBLE);
        Map<String,String> param = new HashMap<>();
        param.put("offset", offset + "");
        param.put("user_id", SharedPreferenceUtils.getPrefs(getApplicationContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID,0)+"");
        AndroidNetworking.get(BuildConfig.API_URL + "commentsuserhistory")
                .addQueryParameter(param)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONArray result = response.getJSONArray("data");
                            String currentDatetime = response.getString("current_datetime");

                            for (int i = 0; i < result.length(); i++) {

                                itemList.add(Utils.toMap(result.getJSONObject(i),currentDatetime));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingBar.setVisibility(View.GONE);
                        commentHistoryRecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        System.out.print("a");
                        loadingBar.setVisibility(View.GONE);

                    }
                });
    }
}
