package com.planetjup.tasks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

/**
 * This class is an persistence util, that helps save and retrieve task list
 * Created by Sumesh Mani on 2/16/18.
 */

public class PersistenceManager {

    private static final String TAG = PersistenceManager.class.getSimpleName();

    private static final String PREFERENCES_KEY_TASKS_MONTHLY = "com_planetjup_tasks_Monthly";
    private static final String PREFERENCES_KEY_TASKS_DAILY = "com_planetjup_tasks_Daily";
    private static final String PREFERENCES_KEY_REMINDER = "com_planetjup_tasks_ReminderList";

    private static final String JSON_KEY_TASK_NAME = "name";
    private static final String JSON_KEY_TASK_STATE = "checked";
    private static final String JSON_KEY_REMINDER_TYPE = "reminderType";
    private static final String JSON_KEY_REMINDER_DAY = "reminderDay";
    private static final String JSON_KEY_REMINDER_HOUR = "reminderHour";
    private static final String JSON_KEY_REMINDER_MINUTE = "reminderMinute";

    private static final String BKP_FILE_NAME = "quickTask.txt";

    public static void writeMonthlyTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        Log.v(TAG, "writeMonthlyTasksList()");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PREFERENCES_KEY_TASKS_MONTHLY, null);
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

            editor.putString(PREFERENCES_KEY_TASKS_MONTHLY, jsonArray.toString());
        } else {
            editor.putString(PREFERENCES_KEY_TASKS_MONTHLY, null);
        }

        editor.apply();
    }

    public static ArrayList<TaskDetails> readMonthlyTasksList(Context context) {
        Log.v(TAG, "readMonthlyTasksList()");
        ArrayList<TaskDetails> tasksList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PREFERENCES_KEY_TASKS_MONTHLY, null);
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

    public static void writeDailyTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        Log.v(TAG, "writeDailyTasksList()");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PREFERENCES_KEY_TASKS_DAILY, null);
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

            editor.putString(PREFERENCES_KEY_TASKS_DAILY, jsonArray.toString());
        } else {
            editor.putString(PREFERENCES_KEY_TASKS_DAILY, null);
        }

        editor.apply();
    }

    public static ArrayList<TaskDetails> readDailyTasksList(Context context) {
        Log.v(TAG, "readDailyTasksList()");
        ArrayList<TaskDetails> tasksList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PREFERENCES_KEY_TASKS_DAILY, null);
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

    public static void writeReminderList(Context context, ArrayList<ReminderDetails> reminderList) {
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
        ArrayList<ReminderDetails> retList = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PREFERENCES_KEY_REMINDER, null);
        Log.v(TAG, "writeReminderList(): json=" + json);
        if (json != null && !json.isEmpty()) {
            try {
                retList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int type = jsonObject.getInt(JSON_KEY_REMINDER_TYPE);
                    ReminderDetails.REMINDER_TYPE reminderType = ReminderDetails.REMINDER_TYPE.getEnum(type);

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
        }

        if (retList == null || retList.isEmpty()) {
            retList = writeDefaultReminderList(context);
        }

        return retList;
    }

    public static ArrayList<TaskDetails> importPreference(Context context) {
        Log.v(TAG, "importPreference()");
        ArrayList<TaskDetails> tasksList = new ArrayList<>();

        try {
            FileInputStream inputStream = context.openFileInput(BKP_FILE_NAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String json = stringBuilder.toString();
            Log.v(TAG, "importPreference(): DONE: json=" + json);

            if (!json.isEmpty()) {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TaskDetails taskDetails = new TaskDetails(jsonObject.getString(JSON_KEY_TASK_NAME), jsonObject.getBoolean(JSON_KEY_TASK_STATE));
                    tasksList.add(taskDetails);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tasksList;
    }

    public static void exportPreference(Context context, ArrayList<TaskDetails> tasksList) {
        Log.v(TAG, "exportPreference()");

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

            Log.v(TAG, "exportPreference(): json=" + jsonArray.toString());

            try {
                byte[] jsonBytes = jsonArray.toString().getBytes();

                FileOutputStream outputStream = context.openFileOutput(BKP_FILE_NAME, Context.MODE_PRIVATE);
                outputStream.write(jsonBytes);
                outputStream.close();

                Log.v(TAG, "exportPreference(): DONE");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<ReminderDetails> writeDefaultReminderList(Context context) {
        ReminderDetails one = new ReminderDetails(
                ReminderDetails.REMINDER_TYPE.REMINDER_TYPE_ONE,
                15,
                11,
                0);
        ReminderDetails two = new ReminderDetails(
                ReminderDetails.REMINDER_TYPE.REMINDER_TYPE_TWO,
                20,
                11,
                0);

        ArrayList<ReminderDetails> retList = new ArrayList<>();
        retList.add(one);
        retList.add(two);
        writeReminderList(context, retList);

        return retList;
    }
}
