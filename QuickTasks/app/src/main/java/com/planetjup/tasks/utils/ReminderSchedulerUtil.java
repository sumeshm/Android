package com.planetjup.tasks.utils;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.os.PersistableBundle;
import android.util.Log;

import com.planetjup.tasks.reminder.AlarmJobService;

import java.util.Calendar;

public class ReminderSchedulerUtil {
    private static final String TAG = ReminderSchedulerUtil.class.getSimpleName();


    public static JobInfo getActivityJobInfo(ReminderDetails reminderDetails, ComponentName componentName) {
        Log.d(TAG, "getActivityJobInfo()");

        Calendar currCalendar = Calendar.getInstance();
        Calendar reminderCalendar = getNextCalendar(reminderDetails);
        long intervalMills = reminderCalendar.getTimeInMillis() - currCalendar.getTimeInMillis();

        final PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(AlarmJobService.EXTRA_REMINDER_TYPE, reminderDetails.getReminderType().getValue());
        bundle.putString(AlarmJobService.EXTRA_REMINDER_TIME, reminderCalendar.getTime().toString());

        return new JobInfo.Builder(reminderDetails.getReminderType().getValue(), componentName)
                .setMinimumLatency(intervalMills)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle)
                .build();
    }

    public static JobInfo getServiceJobInfo(int type, ComponentName componentName) {
        Log.d(TAG, "getServiceJobInfo()");
        Calendar currCalendar = Calendar.getInstance();
        Calendar recurCalendar = getRecurCalendar(currCalendar);
        long intervalMills = recurCalendar.getTimeInMillis() - currCalendar.getTimeInMillis();

        final PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(AlarmJobService.EXTRA_REMINDER_TYPE, type);
        bundle.putString(AlarmJobService.EXTRA_REMINDER_TIME, recurCalendar.getTime().toString());

        return new JobInfo.Builder(type, componentName)
                .setMinimumLatency(intervalMills)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle)
                .build();
    }

    // Get reminder date based on user input. If its missed for this month, schedule it for next.
    private static Calendar getNextCalendar(ReminderDetails reminderDetails) {
        Log.v(TAG, "getNextCalendar()");
        Calendar currCalendar = Calendar.getInstance();
        int currDay = currCalendar.get(Calendar.DAY_OF_MONTH);
        int currHour = currCalendar.get(Calendar.HOUR_OF_DAY);
        int currMin = currCalendar.get(Calendar.MINUTE);

        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.set(Calendar.SECOND, 0);
        nextCalendar.set(Calendar.MINUTE, reminderDetails.getMinute());
        nextCalendar.set(Calendar.HOUR_OF_DAY, reminderDetails.getHour());
        nextCalendar.set(Calendar.DAY_OF_MONTH, reminderDetails.getDay());

        if (currDay <= reminderDetails.getDay() && currHour <= reminderDetails.getHour() && currMin < reminderDetails.getMinute()) {
            // notify starting this month
            nextCalendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH));
        } else {
            // notify from next month
            nextCalendar.add(Calendar.MONTH, 1);
        }

        Log.v(TAG, "getNextCalendar() : reminder_type=" + reminderDetails.getReminderType());
        Log.v(TAG, "getNextCalendar() : currCalendar=" + currCalendar.getTime());
        Log.v(TAG, "getNextCalendar() : nextCalendar=" + nextCalendar.getTime());

        return nextCalendar;
    }

    // Get reminder date, which is exactly one month from now
    private static Calendar getRecurCalendar(Calendar currCalendar) {
        Log.v(TAG, "getRecurCalendar()");
        Calendar recurCalendar = Calendar.getInstance();
        recurCalendar.set(Calendar.SECOND, 0);
        recurCalendar.set(Calendar.MINUTE, currCalendar.get(Calendar.MINUTE));
        recurCalendar.set(Calendar.HOUR_OF_DAY, currCalendar.get(Calendar.HOUR_OF_DAY));
        recurCalendar.set(Calendar.DAY_OF_MONTH, currCalendar.get(Calendar.DAY_OF_MONTH));
        recurCalendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH));

        // recur every month
        recurCalendar.add(Calendar.MONTH, 1);
        Log.v(TAG, "getNextCalendar() : reminder_cal=" + currCalendar.getTime());
        Log.v(TAG, "getNextCalendar() :    recur_cal=" + recurCalendar.getTime());

        return recurCalendar;
    }
}
