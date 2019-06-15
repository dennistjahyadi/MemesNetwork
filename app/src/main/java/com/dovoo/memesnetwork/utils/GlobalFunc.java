package com.dovoo.memesnetwork.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.dovoo.memesnetwork.testing.DirectLinkItemTest;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class GlobalFunc {

    public static GoogleSignInClient mGoogleSignInClient;
    public static DirectLinkItemTest currentVideoItem;
    public static boolean isMute = false;

    public static void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferenceUtils.removeUserPrefs(activity.getApplicationContext());
                        activity.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}
