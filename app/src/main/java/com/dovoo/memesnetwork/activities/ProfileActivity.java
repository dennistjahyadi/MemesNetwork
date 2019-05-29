package com.dovoo.memesnetwork.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvBtnLike, tvBtnDislike, tvBtnComment, tvBtnPrivacyPolicy,tvBtnSubscription, tvUsername, tvBtnEditProfile, tvBtnLogout;
    private LinearLayout linBtnBack;
    private ImageView ivProfile;
    private AdView mAdView;

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
        tvBtnSubscription = findViewById(R.id.tvBtnSubscription);
        tvBtnLogout = findViewById(R.id.tvBtnLogout);
        tvUsername = findViewById(R.id.tvUsername);
        linBtnBack = findViewById(R.id.linBtnBack);
        ivProfile = findViewById(R.id.ivProfile);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        linBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvBtnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://68.183.159.197/privacypolicy/"));
                startActivity(browserIntent);
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

        tvBtnSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Coming soon",Toast.LENGTH_LONG).show();
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

    private void setupPayment(){


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
