package com.planetjup.tasks.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import planetjup.com.tasks.R;

/**
 * This class will start the ReminderService after phone boot-up
 * <p>
 * Created by Sumesh Mani on 2/20/18.
 */

public class ReminderBroadcastReceiver extends android.content.BroadcastReceiver {

    public static final String ACTION_SEND_REMINDER = "com.planetjup.tasks.action.SEND_REMINDER";

    private static final String TAG = ReminderService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive()");

        if (intent.getAction() != null)
        {
            Log.v(TAG, "onReceive() : Intent.Action=" + intent.getAction());

            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                context.startService(new Intent(context, ReminderService.class));
            } else if (intent.getAction().equalsIgnoreCase(ACTION_SEND_REMINDER)) {
                sendQuickTasksNotification(context);

                Intent timerIntent = new Intent(context, ReminderService.class);
                timerIntent.setAction(ReminderService.ACTION_START_TIMER);
                context.startService(timerIntent);
            }
        }
    }

    public void sendQuickTasksNotification(Context context) {
        Intent reminderIntent = new Intent(context, ReminderService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel channel = new NotificationChannel(context.getPackageName(),
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getPackageName())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(context.getString(R.string.msg_notification))
                .setColor(context.getColor(R.color.colorOrange))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(0);

        notificationManager.notify(0, notificationBuilder.build());

    }
}
