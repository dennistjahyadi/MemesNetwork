package com.dovoo.memesnetwork.utils

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient

object GlobalFunc {

    @JvmField
    var currentVideoItem: DirectLinkItemTest? = null

    @JvmField
    var isMute = false

    @JvmStatic
    fun isLogin(context: Context): Boolean {

//        return SharedPreferenceUtils.getPrefs(context).getBoolean(
//            SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
//            false
//        )
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }

    fun getLoggedInUserId(context: Context): Int{
        val userId = SharedPreferenceUtils.getPrefs(context)
            .getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, -1)
        return userId
    }
}