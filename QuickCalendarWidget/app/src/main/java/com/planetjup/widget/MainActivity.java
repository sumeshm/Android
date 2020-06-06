package com.planetjup.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ON_CALENDAR_PERMISSION_CALLBACK_CODE = 12345;
    private static final int ON_ALARM_CALLBACK_CODE = 12346;
    private static final int ON_ALARM_CALLBACK_CODE2 = 12347;

    String seekText = "0";
    private TextView textViewSeek;
    private SeekBar seekBar;
    private CustomRadioGroup bgColorSettings;
    private CustomRadioGroup dayColorSettings;
    private CustomRadioGroup dateColorSettings;
    private CustomRadioGroup eventColorSettings;
    private CustomRadioGroup todayColorSettings;
    private Map<String, Integer> settingsMap = new HashMap<>();

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

        // setup color pallet
        bgColorSettings = findViewById(R.id.bgColorSettings);
        bgColorSettings.setSelectedColor(settingsMap.get(CalendarWidget.KEY_BG_COLOR));

        dayColorSettings = findViewById(R.id.dayColorSettings);
        dayColorSettings.setSelectedColor(settingsMap.get(CalendarWidget.KEY_DAY_COLOR));

        dateColorSettings = findViewById(R.id.dateColorSettings);
        dateColorSettings.setSelectedColor(settingsMap.get(CalendarWidget.KEY_DATE_COLOR));

        eventColorSettings = findViewById(R.id.eventColorSettings);
        eventColorSettings.setSelectedColor(settingsMap.get(CalendarWidget.KEY_EVENT_COLOR));

        todayColorSettings = findViewById(R.id.todayColorSettings);
        todayColorSettings.setSelectedColor(settingsMap.get(CalendarWidget.KEY_TODAY_COLOR));

        // setup seekbar
        int alpha = settingsMap.get(CalendarWidget.KEY_ALPHA);
        textViewSeek = findViewById(R.id.textViewSeek);
        textViewSeek.setText(Integer.toString(alpha * 10));
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(alpha);
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

        settingsMap.put(CalendarWidget.KEY_ALPHA, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekText = textViewSeek.getText().toString();
        Log.v(TAG, "onStopTrackingTouch: text=" + seekText);

        // notify widget and persist settings
        publishSettings();
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
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 01);
        calendar.set(Calendar.SECOND, 00);

        // designate alarm handler
        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(CalendarWidget.ACTION_UI_REFRESH);
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
        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(CalendarWidget.ACTION_UI_REFRESH_HOURLY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), ON_ALARM_CALLBACK_CODE2, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void publishSettings() {
        Log.v(TAG, "publishSettings():");

        settingsMap.put(CalendarWidget.KEY_BG_COLOR, bgColorSettings.getSelectedColor());
        settingsMap.put(CalendarWidget.KEY_DAY_COLOR, dayColorSettings.getSelectedColor());
        settingsMap.put(CalendarWidget.KEY_DATE_COLOR, dateColorSettings.getSelectedColor());
        settingsMap.put(CalendarWidget.KEY_EVENT_COLOR, eventColorSettings.getSelectedColor());
        settingsMap.put(CalendarWidget.KEY_TODAY_COLOR, todayColorSettings.getSelectedColor());
        PersistenceManager.writeSettings(getApplicationContext(), settingsMap);

        // todo: notify widget with settings data

        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(CalendarWidget.ACTION_SETTINGS_REFRESH);
        intent.putExtra(CalendarWidget.KEY_ALPHA, Integer.valueOf(seekText));
        intent.putExtra(CalendarWidget.KEY_BG_COLOR, bgColorSettings.getSelectedColor());
        intent.putExtra(CalendarWidget.KEY_DAY_COLOR, dayColorSettings.getSelectedColor());
        intent.putExtra(CalendarWidget.KEY_DATE_COLOR, dateColorSettings.getSelectedColor());
        intent.putExtra(CalendarWidget.KEY_EVENT_COLOR, eventColorSettings.getSelectedColor());
        intent.putExtra(CalendarWidget.KEY_TODAY_COLOR, todayColorSettings.getSelectedColor());

        sendBroadcast(intent);
    }
}
