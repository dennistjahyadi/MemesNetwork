package com.dovoo.memesnetwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.LoginActivity;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvBtnLike,tvBtnDislike, tvBtnComment,tvUsername;
    private LinearLayout linBtnBack;
    private ImageView ivProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        tvBtnLike = findViewById(R.id.tvBtnLike);
        tvBtnDislike = findViewById(R.id.tvBtnDislike);
        tvBtnComment = findViewById(R.id.tvBtnComment);
        tvUsername = findViewById(R.id.tvUsername);
        linBtnBack = findViewById(R.id.linBtnBack);
        ivProfile = findViewById(R.id.ivProfile);
    }



}
