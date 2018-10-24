package com.planetjup.tasks.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.planetjup.tasks.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import planetjup.com.tasks.R;

public class AlarmJobService extends JobService {

    private static final String TAG = AlarmJobService.class.getSimpleName();

    public static final String EXTRA_REMINDER_ORIGIN = "com.planetjup.tasks.extra.EXTRA_REMINDER_ORIGIN";
    public static final String EXTRA_REMINDER_TYPE = "com.planetjup.tasks.extra.EXTRA_REMINDER_TYPE";
    public static final String EXTRA_REMINDER_DAY = "com.planetjup.tasks.extra.EXTRA_REMINDER_DAY";
    public static final String EXTRA_REMINDER_HOUR = "com.planetjup.tasks.extra.EXTRA_REMINDER_HOUR";
    public static final String EXTRA_REMINDER_MINUTE = "com.planetjup.tasks.extra.EXTRA_REMINDER_MINUTE";


    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.v(TAG, "onStartJob():");

        sendQuickTasksNotification(getApplicationContext(), params.getExtras());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.v(TAG, "onStopJob():");
        return true;
    }

    private void sendQuickTasksNotification(Context context, PersistableBundle bundle) {
        Log.v(TAG, "sendQuickTasksNotification()");
        Intent reminderIntent = new Intent(context, MainActivity.class);

        int requestType = bundle.getInt(EXTRA_REMINDER_TYPE, 0);

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

            Date curDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd hh:mm:ss");
            Log.v(TAG, "sendQuickTasksNotification(): " + format.format(curDate) + " :: id=" + requestType);

            Notification notification = new NotificationCompat.Builder(context, channel.getId())
                    .setSmallIcon(R.drawable.ic_notification)
                    //.setContentText(context.getString(R.string.msg_notification))
                    .setContentText(format.format(curDate) + " - " + requestType)
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
}
