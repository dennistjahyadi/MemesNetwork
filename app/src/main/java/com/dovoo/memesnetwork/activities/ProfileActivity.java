package com.dovoo.memesnetwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvBtnLike, tvBtnDislike, tvBtnComment, tvBtnPrivacyPolicy, tvUsername, tvBtnEditProfile, tvBtnLogout;
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


        tvBtnEditProfile = findViewById(R.id.tvBtnEditProfile);
        tvBtnLike = findViewById(R.id.tvBtnLike);
        tvBtnDislike = findViewById(R.id.tvBtnDislike);
        tvBtnComment = findViewById(R.id.tvBtnComment);
        tvBtnPrivacyPolicy = findViewById(R.id.tvBtnPrivacyPolicy);
        tvBtnLogout = findViewById(R.id.tvBtnLogout);
        tvUsername = findViewById(R.id.tvUsername);
        linBtnBack = findViewById(R.id.linBtnBack);
        ivProfile = findViewById(R.id.ivProfile);

        linBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfileEditActivity.class);
                startActivity(i);
            }
        });

        tvBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),LikeHistoryActivity.class);
                startActivity(i);
            }
        });

        tvBtnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),DislikeHistoryActivity.class);
                startActivity(i);
            }
        });

        tvBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),CommentHistoryActivity.class);
                startActivity(i);
            }
        });
        tvBtnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (GlobalFunc.mGoogleSignInClient != null) {
                    doGoogleLogout();
                }else{
                    GlobalFunc.logout(ProfileActivity.this);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = SharedPreferenceUtils.getPrefs(getApplicationContext()).getString(SharedPreferenceUtils.PREFERENCES_USER_NAME, "");
        tvUsername.setText(username);
        String userPhotoUrl = SharedPreferenceUtils.getPrefs(getApplicationContext()).getString(SharedPreferenceUtils.PREFERENCES_USER_PHOTO_URL, "");
        if (!userPhotoUrl.equals("")) {
            Picasso.get().load(userPhotoUrl).into(ivProfile);
        } else {
            Picasso.get().load(R.drawable.funny_user2).into(ivProfile);
        }
    }

    private void doGoogleLogout() {
        GlobalFunc.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        GlobalFunc.logout(ProfileActivity.this);
                    }
                });
    }


}
