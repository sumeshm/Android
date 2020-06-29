package com.planetjup.widget;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import com.planetjup.widget.util.Constants;

public class SyncService extends JobService {

    private static final String TAG = SyncService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.v(TAG, "onStartJob()");

        // notify widget about change in settings
        Intent intent = new Intent(getApplicationContext(), CalendarWidget.class);
        intent.setAction(Constants.ACTION_UI_REFRESH_HOURLY);
        sendBroadcast(intent);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.v(TAG, "onStartJob()");
        return false;
    }
}
