package com.planetjup.tasks.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.planetjup.tasks.MainTabActivity;
import com.planetjup.tasks.utils.ReminderSchedulerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import planetjup.com.tasks.R;

public class AlarmJobService extends JobService {

    private static final String TAG = AlarmJobService.class.getSimpleName();

    public static final String EXTRA_REMINDER_TYPE = "com.planetjup.tasks.extra.EXTRA_REMINDER_TYPE";
    public static final String EXTRA_REMINDER_TIME = "com.planetjup.tasks.extra.EXTRA_REMINDER_TIME";


    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.v(TAG, "onStartJob():");

        // show the current notification
        sendQuickTasksNotification(getApplicationContext(), params.getExtras());

        // queue the next/recurring notification one month from now
        int requestType = params.getExtras().getInt(EXTRA_REMINDER_TYPE, 0);
        String targetTime = params.getExtras().getString(AlarmJobService.EXTRA_REMINDER_TIME);
        Log.v(TAG, "onStartJob(): Reminder-" + requestType + " : target.time=" + targetTime);

        final JobInfo jobInfo = ReminderSchedulerUtil.getServiceJobInfo(requestType, new ComponentName(this, AlarmJobService.class));
        final JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            final int result = jobScheduler.schedule(jobInfo);
            Log.v(TAG, "onStartJob(): Reminder-" + requestType + " : schedule.result=" + result);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.v(TAG, "onStopJob():");
        return true;
    }

    private void sendQuickTasksNotification(Context context, PersistableBundle bundle) {
        Log.v(TAG, "sendQuickTasksNotification()");
        Intent reminderIntent = new Intent(context, MainTabActivity.class);

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
