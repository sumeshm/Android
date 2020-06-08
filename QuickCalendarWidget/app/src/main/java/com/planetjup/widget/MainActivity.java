package com.planetjup.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.planetjup.widget.util.Constants;
import com.planetjup.widget.util.PersistenceManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, CustomRadioGroup.OnButtonClickedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ON_CALENDAR_PERMISSION_CALLBACK_CODE = 12345;
    private static final int ON_ALARM_CALLBACK_CODE = 12346;
    private static final int ON_ALARM_CALLBACK_CODE2 = 12347;


    private Map<String, Integer> settingsMap = new HashMap<>();
    private final Map<String, Integer> colorsMap = new HashMap<>();

    // seek bar members
    private TextView textViewSeek;
    private SeekBar seekBar;

    private LinearLayout radioGroupPlaceHolder;

    // preview box members
    private final GradientDrawable shape = new GradientDrawable();
    private TextView previewTextDay;
    private TextView previewTextDate;
    private TextView previewTextEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getContactsPermission();
        startDailyAlarm();
        startHourlyAlarm();

        setContentView(R.layout.activity_main);

        // fetch settings from Storage
        settingsMap = PersistenceManager.readSettings(getApplicationContext());
        Log.v(TAG, "onCreate: settingsMap=" + settingsMap.toString());

        // setup seekbar and save button
        int alpha = settingsMap.get(Constants.KEY_ALPHA);
        textViewSeek = findViewById(R.id.textViewSeek);
        textViewSeek.setText(Integer.toString(alpha * 10));
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(alpha);

        Button buttonSave = findViewById(R.id.buttonSubmit);
        buttonSave.setOnClickListener(this);

        // setup preview box
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(20f);
        shape.setColor(settingsMap.get(Constants.KEY_BG_COLOR));
        shape.setStroke(1, Color.BLACK);

        View previewBox = findViewById(R.id.preview);
        previewBox.setClickable(false);
        previewBox.setBackground(shape);
        previewTextDay = findViewById(R.id.previewTextDay);
        previewTextDate = findViewById(R.id.previewTextDate);
        previewTextEvent = findViewById(R.id.previewTextEvent);
        updatePreviewBox(Constants.KEY_ALPHA, 0);
        updatePreviewBox(Constants.KEY_BG_COLOR, settingsMap.get(Constants.KEY_BG_COLOR));
        updatePreviewBox(Constants.KEY_DAY_COLOR, settingsMap.get(Constants.KEY_DAY_COLOR));
        updatePreviewBox(Constants.KEY_DATE_COLOR, settingsMap.get(Constants.KEY_DATE_COLOR));
        updatePreviewBox(Constants.KEY_EVENT_COLOR, settingsMap.get(Constants.KEY_EVENT_COLOR));


        // setup radio-groups
        int[] colorsList = getResources().getIntArray(R.array.colorList);
        String[] colorsNameList = getResources().getStringArray(R.array.colorsNameList);
        for (int i = 0; i < colorsList.length; i++) {
            colorsMap.put(colorsNameList[i], colorsList[i]);
        }
        Log.v(TAG, "onCreate: colorsMap=" + colorsMap.toString());

        radioGroupPlaceHolder = findViewById(R.id.radioGroupPlaceHolder);
        createRadioGroup(getString(R.string.advice_backgroundColor), Constants.KEY_BG_COLOR, colorsMap);
        createRadioGroup(getString(R.string.advice_dayColor), Constants.KEY_DAY_COLOR, colorsMap);
        createRadioGroup(getString(R.string.advice_dateColor), Constants.KEY_DATE_COLOR, colorsMap);
        createRadioGroup(getString(R.string.advice_eventColor), Constants.KEY_EVENT_COLOR, colorsMap);
        createRadioGroup(getString(R.string.advice_todayColor), Constants.KEY_TODAY_COLOR, colorsMap);

    }

    private void createRadioGroup(String titleText, String listenerId, Map<String, Integer> colorsMap) {
        int selectedColor = settingsMap.get(listenerId);

        CustomRadioGroup radioGroup = new CustomRadioGroup(getApplicationContext(), titleText, colorsMap);
        radioGroup.setUp(titleText, listenerId, this, selectedColor);

        radioGroupPlaceHolder.addView(radioGroup);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult: requestCode=" + requestCode);

        if (requestCode == ON_CALENDAR_PERMISSION_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_CONTACTS_PERMISSION_CALLBACK_CODE");
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String data = Integer.toString(progress * 10);
        textViewSeek.setText(data);

        settingsMap.put(Constants.KEY_ALPHA, progress);

        // update preview
        updatePreviewBox(Constants.KEY_ALPHA, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        String seekText = textViewSeek.getText().toString();
        Log.v(TAG, "onStopTrackingTouch: text=" + seekText);
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed():");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save Settings?")
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // save settings, notify widget and exit
                        publishSettings();
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // exit without saving settings
                        MainActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick():");

        // notify widget and persist settings
        publishSettings();

        // exit acitivity
        finish();
    }

    @Override
    public void radioButtonClicked(String listenerId, int color) {
        Log.v(TAG, "radioButtonClicked(): listenerId=" + listenerId + ", color=" + color);

        // update preview box
        updatePreviewBox(listenerId, color);
    }

    private void getContactsPermission() {
        Log.v(TAG, "getContactsPermission()");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "getContactsPermission : READ_CALENDAR=FALSE");

            String[] permissionList = {Manifest.permission.READ_CALENDAR};
            requestPermissions(permissionList, ON_CALENDAR_PERMISSION_CALLBACK_CODE);

        } else {
            Log.v(TAG, "getContactsPermission : READ_CALENDAR=TRUE");
        }
    }

    // set alarm for 12:00 am
    private void startDailyAlarm() {
        Log.v(TAG, "startAlarm()");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);

        // designate alarm handler
        Intent intent = new Intent(this, Constants.class);
        intent.setAction(Constants.ACTION_UI_REFRESH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), ON_ALARM_CALLBACK_CODE, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // set alarm for every one hour
    private void startHourlyAlarm() {
        Log.v(TAG, "startHourlyAlarm()");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // designate alarm handler
        Intent intent = new Intent(this, Constants.class);
        intent.setAction(Constants.ACTION_UI_REFRESH_HOURLY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), ON_ALARM_CALLBACK_CODE2, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    private void publishSettings() {
        Log.v(TAG, "publishSettings():");

        PersistenceManager.writeSettings(getApplicationContext(), settingsMap);

        // notify widget about change in settings
        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(Constants.ACTION_SETTINGS_REFRESH);
        sendBroadcast(intent);
    }

    private void updatePreviewBox(String listenerId, @ColorInt int color) {
        // update preview box
        switch (listenerId) {
            case Constants.KEY_ALPHA:
                int effetiveAlpha = 0;
                int progress = seekBar.getProgress();
                if (progress != 0) {
                    effetiveAlpha = (255 * progress) / 10;
                }

                shape.setAlpha(effetiveAlpha);
                break;
            case Constants.KEY_BG_COLOR:
                shape.setColor(color);
                settingsMap.put(Constants.KEY_BG_COLOR, color);
                break;
            case Constants.KEY_DAY_COLOR:
                previewTextDay.setTextColor(color);
                settingsMap.put(Constants.KEY_DAY_COLOR, color);
                break;
            case Constants.KEY_DATE_COLOR:
                previewTextDate.setTextColor(color);
                settingsMap.put(Constants.KEY_DATE_COLOR, color);
                break;
            case Constants.KEY_EVENT_COLOR:
                previewTextEvent.setTextColor(color);
                settingsMap.put(Constants.KEY_EVENT_COLOR, color);
                break;
            case Constants.KEY_TODAY_COLOR:
                settingsMap.put(Constants.KEY_TODAY_COLOR, color);
                break;
        }
    }
}
