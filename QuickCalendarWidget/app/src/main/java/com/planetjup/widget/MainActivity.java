package com.planetjup.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.planetjup.widget.util.Constants;
import com.planetjup.widget.util.PersistenceManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ON_CALENDAR_PERMISSION_CALLBACK_CODE = 12345;
    private static final int ON_ALARM_CALLBACK_CODE = 12346;
    private static final int ON_ALARM_CALLBACK_CODE2 = 12347;

    private String seekText = "0";
    private TextView textViewSeek;
    private SeekBar seekBar;
    private CustomRadioGroup bgColorSettings;
    private CustomRadioGroup dayColorSettings;
    private CustomRadioGroup dateColorSettings;
    private CustomRadioGroup eventColorSettings;
    private CustomRadioGroup todayColorSettings;
    private Button buttonSubmit;
    private View previewBox;
    private Map<String, Integer> settingsMap = new HashMap<>();
    private GradientDrawable shape = new GradientDrawable();

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
        bgColorSettings.setSelectedColor(settingsMap.get(Constants.KEY_BG_COLOR));

        dayColorSettings = findViewById(R.id.dayColorSettings);
        dayColorSettings.setSelectedColor(settingsMap.get(Constants.KEY_DAY_COLOR));

        dateColorSettings = findViewById(R.id.dateColorSettings);
        dateColorSettings.setSelectedColor(settingsMap.get(Constants.KEY_DATE_COLOR));

        eventColorSettings = findViewById(R.id.eventColorSettings);
        eventColorSettings.setSelectedColor(settingsMap.get(Constants.KEY_EVENT_COLOR));

        todayColorSettings = findViewById(R.id.todayColorSettings);
        todayColorSettings.setSelectedColor(settingsMap.get(Constants.KEY_TODAY_COLOR));

        // setup seekbar
        int alpha = settingsMap.get(Constants.KEY_ALPHA);
        textViewSeek = findViewById(R.id.textViewSeek);
        textViewSeek.setText(Integer.toString(alpha * 10));
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(alpha);

        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);

        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(15f);
        shape.setColor(bgColorSettings.getSelectedColor());
        shape.setStroke(1, Color.BLACK);

        previewBox = findViewById(R.id.preview);
        previewBox.setClickable(false);
        previewBox.setBackground(shape);
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

        // todo: temp code
        int effetiveAlpha = 0;
        if (progress != 0) {
            effetiveAlpha = (255 * progress) / 10;
        }

        // update preview
        shape.setAlpha(effetiveAlpha);
        shape.setColor(bgColorSettings.getSelectedColor());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekText = textViewSeek.getText().toString();
        Log.v(TAG, "onStopTrackingTouch: text=" + seekText);
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed():");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save Settings?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // save settings, notify widget and exit
                        publishSettings();
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

        settingsMap.put(Constants.KEY_BG_COLOR, bgColorSettings.getSelectedColor());
        settingsMap.put(Constants.KEY_DAY_COLOR, dayColorSettings.getSelectedColor());
        settingsMap.put(Constants.KEY_DATE_COLOR, dateColorSettings.getSelectedColor());
        settingsMap.put(Constants.KEY_EVENT_COLOR, eventColorSettings.getSelectedColor());
        settingsMap.put(Constants.KEY_TODAY_COLOR, todayColorSettings.getSelectedColor());
        PersistenceManager.writeSettings(getApplicationContext(), settingsMap);

        // notify widget about change in settings
        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(Constants.ACTION_SETTINGS_REFRESH);
        sendBroadcast(intent);
    }

}
