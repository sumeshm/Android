package planetjup.com.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by summani on 2/16/18.
 */

public class PersistenceHelper {

    private static final String PREFERENCES_KEY = "planetjup.com.util.TaskDetails";
    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_STATE = "checked";

    public static void setStringArrayPref(Context context, ArrayList<TaskDetails> values) {

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            TaskDetails taskDetails = values.get(i);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(JSON_KEY_NAME, taskDetails.getTaskName());
                jsonObject.put(JSON_KEY_STATE, taskDetails.isCompleted());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (!values.isEmpty()) {
            editor.putString(PREFERENCES_KEY, jsonArray.toString());
        } else {
            editor.putString(PREFERENCES_KEY, null);
        }

        editor.apply();
    }

    public static ArrayList<TaskDetails> getStringArrayPref(Context context) {
        ArrayList<TaskDetails> tasksList = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(PREFERENCES_KEY, null);
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
}
