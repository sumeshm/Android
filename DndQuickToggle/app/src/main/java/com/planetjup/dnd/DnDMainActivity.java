package com.planetjup.dnd;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class will manage the Do-Not-Disturb app
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */

public class DnDMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DnDMainActivity.class.getSimpleName();
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 0;

    private static boolean isAllowed = Boolean.FALSE;

    private boolean isTimerCancel = Boolean.FALSE;

    private AudioManager audioManager;
    private NotificationManager notificationManager;
    private BroadcastReceiver ringerModeReceiver;
    private SeekBar seekBar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (!getPermission()) {
            exitActivity(getString(R.string.Err_permission));
        } else {
            setContentView(R.layout.activity_dnd_driver);

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            prepareSeekBar();
            //registerListenerHere();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(ringerModeReceiver);
        Log.v(TAG, "unregisterReceiver() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult :: requestCode=" + requestCode);

        if (requestCode == DnDMainActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_DO_NOT_DISTURB_CALLBACK_CODE");
            isAllowed = true;
        }
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick : " + view.getId());

        if (!getPermission()) {
            exitActivity(getString(R.string.Err_permission));
        }

        switch (view.getId()) {
            case R.id.radio_15:
                isTimerCancel = Boolean.FALSE;
                changeMode(AudioManager.RINGER_MODE_SILENT);
                startCountdownTimer(15 * 60 * 1000);
                break;

            case R.id.radio_30:
                isTimerCancel = Boolean.FALSE;
                changeMode(AudioManager.RINGER_MODE_SILENT);
                startCountdownTimer(30 * 60 * 1000);
                break;

            case R.id.radio_60:
                isTimerCancel = Boolean.FALSE;
                changeMode(AudioManager.RINGER_MODE_SILENT);
                startCountdownTimer(60 * 60 * 1000);
                break;

            case R.id.buttonOk:
                isTimerCancel = Boolean.FALSE;
                changeMode(AudioManager.RINGER_MODE_SILENT);
                startCountdownTimer(seekBar.getProgress() * 60 * 1000);
                break;

            case R.id.buttonStop:
                isTimerCancel = Boolean.TRUE;
                changeMode(AudioManager.RINGER_MODE_NORMAL);
                break;

            case R.id.buttonClose:
                break;
        }

        exitActivity(getString(R.string.Err_none));
    }

    private boolean getPermission() {
        Log.v(TAG, "getPermission : isAllowed=" + isAllowed);

        if (isAllowed) {
            return Boolean.TRUE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {

                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                startActivityForResult(intent, DnDMainActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE);
            } else {
                isAllowed = Boolean.TRUE;
            }
        }

        return isAllowed;
    }

    private void prepareSeekBar() {
        Log.v(TAG, "prepareSeekBar");

        progressText = (TextView) findViewById(R.id.progressText);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressVal, boolean fromUser) {
                progressText.setText(progressVal + " Min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void registerListenerHere() {
        Log.v(TAG, "registerListener() called");

        ringerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "onReceive : " + intent.getAction());
                isTimerCancel = Boolean.TRUE;
                Toast.makeText(DnDMainActivity.this, "Custom Intent received", Toast.LENGTH_LONG).show();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.dnd_broadcast));
        this.registerReceiver(ringerModeReceiver, filter);
    }

    private void changeMode(int newRingerMode) {
        Log.v(TAG, "changeMode : newRingerMode = " + newRingerMode);

        if (isAllowed) {
            int ringerMode = audioManager.getRingerMode();
            if (ringerMode != newRingerMode) {
                audioManager.setRingerMode(newRingerMode);
            }

            printAudioMode();
        }
    }

    private void printAudioMode() {
        String toastMsg = "";

        switch (audioManager.getRingerMode()) {
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

        Log.v(TAG, "printAudioMode : toastMsg=" + toastMsg);
        Toast.makeText(DnDMainActivity.this, toastMsg, Toast.LENGTH_LONG).show();
    }

    private void startCountdownTimer(long duration) {
        Log.v(TAG, "startCountdownTimer : duration=" + duration);

        isTimerCancel = Boolean.FALSE;

        final CountDownTimer countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                if (isTimerCancel) {
                    Log.v(TAG, "CountdownTimer : CANCEL");
                    cancel();
                    isTimerCancel = Boolean.FALSE;
                }
            }

            public void onFinish() {
                Log.v(TAG, "CountdownTimer : FINISH");
                changeMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }.start();
    }


    private void exitActivity(String msg) {
        Log.v(TAG, "exitActivity : msg=" + msg);

        finish();
    }
}
