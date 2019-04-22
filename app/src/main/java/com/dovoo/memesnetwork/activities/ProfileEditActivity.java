package com.dovoo.memesnetwork.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.R;

public class ProfileEditActivity extends AppCompatActivity {

    private LinearLayout linBtnBack;
    private TextView tvBtnChangeProfilePic,tvBtnSave;
    private TextInputLayout tilUsername;
    private EditText etUsername;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        linBtnBack = findViewById(R.id.linBtnBack);
        tvBtnChangeProfilePic = findViewById(R.id.tvBtnChangeProfilePic);
        tvBtnSave = findViewById(R.id.tvBtnSave);
        tilUsername = findViewById(R.id.tilUsername);
        etUsername = findViewById(R.id.etUsername);

        tilUsername.setError("error test");

        linBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvBtnChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tvBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
