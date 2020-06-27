package com.planetjup.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.planetjup.widget.util.Constants;
import com.planetjup.widget.util.IUserActionListener;
import com.planetjup.widget.util.PersistenceManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements IUserActionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int ON_CALENDAR_PERMISSION_CALLBACK_CODE = 12345;
    private static final int ON_ALARM_CALLBACK_CODE = 12346;
    private static final int ON_ALARM_CALLBACK_CODE2 = 12347;

    private Map<String, Integer> settingsMap = new HashMap<>();

    private LinearLayout placeHolder;
    private LinearLayout placeHolderPreview;
    private PreviewBox previewBox;

    private BroadcastReceiver chargerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getContactsPermission();
        startDailyAlarm();
        startHourlyDataSync();

        setContentView(R.layout.activity_main);

        // fetch settings from Storage
        settingsMap = PersistenceManager.readSettings(getApplicationContext());
        Log.v(TAG, "onCreate: settingsMap=" + settingsMap.toString());

        // Build UI
        placeHolder = findViewById(R.id.placeHolder);
        placeHolderPreview = findViewById(R.id.placeHolderPreview);

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
    public void radioButtonClicked(String listenerId, int color) {
        Log.v(TAG, "radioButtonClicked(): listenerId=" + listenerId + ", color=" + color);

        // update settings map
        settingsMap.put(listenerId, color);

        // update preview
        previewBox.updatePreview(settingsMap);
    }

    @Override
    public void checkBoxClicked(String checkBoxId, boolean isChecked) {
        Log.v(TAG, "checkBoxClicked(): isChecked=" + isChecked);

        // update settings map
        settingsMap.put(Constants.KEY_CLOCK_CHECKED, isChecked ? 1 : 0);

        // update preview
        previewBox.updatePreview(settingsMap);
    }

    @Override
    public void progressBarChanged(String checkBoxId, int progress) {
        Log.v(TAG, "progressBarChanged(): progress=" + progress);

        // update settings map
        settingsMap.put(Constants.KEY_ALPHA, progress);

        // update preview
        previewBox.updatePreview(settingsMap);
    }

    @Override
    public void saveButtonClicked() {
        Log.v(TAG, "saveButtonClicked():");

        // notify widget and persist settings
        publishSettings();

        // exit activity
        finish();
    }

    private void createPreviewBox() {
        Log.v(TAG, "createPreviewBox:");

        previewBox = new PreviewBox(getApplicationContext(), this);
        previewBox.updatePreview(settingsMap);

        placeHolderPreview.addView(previewBox);
    }

    private void createSeekBar() {
        Log.v(TAG, "createSeekBar:");

        CustomSeekBar seekBar = new CustomSeekBar(getApplicationContext(), settingsMap.get(Constants.KEY_ALPHA));
        seekBar.setUserActionListener(this);

        placeHolder.addView(seekBar);
    }

    private void createRadioGroup() {
        int[] colorsList = getResources().getIntArray(R.array.colorList);
        Log.v(TAG, "createRadioGroup: colorsList.length=" + colorsList.length);

        CustomRadioGroup bgRadioGroup = new CustomRadioGroup(getApplicationContext(),
                Constants.KEY_BG_COLOR, getString(R.string.advice_backgroundColor), colorsList, settingsMap.get(Constants.KEY_BG_COLOR));
        bgRadioGroup.setUp(this);

        CustomRadioGroup clockRadioGroup = new CustomRadioGroup(getApplicationContext(),
                Constants.KEY_CLOCK_COLOR, getString(R.string.advice_clockColor), colorsList, settingsMap.get(Constants.KEY_CLOCK_COLOR));
        clockRadioGroup.setUp(this);

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
        placeHolder.addView(clockRadioGroup);
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

    // Use JobScheduler to help sync calendar data every hour in the background
    private void startHourlyDataSync() {
        Log.v(TAG, "startHourlyDataSync()");

        ComponentName serviceComponent = new ComponentName(getApplicationContext(), SyncService.class);
        JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(0, serviceComponent);
        jobInfoBuilder.setPeriodic(60 * 60 * 1000);
        jobInfoBuilder.setRequiresBatteryNotLow(true);

        getApplicationContext().getSystemService(JobScheduler.class).schedule(jobInfoBuilder.build());
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
