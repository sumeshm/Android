package com.planetjup.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is an persistence util, that helps save and retrieve task list
 * Created by Sumesh Mani on 2/16/18.
 */

public class PersistenceManager {

    private static final String TAG = PersistenceManager.class.getSimpleName();

    private static final String PERSISTENCE_KEY = "com_planetjup_widget_CalendarWidget";

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

        Map<String, Integer> retMap = new HashMap<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PERSISTENCE_KEY, null);
        Log.v(TAG, "readSettings(): json=" + json);

        if (json != null && !json.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return retMap;
    }
}
