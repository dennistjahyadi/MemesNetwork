package com.dovoo.memesnetwork.utils

import android.R
import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.util.ArrayList
import java.util.HashMap

object Utils {
    //public static final String DOMAIN = "http://192.168.1.8:8000";
    //public static final String DOMAIN = "http://68.183.159.197:8000";
    const val DOMAIN = "https://api.memesnetwork.com"
    const val API_URL = DOMAIN + "/api/"
    const val SOURCE_URL = DOMAIN + "/sources/"

    // test
    // public static final String MOPUB_AD_UNIT_ID = "b195f8dd8ded45fe847ad89ed1d016da";
    // publish
    const val MOPUB_AD_UNIT_ID = "90d50eb73f9941bd83629652b0ad6a4d"

    // hollow clientId: 1016402780199-fqinff42oo693meufqji5na1adj53b7v.apps.googleusercontent.com
    const val clientId =
        "1016402780199-g60jlvimi8qgs9fh0jnl9dvgvbpo1q8m.apps.googleusercontent.com" // get from console.developer.google.com

    @Throws(JSONException::class)
    @JvmStatic
    fun toMap(`object`: JSONObject): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        val keysItr = `object`.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            var value = `object`[key]
            if (value is JSONArray) {
                value = toList(value)
            } else if (value is JSONObject) {
                value = toMap(value)
            }
            map[key] = value
        }
        return map
    }

    @JvmStatic
    @Throws(JSONException::class)
    fun toMap(`object`: JSONObject, currentDatetime: String): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        val keysItr = `object`.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            var value = `object`[key]
            if (value is JSONArray) {
                value = toList(value)
            } else if (value is JSONObject) {
                value = toMap(value)
            }
            map[key] = value
            map["current_datetime"] = currentDatetime
        }
        return map
    }

    @Throws(JSONException::class)
    fun toList(array: JSONArray): List<Any> {
        val list: MutableList<Any> = ArrayList()
        for (i in 0 until array.length()) {
            var value = array[i]
            if (value is JSONArray) {
                value = toList(value)
            } else if (value is JSONObject) {
                value = toMap(value)
            }
            list.add(value)
        }
        return list
    }

    @JvmStatic
    fun hideKeyboard(activity: Activity) {
        val view = activity.findViewById<View>(R.id.content)
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}