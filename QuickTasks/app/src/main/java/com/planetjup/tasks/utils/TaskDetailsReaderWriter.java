package com.planetjup.tasks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * This class is an persistence util, that helps save and retrieve task list
 * Created by Sumesh Mani on 2/16/18.
 */

public class TaskDetailsReaderWriter {

    private static final String PREFERENCES_KEY_TASKS = "com_planetjup_tasks_TasksList";
    private static final String PREFERENCES_KEY_REFRESH_DATE = "com_planetjup_tasks_Refresh_Date";

    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_STATE = "checked";

    public static void writeTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        if (!tasksList.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (TaskDetails taskDetails : tasksList) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(JSON_KEY_NAME, taskDetails.getTaskName());
                    jsonObject.put(JSON_KEY_STATE, taskDetails.isCompleted());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            editor.putString(PREFERENCES_KEY_TASKS, jsonArray.toString());
        } else {
            editor.putString(PREFERENCES_KEY_TASKS, null);
        }

        editor.apply();
    }

    public static ArrayList<TaskDetails> readTasksList(Context context) {
        ArrayList<TaskDetails> tasksList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PREFERENCES_KEY_TASKS, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TaskDetails taskDetails = new TaskDetails(jsonObject.getString(JSON_KEY_NAME), jsonObject.getBoolean(JSON_KEY_STATE));
                    tasksList.add(taskDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tasksList;
    }

    public static void writeTasksRefreshDate(Context context, int month) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(PREFERENCES_KEY_REFRESH_DATE, month);
        editor.apply();
    }

    public static int readTasksRefreshDate(Context context) {
        Date retVal = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(PREFERENCES_KEY_REFRESH_DATE, -1);
    }

}
