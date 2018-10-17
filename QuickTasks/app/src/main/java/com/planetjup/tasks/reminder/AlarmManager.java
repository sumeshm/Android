package com.planetjup.tasks.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.planetjup.tasks.MainActivity;
import com.planetjup.tasks.utils.ReminderDetails;

import java.util.Calendar;

import planetjup.com.tasks.R;

/**
 * This class will receive broadcasts sent by main activity. This class will setup repeating reminders.
 * This reminder will be triggered every month, on the 18th day. For each trigger from AlarmManager,
 * a push-notification is created and the next reminder is queued.
 * <p>
 * This class will also listen for REBOOT event, so that the reminder can be queued again.
 * This class will behave like a pseudo service, which does not have to be kept alive.
 * <p>
 * <p>
 * Created by Sumesh Mani on 2/20/18.
 */

public class AlarmManager extends android.content.BroadcastReceiver {

    public static final String ACTION_START_ALARM = "com.planetjup.tasks.action.START_ALARM";
    private static final String ACTION_SEND_REMINDER = "com.planetjup.tasks.action.SEND_REMINDER";

    public static final String EXTRA_REMINDER_TYPE = "com.planetjup.tasks.extra.EXTRA_REMINDER_TYPE";
    public static final String EXTRA_REMINDER_DAY = "com.planetjup.tasks.extra.EXTRA_REMINDER_DAY";
    public static final String EXTRA_REMINDER_HOUR = "com.planetjup.tasks.extra.EXTRA_REMINDER_HOUR";
    public static final String EXTRA_REMINDER_MINUTE = "com.planetjup.tasks.extra.EXTRA_REMINDER_MINUTE";

    private static final String TAG = AlarmManager.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive()");

        if (intent.getAction() != null) {
            Log.v(TAG, "onReceive() : Intent.Action=" + intent.getAction());

            int type = intent.getIntExtra(EXTRA_REMINDER_TYPE, 0);
            int day = intent.getIntExtra(EXTRA_REMINDER_DAY, 0);
            int hour = intent.getIntExtra(EXTRA_REMINDER_HOUR, 0);
            int minute = intent.getIntExtra(EXTRA_REMINDER_MINUTE, 0);

            ReminderDetails reminderDetails = new ReminderDetails(ReminderDetails.REMINDER_TYPE.getType(type), day, hour, minute);

            if (intent.getAction().equalsIgnoreCase(ACTION_SEND_REMINDER)) {
                sendQuickTasksNotification(context.getApplicationContext(), reminderDetails.getReminderType().getValue());
            }

            startDelayedAlarm(context.getApplicationContext(), reminderDetails);
        }
    }

    private void sendQuickTasksNotification(Context context, int requestType) {
        Log.v(TAG, "sendQuickTasksNotification()");
        Intent reminderIntent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel channel = new NotificationChannel(
                context.getPackageName() + requestType,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
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
        } else {
            Log.e(TAG, "sendQuickTasksNotification(): failed to get NotificationManager");
        }
    }

    private void startDelayedAlarm(Context context, ReminderDetails reminderDetails) {
        Log.v(TAG, "startDelayedAlarm()");
        Calendar currCalendar = Calendar.getInstance();
        int currDay = currCalendar.get(Calendar.DAY_OF_MONTH);
        int currMin = currCalendar.get(Calendar.MINUTE);

        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.set(Calendar.SECOND, 0);
        nextCalendar.set(Calendar.MINUTE, reminderDetails.getMinute());
        nextCalendar.set(Calendar.HOUR_OF_DAY, reminderDetails.getHour());
        nextCalendar.set(Calendar.DAY_OF_MONTH, reminderDetails.getDay());

        if (currDay <= reminderDetails.getDay() && currMin < reminderDetails.getMinute()) {
            // notify starting this month
            nextCalendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH));
        } else {
            // notify from next month
            nextCalendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH) + 1);
        }

        Log.v(TAG, "startDelayedAlarm() : reminder_type=" + reminderDetails.getReminderType());
        Log.v(TAG, "startDelayedAlarm() : currCalendar=" + currCalendar.getTime());
        Log.v(TAG, "startDelayedAlarm() : nextCalendar=" + nextCalendar.getTime());

        Intent intent = new Intent(context, AlarmManager.class);
        intent.setAction(ACTION_SEND_REMINDER);
        intent.putExtra(AlarmManager.EXTRA_REMINDER_TYPE, reminderDetails.getReminderType().getValue());
        intent.putExtra(AlarmManager.EXTRA_REMINDER_DAY, reminderDetails.getDay());
        intent.putExtra(AlarmManager.EXTRA_REMINDER_HOUR, reminderDetails.getHour());
        intent.putExtra(AlarmManager.EXTRA_REMINDER_MINUTE, reminderDetails.getMinute());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminderDetails.getReminderType().getValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, nextCalendar.getTimeInMillis(), pendingIntent);
        } else {
            Log.e(TAG, "startDelayedAlarm(): failed to get AlarmManager");
        }
    }
}
