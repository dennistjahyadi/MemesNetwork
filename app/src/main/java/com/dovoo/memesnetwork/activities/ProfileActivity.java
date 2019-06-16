package com.dovoo.memesnetwork.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.vending.billing.IInAppBillingService;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.billing.BillingManager;
import com.dovoo.memesnetwork.utils.AdUtils;
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements PurchasesUpdatedListener, BillingManager.BillingUpdatesListener {

    private static final String TAG = "Purchasing";
    private IInAppBillingService mService;

    private TextView tvBtnLike, tvBtnDislike, tvBtnComment, tvBtnPrivacyPolicy, tvBtnSubscription, tvUsername, tvBtnEditProfile, tvBtnLogout;
    private LinearLayout linBtnBack;
    private ImageView ivProfile;
    private AdView mAdView;
    private BillingClient mBillingClient;
    private boolean mIsServiceConnected;
    private BillingClient billingClient;
    private ServiceConnection mServiceConn;
    private List<String> skuList = new ArrayList<String>() {
        {
            add("premium_member");
        }
    };
    private BillingManager billingManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }


    private void init() {
        setupBillingService();
        setupBillingClient();

        billingManager = new BillingManager(this);

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
                Intent i = new Intent(getApplicationContext(), LikeHistoryActivity.class);
                startActivity(i);
            }
        });

        tvBtnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DislikeHistoryActivity.class);
                startActivity(i);
            }
        });

        tvBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CommentHistoryActivity.class);
                startActivity(i);
            }
        });

        tvBtnSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"Coming soon",Toast.LENGTH_LONG).show();
                subscribe();
            }
        });

        tvBtnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (GlobalFunc.mGoogleSignInClient != null) {
                    doGoogleLogout();
                } else {
                    GlobalFunc.logout(ProfileActivity.this);
                }
            }
        });

    }

    private void setupBillingService() {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                checkMemberIsPremium();
            }
        };

        Intent i = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        i.setPackage("com.android.vending");

        bindService(i,
                mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    System.out.println("BILLING | startConnection | RESULT OK");
                } else {
                    System.out.println("BILLING | startConnection | RESULT: " + billingResult.getResponseCode());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                System.out.println("BILLING | onBillingServiceDisconnected | DISCONNECTED");
            }
        });

    }

    private void subscribe() {
        if (billingClient.isReady()) {
            SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.SUBS).build();

            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initProductAdapter(skuDetailsList);
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Billing not ready", Toast.LENGTH_LONG).show();
        }
    }

    public void checkMemberIsPremium() {
        boolean isPremiumMember = false;

        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList ownedSkus =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String sku = (String) ownedSkus.get(i);
                    if (sku.equals("premium_member")) {
                        isPremiumMember = true;
                    }
                }

                // if continuationToken != null, call getPurchases again
                // and pass in the token to retrieve more items
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        billingManager.updateMemberStatus(getApplicationContext(), isPremiumMember);

    }

    private void initProductAdapter(List<SkuDetails> skuDetailsList) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(0)).build();
        billingClient.launchBillingFlow(this, billingFlowParams);
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

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        boolean isPremiumMember = false;
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals("premium_member")) {
                    isPremiumMember = true;
                }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
        } else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + billingResult.getResponseCode());
        }
        billingManager.updateMemberStatus(getApplicationContext(), isPremiumMember);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    public void onSubscriptionPurchaseUpdated() {
        boolean isPremiumMember = SharedPreferenceUtils.getPrefs(getApplicationContext()).getBoolean(SharedPreferenceUtils.PREFERENCES_PREMIUM_MEMBER, false);
        if (isPremiumMember) {
            tvBtnSubscription.setText("Thank you, You're Premium Member");
            tvBtnSubscription.setEnabled(false);
            mAdView.setVisibility(View.GONE);
        } else {
            tvBtnSubscription.setText("Help the Developer (Remove Ads)");
            tvBtnSubscription.setEnabled(true);
        }
        AdUtils.loadAds(getApplicationContext(),mAdView);
    }
}
