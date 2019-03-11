package com.example.acer.memesnetwork.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.acer.memesnetwork.R;
import com.example.acer.memesnetwork.adapter.CommentRecyclerViewAdapter;
import com.example.acer.memesnetwork.adapter.VideoRecyclerViewAdapter;
import com.example.acer.memesnetwork.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private RecyclerView rvComment;
    private EditText etComment;
    private Button btnSend;
    private List<Map<String, Object>> itemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_comment);
        init();
    }

    private void init() {
        etComment = findViewById(R.id.etComment);
        btnSend = findViewById(R.id.btnSend);
        rvComment = findViewById(R.id.rvComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(getApplicationContext(), itemList, VideoRecyclerViewAdapter.currentVideoItem);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setAdapter(commentRecyclerViewAdapter);


        fetchData();
    }

    private void fetchData() {

    }

    public void sendComment() {
        final JSONObject jsonObject = new JSONObject();
        try {
           // jsonObject.put("meme_id", etLogin.getText().toString());
           // jsonObject.put("user_id", );
            jsonObject.put("messages", etComment.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(Utils.API_URL + "")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject result = response.getJSONObject(i);


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
}
