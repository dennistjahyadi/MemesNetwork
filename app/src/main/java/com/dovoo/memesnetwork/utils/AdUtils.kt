package com.dovoo.memesnetwork.utils

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

object AdUtils {
    @JvmStatic
    fun loadAds(context: Context, adview: AdView) {
        val isPremiumMember = SharedPreferenceUtils.getPrefs(context)
            .getBoolean(SharedPreferenceUtils.PREFERENCES_PREMIUM_MEMBER, false)
        if (!isPremiumMember) {
            adview.visibility = View.VISIBLE
            val adRequest = AdRequest.Builder().build()
            adview.loadAd(adRequest)
        } else {
            adview.destroy()
            adview.visibility = View.GONE
        }
    }

    @JvmStatic
    fun loadInterstitialAds(context: Context, interstitialAd: InterstitialAd) {
        val isPremiumMember = SharedPreferenceUtils.getPrefs(context)
            .getBoolean(SharedPreferenceUtils.PREFERENCES_PREMIUM_MEMBER, false)
        if (!isPremiumMember) {
            interstitialAd.loadAd(AdRequest.Builder().build())
        }
    }
}