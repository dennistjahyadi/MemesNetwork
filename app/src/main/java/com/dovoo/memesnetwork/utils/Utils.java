package com.dovoo.memesnetwork.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {

    //public static final String DOMAIN = "http://192.168.1.8:8000";
    //public static final String DOMAIN = "http://68.183.159.197:8000";
    public static final String DOMAIN = "https://api.memesnetwork.com";

    public static final String API_URL = DOMAIN+"/api/";
    public static final String SOURCE_URL = DOMAIN+"/sources/";


    // test
    // public static final String MOPUB_AD_UNIT_ID = "b195f8dd8ded45fe847ad89ed1d016da";

    // publish
    public static final String MOPUB_AD_UNIT_ID = "90d50eb73f9941bd83629652b0ad6a4d";

    // hollow clientId: 1016402780199-fqinff42oo693meufqji5na1adj53b7v.apps.googleusercontent.com

    public static final String clientId  = "1016402780199-g60jlvimi8qgs9fh0jnl9dvgvbpo1q8m.apps.googleusercontent.com"; // get from console.developer.google.com


    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();

        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }
    public static Map<String, Object> toMap(JSONObject object,String currentDatetime) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();

        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
            map.put("current_datetime", currentDatetime);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
