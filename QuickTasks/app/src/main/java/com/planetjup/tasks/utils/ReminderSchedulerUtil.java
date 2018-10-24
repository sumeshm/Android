package com.planetjup.tasks.utils;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.os.PersistableBundle;
import android.util.Log;

import com.planetjup.tasks.reminder.AlarmJobService;

import java.util.Calendar;

public class ReminderSchedulerUtil {
    private static final String TAG = ReminderSchedulerUtil.class.getSimpleName();


    public static JobInfo getJobInfo(ReminderDetails reminderDetails, ComponentName componentName) {
        Log.d(TAG, "getJobInfo()");

        Calendar currCalendar = Calendar.getInstance();
        Calendar reminderCalendar = getNextCalendar(reminderDetails);
        long intervalMills = reminderCalendar.getTimeInMillis() - currCalendar.getTimeInMillis();

//        Calendar recurCalendar = getRecurCalendar(reminderCalendar);
//        long periodicMills = recurCalendar.getTimeInMillis() - reminderCalendar.getTimeInMillis();

        final PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(AlarmJobService.EXTRA_REMINDER_ORIGIN, reminderDetails.getReminderOrigin().getValue());
        bundle.putInt(AlarmJobService.EXTRA_REMINDER_TYPE, reminderDetails.getReminderType().getValue());
        bundle.putInt(AlarmJobService.EXTRA_REMINDER_DAY, reminderDetails.getDay());
        bundle.putInt(AlarmJobService.EXTRA_REMINDER_HOUR, reminderDetails.getHour());
        bundle.putInt(AlarmJobService.EXTRA_REMINDER_MINUTE, reminderDetails.getMinute());

        return new JobInfo.Builder(reminderDetails.getReminderType().getValue(), componentName)
                .setMinimumLatency(intervalMills)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle)
                .build();
    }

    private static Calendar getNextCalendar(ReminderDetails reminderDetails) {
        Log.v(TAG, "getNextCalendar()");
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
            nextCalendar.add(Calendar.MONTH, 1);
        }

        Log.v(TAG, "getNextCalendar() : reminder_type=" + reminderDetails.getReminderType());
        Log.v(TAG, "getNextCalendar() : currCalendar=" + currCalendar.getTime());
        Log.v(TAG, "getNextCalendar() : nextCalendar=" + nextCalendar.getTime());

        return nextCalendar;
    }

    private static Calendar getRecurCalendar(Calendar nextCalendar) {
        Log.v(TAG, "getRecurCalendar()");
        Calendar recurCalendar = Calendar.getInstance();
        recurCalendar.set(Calendar.SECOND, 0);
        recurCalendar.set(Calendar.MINUTE, nextCalendar.get(Calendar.MINUTE));
        recurCalendar.set(Calendar.HOUR_OF_DAY, nextCalendar.get(Calendar.HOUR_OF_DAY));
        recurCalendar.set(Calendar.DAY_OF_MONTH, nextCalendar.get(Calendar.DAY_OF_MONTH));
        recurCalendar.set(Calendar.MONTH, nextCalendar.get(Calendar.MONTH));

        // recur every month
        recurCalendar.add(Calendar.MONTH, 1);
        Log.v(TAG, "getNextCalendar() : reminder_cal=" + nextCalendar.getTime());
        Log.v(TAG, "getNextCalendar() :    recur_cal=" + recurCalendar.getTime());

        return recurCalendar;
    }
}
