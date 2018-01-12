package com.planetjup.dnd.old;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.planetjup.dnd.R;

/**
 * This class will manage the Do-Not-Disturb app
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */

public class DriverActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DriverActivity.class.getSimpleName();
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 0;
    private static boolean isCreatedBefore = false;
    private static boolean isAllowed = false;

    private AudioManager audioMgr;
    private NotificationManager notificationManager;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver);

        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        getPermission();

        findViewById(R.id.button15).setOnClickListener(this);
        findViewById(R.id.button30).setOnClickListener(this);
        findViewById(R.id.button60).setOnClickListener(this);
        findViewById(R.id.buttonStop).setOnClickListener(this);
        findViewById(R.id.buttonClose).setOnClickListener(this);

        if (!isCreatedBefore) {
            isCreatedBefore = true;
            exitApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult :: requestCode=" + requestCode);

        if (requestCode == DriverActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_DO_NOT_DISTURB_CALLBACK_CODE");
            isAllowed = true;
        }
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick : " + view.getId());

        switch (view.getId()) {
            case R.id.button15:
                changeMode(AudioManager.RINGER_MODE_SILENT);
                startCountdownTimer(15 * 60 * 1000);
                break;

            case R.id.button30:
                changeMode(AudioManager.RINGER_MODE_SILENT);
                startCountdownTimer(30 * 60 * 1000);
                break;

            case R.id.button60:
                changeMode(AudioManager.RINGER_MODE_SILENT);
                startCountdownTimer(60 * 60 * 1000);
                break;

            case R.id.buttonStop:
                countDownTimer.cancel();
                changeMode(AudioManager.RINGER_MODE_NORMAL);
                break;

            case R.id.buttonClose:
                break;
        }

        exitApp();
    }

    private void getPermission() {
        if (isAllowed) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {

                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                startActivityForResult(intent, DriverActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE);
            } else {
                isAllowed = true;
            }
        }
    }

    private void changeMode(int newRingerMode) {
        if (isAllowed) {
            int ringerMode = audioMgr.getRingerMode();
            if (ringerMode != newRingerMode) {
                audioMgr.setRingerMode(newRingerMode);
            }

            printAudioMode();
        }
    }

    private void printAudioMode() {
        String toastMsg = "";

        switch (audioMgr.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                toastMsg = "Ringer is in Silent mode";
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                toastMsg = "Ringer is in Vibrate mode";
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                toastMsg = "Ringer is in Normal mode";
                break;
        }

        Toast.makeText(DriverActivity.this, toastMsg, Toast.LENGTH_LONG).show();
    }

    private void startCountdownTimer(long duration) {
        countDownTimer = new CountDownTimer(duration, duration) {
            @Override
            public void onTick(long l) {

            }

            public void onFinish() {
                changeMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }.start();
    }

    private void exitApp() {
        // todo : add other cleanup here
        finish();
    }
}
