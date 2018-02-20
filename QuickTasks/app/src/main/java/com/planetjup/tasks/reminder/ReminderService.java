package com.planetjup.tasks.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

/**
 * This service will remind user of pending task on a montly basis (18th of every month)
 * <p>
 * Created by Sumesh Mani on 2/20/18.
 */

public class ReminderService extends Service {

    public static final String ACTION_START_TIMER = "com.planetjup.tasks.action.START_TIMER";

    private static final String TAG = ReminderService.class.getSimpleName();

    private PendingIntent pendingIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate()");

        strtDelayedAlarm();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");


        if (intent.getAction() != null) {
            Log.v(TAG, "onStartCommand() : action=" + intent.getAction());

            if (intent.getAction().equalsIgnoreCase(ACTION_START_TIMER)) {
                strtDelayedAlarm();
            }
        }

        return START_STICKY;
    }

    private void strtDelayedAlarm()
    {
        Calendar currCalendar = Calendar.getInstance();

        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH) + 1);
        nextCalendar.set(Calendar.DAY_OF_MONTH, 18);
        nextCalendar.set(Calendar.HOUR_OF_DAY, 11);
//        nextCalendar.set(Calendar.MINUTE, currCalendar.get(Calendar.MINUTE) + 2);

        Log.v(TAG, "onCreate() : currCalendar=" + currCalendar.getTime());
        Log.v(TAG, "onCreate() : nextCalendar=" + nextCalendar.getTime());

        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        intent.setAction(ReminderBroadcastReceiver.ACTION_SEND_REMINDER);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextCalendar.getTimeInMillis(), pendingIntent);
    }
}
