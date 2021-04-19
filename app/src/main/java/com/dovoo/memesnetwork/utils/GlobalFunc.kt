package com.dovoo.memesnetwork.utils

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient

object GlobalFunc {
    @JvmField
    var mGoogleSignInClient: GoogleSignInClient? = null

    @JvmField
    var currentVideoItem: DirectLinkItemTest? = null

    @JvmField
    var isMute = false

    @JvmStatic
    fun logout(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Do you want to logout?")
            .setPositiveButton("Yes") { dialog, id ->
                SharedPreferenceUtils.removeUserPrefs(activity.applicationContext)
                activity.finish()
            }
            .setNegativeButton("No") { dialog, id -> }
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    @JvmStatic
    fun isLogin(context: Context): Boolean {

//        return SharedPreferenceUtils.getPrefs(context).getBoolean(
//            SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
//            false
//        )
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }
}