package com.planetjup.widget.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is an persistence util, that helps save and retrieve task list
 * Created by Sumesh Mani on 2/16/18.
 */

public class PersistenceManager {

    private static final String TAG = PersistenceManager.class.getSimpleName();

    private static final String PERSISTENCE_KEY = "com_planetjup_widget_CalendarWidget";

    private static Gson gson = new Gson();

    public static void writeSettings(Context context, Map<String, Integer> settingsMap) {
        Log.v(TAG, "writeSettings()");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        JSONObject jsonObject = new JSONObject(settingsMap);
        Log.v(TAG, "writeSettings(): jsonObject=" + jsonObject);

        editor.putString(PERSISTENCE_KEY, jsonObject.toString());
        editor.apply();
    }

    public static Map<String, Integer> readSettings(Context context) {
        Log.v(TAG, "readSettings()");

        Map<String, Integer> retMap = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PERSISTENCE_KEY, null);
        Log.v(TAG, "readSettings(): json=" + json);

        if (json != null && !json.isEmpty()) {
            try {
                Type type = new TypeToken<Map<String, Integer>>() {
                }.getType();
                JSONObject jsonObject = new JSONObject(json);
                retMap = gson.fromJson(jsonObject.toString(), type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            retMap = new HashMap<>();
        }

        // create default settings if needed
        retMap.putIfAbsent(Constants.KEY_ALPHA, 2);
        retMap.putIfAbsent(Constants.KEY_BG_COLOR, Color.DKGRAY);
        retMap.putIfAbsent(Constants.KEY_DAY_COLOR, Color.BLACK);
        retMap.putIfAbsent(Constants.KEY_DATE_COLOR, Color.BLACK);
        retMap.putIfAbsent(Constants.KEY_EVENT_COLOR, Color.YELLOW);
        retMap.putIfAbsent(Constants.KEY_TODAY_COLOR, Color.BLUE);

        return retMap;
    }
}
