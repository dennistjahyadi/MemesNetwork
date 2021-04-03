package com.dovoo.memesnetwork.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dovoo.memesnetwork.BuildConfig;
import com.dovoo.memesnetwork.MainActivity;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.adapter.MemesRecyclerViewAdapter;
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest;
import com.dovoo.memesnetwork.billing.BillingManager;
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener;
import com.dovoo.memesnetwork.components.MyLinearLayoutManager;
import com.dovoo.memesnetwork.utils.AdUtils;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import im.ene.toro.widget.Container;
import im.ene.toro.widget.PressablePlayerSelector;

public class NewestMemesFragment extends Fragment implements BillingManager.BillingUpdatesListener {

    private FrameLayout loadingBar;
    private String section = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Container container;
    private MyLinearLayoutManager layoutManager;
    private MemesRecyclerViewAdapter adapter;
    private PressablePlayerSelector selector;
    private List<DirectLinkItemTest> directLinkItemTestList = new ArrayList<>();
    //private IInAppBillingService mService;
    private BillingManager billingManager;
    private InterstitialAd mInterstitialAd;
    private ServiceConnection mServiceConn;
    private AdView mAdView;
    private FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            section = bundle.getString("section", null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, viewGroup, false);
        billingManager = new BillingManager(this);
        setupBillingService();

        mAdView = view.findViewById(R.id.adView);
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-4908922088432819/1640263467");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Toast.makeText(getContext(), "To remove ads, click button remove ads on your profile", Toast.LENGTH_LONG).show();
            }
        });

        container = view.findViewById(R.id.player_container);
        loadingBar = view.findViewById(R.id.loadingBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fab = view.findViewById(R.id.fab);

        layoutManager = new MyLinearLayoutManager(getContext());

        container.setLayoutManager(layoutManager);
        selector = new PressablePlayerSelector(container);
        container.setPlayerSelector(selector);

        adapter = new MemesRecyclerViewAdapter(getContext(), selector, directLinkItemTestList, ((MainActivity) getActivity()).loadingBar);
        container.setAdapter(adapter);
        container.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchData(totalItemsCount);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setVisibility(View.GONE);
                        fetchData(0);
                    }
                }
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.SmoothScroller smoothScrollerToTop = new LinearSmoothScroller(getContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
                smoothScrollerToTop.setTargetPosition(0);

                layoutManager.startSmoothScroll(smoothScrollerToTop);
            }
        });
        fetchData(0);

        return view;
    }

    private void setupBillingService() {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
               // mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
               // mService = IInAppBillingService.Stub.asInterface(service);
                checkMemberIsPremium();
            }
        };

        Intent i = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        i.setPackage("com.android.vending");

        getContext().bindService(i,
                mServiceConn, Context.BIND_AUTO_CREATE);
    }


    public void checkMemberIsPremium() {
        boolean isPremiumMember = false;
        String packageName = "com.dovoo.memesnetwork";
//        try {
//            Bundle ownedItems = mService.getPurchases(3, packageName, "subs", null);
//            int response = ownedItems.getInt("RESPONSE_CODE");
//            if (response == 0) {
//                ArrayList ownedSkus =
//                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
//                ArrayList purchaseDataList =
//                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
//
//                for (int i = 0; i < purchaseDataList.size(); ++i) {
//                    String sku = (String) ownedSkus.get(i);
//                    if (sku.equals("premium_member")) {
//                        isPremiumMember = true;
//                    }
//                }
//
//                // if continuationToken != null, call getPurchases again
//                // and pass in the token to retrieve more items
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        billingManager.updateMemberStatus(getContext(), isPremiumMember);

    }

    private void fetchData(int offset) {
        loadingBar.setVisibility(View.VISIBLE);
        int min = 1;
        int max = 100;
        int randomNum = new Random().nextInt((max - min) + 1) + min;

        if (randomNum > 30) {
            AdUtils.loadInterstitialAds(getContext(), mInterstitialAd);
        }

        if (offset == 0) {
            directLinkItemTestList.clear();
        }

        Map<String, String> param = new HashMap<>();
        param.put("offset", offset + "");
        param.put("user_id", SharedPreferenceUtils.getPrefs(getContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0) + "");

        if (section != null && !section.equalsIgnoreCase("all")) {
            param.put("post_section", section);
        }

        AndroidNetworking.get(BuildConfig.API_URL + "index")
                .addQueryParameter(param)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject result = response.getJSONObject(i);
                                Integer id = result.getInt("id");
                                String title = result.getString("title");
                                String type = result.getString("type");
                                JSONObject imagesObject = new JSONObject(result.getString("images"));
                                String coverUrl = imagesObject.getJSONObject("image700").getString("url");
                                String category = result.getString("post_section");
                                String videoUrl = null;
                                boolean isVideo = false;
                                boolean hasAudio = false;
                                if (type.equalsIgnoreCase("animated")) {
                                    isVideo = true;
                                    videoUrl = imagesObject.getJSONObject("image460sv").getString("url");
                                    hasAudio = (imagesObject.getJSONObject("image460sv").getInt("hasAudio") == 1 ? true : false);
                                }

                                int width = imagesObject.getJSONObject("image700").getInt("width");
                                int height = imagesObject.getJSONObject("image700").getInt("height");
                                Map<String, Object> data = new HashMap<>();
                                data.put("total_like", result.get("total_like"));
                                data.put("total_dislike", result.get("total_dislike"));
                                data.put("total_comment", result.get("total_comment"));
                                data.put("is_liked", result.get("is_liked"));

                                directLinkItemTestList.add(new DirectLinkItemTest(id, category, title, videoUrl, data, Picasso.get(), coverUrl, width, height, hasAudio, isVideo));
                            }
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setEnabled(true);
                            swipeRefreshLayout.setRefreshing(false);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        System.out.print("a");
                        loadingBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        AdUtils.loadAds(getContext(), mAdView);
    }

    @Override
    public void onDestroyView() {
        layoutManager = null;
        adapter = null;
        selector = null;
        super.onDestroyView();
    }

    @Override
    public void onSubscriptionPurchaseUpdated() {

        AdUtils.loadAds(getContext(), mAdView);
        AdUtils.loadInterstitialAds(getContext(), mInterstitialAd);

    }
}
