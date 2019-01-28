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

    private static final String BKP_FILE_NAME_MONTHLY = "quickTask_Monthly.txt";
    private static final String BKP_FILE_NAME_DAILY = "quickTask_daily.txt";


    public static void writeMonthlyTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        Log.v(TAG, "writeMonthlyTasksList()");
        writeTasksList(context, tasksList, PREFERENCES_KEY_TASKS_MONTHLY);
    }

    public static ArrayList<TaskDetails> readMonthlyTasksList(Context context) {
        Log.v(TAG, "readMonthlyTasksList()");
        return readTasksList(context, PREFERENCES_KEY_TASKS_MONTHLY);
    }

    public static void writeDailyTasksList(Context context, ArrayList<TaskDetails> tasksList) {
        Log.v(TAG, "writeDailyTasksList()");
        writeTasksList(context, tasksList, PREFERENCES_KEY_TASKS_DAILY);
    }

    public static ArrayList<TaskDetails> readDailyTasksList(Context context) {
        Log.v(TAG, "readDailyTasksList()");
        return readTasksList(context, PREFERENCES_KEY_TASKS_DAILY);
    }

    private static void writeTasksList(Context context, ArrayList<TaskDetails> tasksList, String key) {
        Log.v(TAG, "writeTasksList() : key=" + key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray jsonArray = convertToJsonArray(tasksList);
        editor.putString(key, jsonArray.toString());
        editor.commit();
    }

    private static ArrayList<TaskDetails> readTasksList(Context context, String key) {
        Log.v(TAG, "readTasksList(): key=" + key);
        ArrayList<TaskDetails> tasksList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String jsonData = prefs.getString(key, null);
        return convertToArray(jsonData);
    }

    private static ArrayList<TaskDetails> convertToArray(String jsonData)
    {
        Log.v(TAG, "convertToArray(): jsonData=" + jsonData);
        ArrayList<TaskDetails> tasksList = new ArrayList<>();

        if (jsonData != null && !jsonData.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TaskDetails taskDetails = new TaskDetails(jsonObject.getString(JSON_KEY_TASK_NAME), jsonObject.getBoolean(JSON_KEY_TASK_STATE));
                    tasksList.add(taskDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.v(TAG, "convertToArray(): tasksList=" + tasksList.toString());
        return tasksList;
    }

    private static JSONArray convertToJsonArray(ArrayList<TaskDetails> tasksList)
    {
        Log.v(TAG, "convertToJsonArray() : tasksList=" + tasksList);

        JSONArray jsonArray = new JSONArray();
        if (!tasksList.isEmpty()) {
            Log.v(TAG, "writeTasksList() : list=" + tasksList.toString());

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
        }

        Log.v(TAG, "convertToJsonArray() : return_json_array.lenght=" + jsonArray.length());
        return jsonArray;
    }

    /*
    * Permanent backup/restore to phone memory
    */

    public static Boolean exportTaskLists(Context context, ArrayList<TaskDetails> monthlyTasksList, ArrayList<TaskDetails> dailyTasksList) {
        Log.v(TAG, "exportTaskLists()");
        Boolean retVal = false;

        JSONArray monthJsonArray = convertToJsonArray(monthlyTasksList);
        Log.v(TAG, "exportTaskLists(): monthJsonArray=" + monthJsonArray.toString());

        try {
            byte[] jsonBytes = monthJsonArray.toString().getBytes();

            FileOutputStream outputStream = context.openFileOutput(BKP_FILE_NAME_MONTHLY, Context.MODE_PRIVATE);
            outputStream.write(jsonBytes);
            outputStream.close();

            retVal = true;
            Log.v(TAG, "exportTaskLists(): DONE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray dailyJsonArray = convertToJsonArray(dailyTasksList);
        Log.v(TAG, "exportTaskLists(): dailyJsonArray=" + dailyJsonArray.toString());

        try {
            byte[] jsonBytes = dailyJsonArray.toString().getBytes();

            FileOutputStream outputStream = context.openFileOutput(BKP_FILE_NAME_DAILY, Context.MODE_PRIVATE);
            outputStream.write(jsonBytes);
            outputStream.close();

            retVal = true;
            Log.v(TAG, "exportTaskLists(): DONE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return retVal;
    }

    public static ArrayList<TaskDetails> importMonthlyTaskLists(Context context) {
        Log.v(TAG, "importMonthlyTaskLists()");
        ArrayList<TaskDetails> tasksList = null;

        try {
            FileInputStream inputStream = context.openFileInput(BKP_FILE_NAME_MONTHLY);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String jsonData = stringBuilder.toString();
            tasksList = convertToArray(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == tasksList) {
            tasksList = new ArrayList<>();
        }

        Log.v(TAG, "importMonthlyTaskLists(): tasksList=" + tasksList.toString());
        return tasksList;
    }


    public static ArrayList<TaskDetails> importDailyTaskLists(Context context) {
        Log.v(TAG, "importDailyTaskLists()");
        ArrayList<TaskDetails> tasksList = null;

        try {
            FileInputStream inputStream = context.openFileInput(BKP_FILE_NAME_DAILY);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String jsonData = stringBuilder.toString();
            tasksList = convertToArray(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == tasksList) {
            tasksList = new ArrayList<>();
        }

        Log.v(TAG, "importDailyTaskLists(): tasksList=" + tasksList.toString());
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
