package com.dovoo.memesnetwork.billing;

import android.content.Context;

import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;

public class BillingManager {

    private BillingUpdatesListener billingUpdatesListener;

    public interface BillingUpdatesListener {
        void onSubscriptionPurchaseUpdated();
    }

    public BillingManager(BillingUpdatesListener billingUpdatesListener) {
        this.billingUpdatesListener = billingUpdatesListener;
    }

    public void updateMemberStatus(Context context, boolean premiumMember) {
        if (context == null) {
            return;
        }
        SharedPreferenceUtils.setPrefs(context, SharedPreferenceUtils.PREFERENCES_PREMIUM_MEMBER, premiumMember);
        billingUpdatesListener.onSubscriptionPurchaseUpdated();
    }
}