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

    private static final String KEY_REMINDER_MONTHLY = "com_planetjup_tasks_ReminderListMonthly";
    private static final String KEY_REMINDER_YEARLY = "com_planetjup_tasks_ReminderListYearly";
    private static final String KEY_REMINDER_OTHER = "com_planetjup_tasks_ReminderListOther";

    private static final String JSON_KEY_TASK_NAME = "name";
    private static final String JSON_KEY_TASK_STATE = "checked";

    private static final String BKP_FILE_NAME = "quickTask.txt";


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

    public static ArrayList<TaskDetails> readMonthlyTasksList(Context context) {
        return readTasksList(context, KEY_REMINDER_MONTHLY);
    }

    public static ArrayList<TaskDetails> readYearlyTasksList(Context context) {
        return readTasksList(context, KEY_REMINDER_YEARLY);
    }

    public static ArrayList<TaskDetails> readOtherTasksList(Context context) {
        return readTasksList(context, KEY_REMINDER_OTHER);
    }

    public static void writeMonthlyTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        writeTasksList(context, tasksList, KEY_REMINDER_MONTHLY);
    }

    public static void writeYearlyTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        writeTasksList(context, tasksList, KEY_REMINDER_YEARLY);
    }

    public static void writeOtherTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        writeTasksList(context, tasksList, KEY_REMINDER_OTHER);
    }

    private static ArrayList<TaskDetails> readTasksList(Context context, String key) {
        Log.v(TAG, "readTasksList()");
        ArrayList<TaskDetails> tasksList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(key, null);
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

    private static void writeTasksList(Context context, ArrayList<TaskDetails> tasksList, String key) {
        Log.v(TAG, "writeTasksList()");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, null);
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

            editor.putString(key, jsonArray.toString());
        } else {
            editor.putString(key, null);
        }

        editor.apply();
    }
}
