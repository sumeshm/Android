package com.planetjup.tasks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an persistence util, that helps save and retrieve task list
 * Created by Sumesh Mani on 2/16/18.
 */

public class PersistenceManager {

    private static final String TAG = PersistenceManager.class.getSimpleName();

    private static final String PREFERENCES_KEY_TASKS = "com_planetjup_tasks_TasksList";
    private static final String PREFERENCES_KEY_REMINDER = "com_planetjup_tasks_ReminderList";

    private static final String JSON_KEY_TASK_NAME = "name";
    private static final String JSON_KEY_TASK_STATE = "checked";
    private static final String JSON_KEY_REMINDER_TYPE = "reminderType";
    private static final String JSON_KEY_REMINDER_DAY = "reminderDay";
    private static final String JSON_KEY_REMINDER_HOUR = "reminderHour";
    private static final String JSON_KEY_REMINDER_MINUTE = "reminderMinute";

    public static void writeTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        Log.v(TAG, "writeTasksList()");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        if (!tasksList.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (TaskDetails taskDetails : tasksList) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(JSON_KEY_TASK_NAME, taskDetails.getTaskName());
                    jsonObject.put(JSON_KEY_TASK_STATE, taskDetails.isCompleted());
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
        Log.v(TAG, "readTasksList()");
        ArrayList<TaskDetails> tasksList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PREFERENCES_KEY_TASKS, null);
        Log.v(TAG, "writeTasksList(): json=" + json);
        if (json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TaskDetails taskDetails = new TaskDetails(jsonObject.getString(JSON_KEY_TASK_NAME), jsonObject.getBoolean(JSON_KEY_TASK_STATE));
                    tasksList.add(taskDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tasksList;
    }

    public static void writeReminderList(Context context, List<ReminderDetails> reminderList) {
        Log.v(TAG, "writeReminderList()");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        if (!reminderList.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (ReminderDetails reminderDetails : reminderList) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(JSON_KEY_REMINDER_TYPE, reminderDetails.getReminderType().getValue());
                    jsonObject.put(JSON_KEY_REMINDER_DAY, reminderDetails.getDay());
                    jsonObject.put(JSON_KEY_REMINDER_HOUR, reminderDetails.getHour());
                    jsonObject.put(JSON_KEY_REMINDER_MINUTE, reminderDetails.getMinute());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            editor.putString(PREFERENCES_KEY_REMINDER, jsonArray.toString());
        } else {
            editor.putString(PREFERENCES_KEY_REMINDER, null);
        }

        editor.apply();
    }

    public static ArrayList<ReminderDetails> readReminderList(Context context) {
        Log.v(TAG, "writeReminderList()");
        ArrayList<ReminderDetails> retList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PREFERENCES_KEY_REMINDER, null);
        Log.v(TAG, "writeReminderList(): json=" + json);
        if (json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int type = jsonObject.getInt(JSON_KEY_REMINDER_TYPE);
                    ReminderDetails.REMINDER_TYPE reminderType = ReminderDetails.REMINDER_TYPE.getType(type);
                    ReminderDetails reminderDetails = new ReminderDetails(
                            reminderType,
                            jsonObject.getInt(JSON_KEY_REMINDER_DAY),
                            jsonObject.getInt(JSON_KEY_REMINDER_HOUR),
                            jsonObject.getInt(JSON_KEY_REMINDER_MINUTE)
                    );
                    retList.add(reminderDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ReminderDetails one = new ReminderDetails(ReminderDetails.REMINDER_TYPE.REMINDER_TYPE_ONE, 15, 11, 0);
            ReminderDetails two = new ReminderDetails(ReminderDetails.REMINDER_TYPE.REMINDER_TYPE_TWO, 20, 11, 0);
            retList.add(one);
            retList.add(two);
        }

        return retList;
    }
}
