package com.dovoo.memesnetwork.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceUtils {
    public static final String SP_NAME = "memesnetwork";
   // public static final String PREFERENCES_USER_DATA = "user_data";
   public static final String PREFERENCES_USER_IS_LOGIN = "user_is_login";
    public static final String PREFERENCES_USER_EMAIL = "user_email";
    public static final String PREFERENCES_USER_NAME = "user_name";
    public static final String PREFERENCES_USER_PHOTO_URL = "user_photo_url";
    public static final String PREFERENCES_USER_LOGIN = "user_login";
    public static final String PREFERENCES_USER_PASSWORD = "user_password";

    public static SharedPreferences getPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SharedPreferenceUtils.SP_NAME, MODE_PRIVATE);
        return prefs;
    }

    public static SharedPreferences.Editor getPrefsEditor(Context context) {
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(SharedPreferenceUtils.SP_NAME, MODE_PRIVATE).edit();
        return prefsEditor;
    }

    public static void setPrefs(Context context, String key, String value) {
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(SharedPreferenceUtils.SP_NAME, MODE_PRIVATE).edit();
        prefsEditor.putString(key,value);
        prefsEditor.apply();
    }
    public static void setPrefs(Context context, String key, Boolean value) {
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(SharedPreferenceUtils.SP_NAME, MODE_PRIVATE).edit();
        prefsEditor.putBoolean(key,value);
        prefsEditor.apply();
    }
    public static void setPrefs(Context context, String key, Integer value) {
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(SharedPreferenceUtils.SP_NAME, MODE_PRIVATE).edit();
        prefsEditor.putInt(key,value);
        prefsEditor.apply();
    }

    public static void removeAllPrefs(Context context){
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(SharedPreferenceUtils.SP_NAME, MODE_PRIVATE).edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }
}
