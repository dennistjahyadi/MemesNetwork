package com.dovoo.memesnetwork.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.adapter.CommentRecyclerViewAdapter;
import com.dovoo.memesnetwork.adapter.VideoRecyclerViewAdapter;
import com.dovoo.memesnetwork.adapter.items.DirectLinkVideoItem;
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private RecyclerView rvComment;
    private EditText etComment;
    private Toolbar toolbar;
    private Button btnSend;
    private List<Map<String, Object>> itemList = new ArrayList<>();
    private FrameLayout loadingBar;
    private Integer memeId;
    private LinearLayout linBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        memeId = getIntent().getIntExtra("meme_id",0);
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        loadingBar = findViewById(R.id.loadingBar);
        linBtnBack = findViewById(R.id.linBtnBack);
        etComment = findViewById(R.id.etComment);
        btnSend = findViewById(R.id.btnSend);
        rvComment = findViewById(R.id.rvComment);
        linBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(getApplicationContext(), itemList, VideoRecyclerViewAdapter.currentVideoItem);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setAdapter(commentRecyclerViewAdapter);
        rvComment.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchData(totalItemsCount-1,false);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    private void fetchData(int offset, final boolean scrollToLastComment) {
        if(offset==0){
            itemList.clear();
        }
        loadingBar.setVisibility(View.VISIBLE);
        Map<String,String> param = new HashMap<>();
        param.put("offset", offset + "");
        AndroidNetworking.get(Utils.API_URL + "comments")
                .addQueryParameter(param)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONArray result = response.getJSONArray("data");
                            for (int i = 0; i < result.length(); i++) {
                                itemList.add(Utils.toMap(result.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(scrollToLastComment){
                            rvComment.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Call smooth scroll
                                    rvComment.smoothScrollToPosition(1);
                                }
                            });
                        }
                        loadingBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        System.out.print("a");
                        loadingBar.setVisibility(View.GONE);

                    }
                });
    }

    public void sendComment() {
        btnSend.setEnabled(false);
        loadingBar.setVisibility(View.VISIBLE);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meme_id", memeId);
            jsonObject.put("user_id",SharedPreferenceUtils.getPrefs(getApplicationContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID,0));
            jsonObject.put("messages", etComment.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(Utils.API_URL + "sendcomment")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        commentRecyclerViewAdapter.notifyDataSetChanged();
                        loadingBar.setVisibility(View.GONE);
                        btnSend.setEnabled(true);
                        etComment.setText("");
                        Utils.hideKeyboard(CommentActivity.this);
                        fetchData(0,true);
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        loadingBar.setVisibility(View.GONE);
                        btnSend.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(commentRecyclerViewAdapter.player!=null) {
            commentRecyclerViewAdapter.player.stop(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(commentRecyclerViewAdapter.player!=null) {
            commentRecyclerViewAdapter.player.stop(true);
        }
    }
}