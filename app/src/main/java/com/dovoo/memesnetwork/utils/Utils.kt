package com.dovoo.memesnetwork.utils

import android.Manifest
import android.R
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    val sdfDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

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

    fun convertToTimeText(created_at: String, current_datetime: String): String {
        val createdAtDate = sdfDateTime.parse(created_at)
        val currentDate = sdfDateTime.parse(current_datetime)
        val createAtMiliseconds = createdAtDate.time
        val currentTimeMiliseconds = currentDate.time
        val thedate = DateUtils.getRelativeTimeSpanString(
            createAtMiliseconds,
            currentTimeMiliseconds,
            DateUtils.MINUTE_IN_MILLIS
        )
        return thedate.toString()
    }

    @JvmStatic
    fun checkPermissionStorage(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}