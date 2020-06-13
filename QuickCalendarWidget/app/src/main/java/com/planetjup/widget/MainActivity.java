package com.planetjup.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.planetjup.widget.util.Constants;
import com.planetjup.widget.util.PersistenceManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, CustomRadioGroup.OnButtonClickedListener, CustomSeekBar.OnProgressChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int ON_CALENDAR_PERMISSION_CALLBACK_CODE = 12345;
    private static final int ON_ALARM_CALLBACK_CODE = 12346;
    private static final int ON_ALARM_CALLBACK_CODE2 = 12347;

    private Map<String, Integer> settingsMap = new HashMap<>();

    private LinearLayout placeHolder;
    private PreviewBox previewBox;

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

        // Build UI
        placeHolder = findViewById(R.id.placeHolder);

        // setup preview-box
        createPreviewBox();

        // setup seek-bar
        createSeekBar();

        // setup radio-groups
        createRadioGroup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult: requestCode=" + requestCode);

        if (requestCode == ON_CALENDAR_PERMISSION_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_CONTACTS_PERMISSION_CALLBACK_CODE");
        }
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed():");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_title)
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

        // exit activity
        finish();
    }

    @Override
    public void progressChanged(int progress) {
        Log.v(TAG, "progressChanged(): progress=" + progress);
        settingsMap.put(Constants.KEY_ALPHA, progress);

        previewBox.updateBackground(
                progress,
                settingsMap.get(Constants.KEY_BG_COLOR),
                settingsMap.get(Constants.KEY_DAY_COLOR),
                settingsMap.get(Constants.KEY_DATE_COLOR),
                settingsMap.get(Constants.KEY_EVENT_COLOR));
    }

    @Override
    public void radioButtonClicked(String listenerId, int color) {
        Log.v(TAG, "radioButtonClicked(): listenerId=" + listenerId + ", color=" + color);

        // update settings map
        switch (listenerId) {
            case Constants.KEY_BG_COLOR:
                settingsMap.put(Constants.KEY_BG_COLOR, color);
                break;
            case Constants.KEY_DAY_COLOR:
                settingsMap.put(Constants.KEY_DAY_COLOR, color);
                break;
            case Constants.KEY_DATE_COLOR:
                settingsMap.put(Constants.KEY_DATE_COLOR, color);
                break;
            case Constants.KEY_EVENT_COLOR:
                settingsMap.put(Constants.KEY_EVENT_COLOR, color);
                break;
            case Constants.KEY_TODAY_COLOR:
                settingsMap.put(Constants.KEY_TODAY_COLOR, color);
                break;
        }

        // update preview
        previewBox.updateBackground(
                settingsMap.get(Constants.KEY_ALPHA),
                settingsMap.get(Constants.KEY_BG_COLOR),
                settingsMap.get(Constants.KEY_DAY_COLOR),
                settingsMap.get(Constants.KEY_DATE_COLOR),
                settingsMap.get(Constants.KEY_EVENT_COLOR));
    }

    private void createPreviewBox() {
        Log.v(TAG, "createPreviewBox:");

        previewBox = new PreviewBox(getApplicationContext());
        previewBox.setOnClickListener(this);
        previewBox.updateBackground(
                settingsMap.get(Constants.KEY_ALPHA),
                settingsMap.get(Constants.KEY_BG_COLOR),
                settingsMap.get(Constants.KEY_DAY_COLOR),
                settingsMap.get(Constants.KEY_DATE_COLOR),
                settingsMap.get(Constants.KEY_EVENT_COLOR));

        placeHolder.addView(previewBox, 0);
    }

    private void createSeekBar() {
        Log.v(TAG, "createPreviewBox:");

        CustomSeekBar seekBar = new CustomSeekBar(getApplicationContext(), settingsMap.get(Constants.KEY_ALPHA));
        seekBar.setOnProgressChangedListener(this);

        placeHolder.addView(seekBar, 1);
    }

    private void createRadioGroup() {
        int[] colorsList = getResources().getIntArray(R.array.colorList);
        Log.v(TAG, "createRadioGroup: colorsList.length=" + colorsList.length);

        CustomRadioGroup bgRadioGroup = new CustomRadioGroup(getApplicationContext(),
                Constants.KEY_BG_COLOR, getString(R.string.advice_backgroundColor), colorsList, settingsMap.get(Constants.KEY_BG_COLOR));
        bgRadioGroup.setUp(this);

        CustomRadioGroup dayRadioGroup = new CustomRadioGroup(getApplicationContext(),
                Constants.KEY_DAY_COLOR, getString(R.string.advice_dayColor), colorsList, settingsMap.get(Constants.KEY_DAY_COLOR));
        dayRadioGroup.setUp(this);

        CustomRadioGroup dateRadioGroup = new CustomRadioGroup(getApplicationContext(),
                Constants.KEY_DATE_COLOR, getString(R.string.advice_dateColor), colorsList, settingsMap.get(Constants.KEY_DATE_COLOR));
        dateRadioGroup.setUp(this);

        CustomRadioGroup eventRadioGroup = new CustomRadioGroup(getApplicationContext(),
                Constants.KEY_EVENT_COLOR, getString(R.string.advice_eventColor), colorsList, settingsMap.get(Constants.KEY_EVENT_COLOR));
        eventRadioGroup.setUp(this);

        CustomRadioGroup todayRadioGroup = new CustomRadioGroup(getApplicationContext(),
                Constants.KEY_TODAY_COLOR, getString(R.string.advice_todayColor), colorsList, settingsMap.get(Constants.KEY_TODAY_COLOR));
        todayRadioGroup.setUp(this);

        placeHolder.addView(bgRadioGroup);
        placeHolder.addView(dayRadioGroup);
        placeHolder.addView(dateRadioGroup);
        placeHolder.addView(eventRadioGroup);
        placeHolder.addView(todayRadioGroup);
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
        Log.v(TAG, "startDailyAlarm()");

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
        Log.v(TAG, "startHourlyAlarm(): day=" + calendar.get(Calendar.DAY_OF_MONTH));
        Log.v(TAG, "startHourlyAlarm(): hour=" + calendar.get(Calendar.HOUR_OF_DAY));
        Log.v(TAG, "startHourlyAlarm(): minute=" + calendar.get(Calendar.MINUTE));

        calendar.add(Calendar.MINUTE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Log.v(TAG, "startHourlyAlarm(): day2=" + calendar.get(Calendar.DAY_OF_MONTH));
        Log.v(TAG, "startHourlyAlarm(): hour2=" + calendar.get(Calendar.HOUR_OF_DAY));
        Log.v(TAG, "startHourlyAlarm(): minute=2" + calendar.get(Calendar.MINUTE));

        // designate alarm handler
        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(Constants.ACTION_UI_REFRESH_HOURLY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), ON_ALARM_CALLBACK_CODE2, intent, PendingIntent.FLAG_ONE_SHOT);

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
}
