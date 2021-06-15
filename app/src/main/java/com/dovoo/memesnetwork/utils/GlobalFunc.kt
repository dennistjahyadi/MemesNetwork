package com.dovoo.memesnetwork.utils

import android.content.Context
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.google.android.gms.auth.api.signin.GoogleSignIn

object GlobalFunc {

    @JvmField
    var currentVideoItem: DirectLinkItemTest? = null

    @JvmField
    var isMute = false

    @JvmStatic
    fun isLogin(context: Context): Boolean {
        val localLogin = SharedPreferenceUtils.getPrefs(context).getBoolean(
            SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
            false
        )

        return (GoogleSignIn.getLastSignedInAccount(context) != null) && localLogin
    }

    @JvmStatic
    fun successLogin(context: Context) {
        SharedPreferenceUtils.getPrefsEditor(context).putBoolean(
            SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
            true
        )
    }

    @JvmStatic
    fun setLogout(context: Context) {
        SharedPreferenceUtils.getPrefsEditor(context).putBoolean(
            SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
            false
        )
    }

    fun getLoggedInUserId(context: Context): Int {
        val userId = SharedPreferenceUtils.getPrefs(context)
            .getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, -1)
        return userId
    }

    @JvmStatic
    fun getNotifCount(
        context: Context
    ): Int {
        return SharedPreferenceUtils.getPrefs(context)
            .getInt(SharedPreferenceUtils.PREFERENCES_NOTIF_COUNT, 0)
    }

    @JvmStatic
    fun addNotifCount(
        context: Context
    ) {
        val count = SharedPreferenceUtils.getPrefs(context)
            .getInt(SharedPreferenceUtils.PREFERENCES_NOTIF_COUNT, 0)
        SharedPreferenceUtils.setPrefs(
            context,
            SharedPreferenceUtils.PREFERENCES_NOTIF_COUNT,
            count + 1
        )
    }

    @JvmStatic
    fun minNotifCount(
        context: Context
    ) {
        val count = SharedPreferenceUtils.getPrefs(context)
            .getInt(SharedPreferenceUtils.PREFERENCES_NOTIF_COUNT, 0)
        SharedPreferenceUtils.setPrefs(
            context,
            SharedPreferenceUtils.PREFERENCES_NOTIF_COUNT,
            count - 1
        )
    }

    @JvmStatic
    fun clearNotifCount(
        context: Context
    ) {
        SharedPreferenceUtils.setPrefs(context, SharedPreferenceUtils.PREFERENCES_NOTIF_COUNT, 0)
    }
}