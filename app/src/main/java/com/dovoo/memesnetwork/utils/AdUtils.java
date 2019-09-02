package com.dovoo.memesnetwork.utils;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdUtils {

    public static void loadAds(Context context, AdView adview){
        boolean isPremiumMember = SharedPreferenceUtils.getPrefs(context).getBoolean(SharedPreferenceUtils.PREFERENCES_PREMIUM_MEMBER, false);
        if (!isPremiumMember) {
            adview.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        }else{
            adview.destroy();
            adview.setVisibility(View.GONE);
        }

    }

    public static void loadInterstitialAds(Context context, InterstitialAd interstitialAd){
        boolean isPremiumMember = SharedPreferenceUtils.getPrefs(context).getBoolean(SharedPreferenceUtils.PREFERENCES_PREMIUM_MEMBER, false);
        if (!isPremiumMember) {
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }
}
