package com.dovoo.memesnetwork.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceUtils {
    const val SP_NAME = "memesnetwork"

    // public static final String PREFERENCES_USER_DATA = "user_data";
    const val PREFERENCES_USER_IS_LOGIN = "user_is_login"
    const val PREFERENCES_USER_ID = "user_id"
    const val PREFERENCES_USER_EMAIL = "user_email"
    const val PREFERENCES_USER_NAME = "user_name"
    const val PREFERENCES_USER_PHOTO_URL = "user_photo_url"
    const val PREFERENCES_USER_LOGIN = "user_login"
    const val PREFERENCES_USER_PASSWORD = "user_password"
    const val PREFERENCES_PREMIUM_MEMBER = "premium_member"
    const val PREFERENCES_NOTIF_COUNT = "notif_count"
    const val PREFERENCES_NOTIF_FOLLOWING = "notif_following"
    const val PREFERENCES_NOTIF_MEMES_COMMENT = "notif_memes_comment"
    const val PREFERENCES_NOTIF_COMMENT_REPLY = "notif_comment_reply"
    const val PREFERENCES_NOTIF_MEMES_LIKED = "notif_memes_liked"

    @JvmStatic
    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun getPrefsEditor(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
    }

    @JvmStatic
    fun setPrefs(context: Context, key: String?, value: String?) {
        val prefsEditor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    @JvmStatic
    fun setPrefs(context: Context, key: String?, value: Boolean?) {
        val prefsEditor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
        prefsEditor.putBoolean(key, value!!)
        prefsEditor.apply()
    }

    @JvmStatic
    fun setPrefs(context: Context, key: String?, value: Int?) {
        val prefsEditor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
        prefsEditor.putInt(key, value!!)
        prefsEditor.apply()
    }

    @JvmStatic
    fun removeAllPrefs(context: Context) {
        val prefsEditor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
        prefsEditor.clear()
        prefsEditor.apply()
    }

    @JvmStatic
    fun saveUserPrefs(
        context: Context,
        username: String?,
        userId: Int,
        email: String,
        photoUrl: String?
    ) {
        setPrefs(context, PREFERENCES_USER_NAME, username)
        setPrefs(context, PREFERENCES_USER_ID, userId)
        setPrefs(context, PREFERENCES_USER_EMAIL, email)
        setPrefs(context, PREFERENCES_USER_PHOTO_URL, photoUrl)
        setPrefs(context, PREFERENCES_USER_IS_LOGIN, true)
    }

    @JvmStatic
    fun insertUsernamePrefs(context: Context, username: String?) {
        setPrefs(context, PREFERENCES_USER_NAME, username)
        setPrefs(context, PREFERENCES_USER_IS_LOGIN, true);
    }

    @JvmStatic
    fun removeUserPrefs(context: Context) {
        val prefsEditor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
        prefsEditor.remove(PREFERENCES_USER_IS_LOGIN)
        prefsEditor.remove(PREFERENCES_USER_ID)
        prefsEditor.remove(PREFERENCES_USER_EMAIL)
        prefsEditor.remove(PREFERENCES_USER_NAME)
        prefsEditor.remove(PREFERENCES_USER_PHOTO_URL)
        prefsEditor.remove(PREFERENCES_USER_LOGIN)
        prefsEditor.remove(PREFERENCES_USER_PASSWORD)
        prefsEditor.apply()
    }

    @JvmStatic
    fun isEnableNotifFollowing(context: Context): Boolean{
        return getPrefs(context).getBoolean(PREFERENCES_NOTIF_FOLLOWING, true)
    }

    @JvmStatic
    fun isEnableNotifMemesComment(context: Context): Boolean{
        return getPrefs(context).getBoolean(PREFERENCES_NOTIF_MEMES_COMMENT, true)
    }

    @JvmStatic
    fun isEnableNotifCommentReply(context: Context): Boolean{
        return getPrefs(context).getBoolean(PREFERENCES_NOTIF_COMMENT_REPLY, true)
    }

    @JvmStatic
    fun isEnableNotifMemesLiked(context: Context): Boolean{
        return getPrefs(context).getBoolean(PREFERENCES_NOTIF_MEMES_LIKED, true)
    }

    @JvmStatic
    fun setNotifFollowing(context: Context, value: Boolean){
        setPrefs(context, PREFERENCES_NOTIF_FOLLOWING, value)
    }

    @JvmStatic
    fun setNotifMemeLiked(context: Context, value: Boolean){
        setPrefs(context, PREFERENCES_NOTIF_MEMES_LIKED, value)
    }

    @JvmStatic
    fun setNotifMemesComment(context: Context, value: Boolean){
        setPrefs(context, PREFERENCES_NOTIF_MEMES_COMMENT, value)
    }

    @JvmStatic
    fun setNotifCommentReply(context: Context, value: Boolean){
        setPrefs(context, PREFERENCES_NOTIF_COMMENT_REPLY, value)
    }


}