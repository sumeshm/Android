package com.planetjup.tasks.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.planetjup.tasks.MainActivity;

import java.util.Calendar;

import planetjup.com.tasks.R;

/**
 * This class will receive broadcasts sent by main activity. This class will setup repeating reminders.
 * This reminder will be triggered every month, on the 18th day. For each trigger from AlarmManager,
 * a push-notification is created and the next reminder is queued.
 *
 * This class will also listen for REBOOT event, so that the reminder can be queued again.
 * This class will behave like a pseudo service, which does not have to be kept alive.
 *
 * <p>
 * Created by Sumesh Mani on 2/20/18.
 */

public class ReminderBroadcastReceiver extends android.content.BroadcastReceiver {

    public static final String ACTION_START_ALARM = "com.planetjup.tasks.action.START_ALARM";
    public static final String ACTION_SEND_REMINDER = "com.planetjup.tasks.action.SEND_REMINDER";

    public static final String EXTRA_DAY = "com.planetjup.tasks.extra.EXTRA_DAY";
    public static final String EXTRA_HOUR = "com.planetjup.tasks.extra.EXTRA_HOUR";

    private static final String TAG = ReminderBroadcastReceiver.class.getSimpleName();

    public int reminderDay;
    public int reminderHour;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive()");

        if (intent.getAction() != null) {
            Log.v(TAG, "onReceive() : Intent.Action=" + intent.getAction());

            if (intent.getAction().equalsIgnoreCase(ACTION_SEND_REMINDER)) {
                sendQuickTasksNotification(context.getApplicationContext());
            }

            reminderDay = intent.getIntExtra(EXTRA_DAY, 0);
            reminderHour = intent.getIntExtra(EXTRA_HOUR, 0);

            startDelayedAlarm(context.getApplicationContext());
            startDelayedAlarm(context.getApplicationContext());
        }
    }

    public void sendQuickTasksNotification(Context context) {
        Log.v(TAG, "sendQuickTasksNotification()");
        Intent reminderIntent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel channel = new NotificationChannel(
                context.getPackageName(),
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(context, channel.getId())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(context.getString(R.string.msg_notification))
                .setColor(context.getColor(R.color.colorOrange))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(0)
                .build();

        notificationManager.notify(0, notification);
    }

    private void startDelayedAlarm(Context context) {
        Log.v(TAG, "startDelayedAlarm()");
        Calendar currCalendar = Calendar.getInstance();
        int currDayOfMonth = currCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.set(Calendar.SECOND, 0);
        nextCalendar.set(Calendar.MINUTE, 0);
        nextCalendar.set(Calendar.HOUR_OF_DAY, reminderHour);
        nextCalendar.set(Calendar.DAY_OF_MONTH, reminderDay);

        if (currDayOfMonth < reminderDay) {
            // notify starting this month
            nextCalendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH));
        } else {
            // notify from next month
            nextCalendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH) + 1);
        }

//         nextCalendar.set(Calendar.MINUTE, currCalendar.get(Calendar.MINUTE) + 1);

        Log.v(TAG, "startDelayedAlarm() : currCalendar=" + currCalendar.getTime());
        Log.v(TAG, "startDelayedAlarm() : nextCalendar=" + nextCalendar.getTime());

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.setAction(ACTION_SEND_REMINDER);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextCalendar.getTimeInMillis(), pendingIntent);
    }
}
