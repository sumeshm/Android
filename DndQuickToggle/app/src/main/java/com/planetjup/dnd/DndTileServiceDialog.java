package com.planetjup.dnd;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class will manage the Do-Not-Disturb quick settings toggle functionality
 *
 * Created by Sumesh Mani on 1/9/18.
 */

public class DndTileServiceDialog extends TileService implements View.OnClickListener {

    private static final String TAG = DndTileServiceDialog.class.getSimpleName();

    private SeekBar seekBar;
    private TextView progressText;
    private Dialog dialog;

    private NotificationManager notificationManager;
    private AudioManager audioMgr;
    private BroadcastReceiver ringerModeReceiver;
    private CountDownTimer countDownTimer;

    // todo
    private static boolean isAllowed = Boolean.FALSE;
    private boolean isTimerCancel = Boolean.FALSE;


    @Override
    public void onCreate() {
        super.onCreate();

        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //registerListenerHere();

        prepareDialog();
        getPermission();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(ringerModeReceiver);
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.v(TAG, "onClick()");

        switch (audioMgr.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                audioMgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                showDialog();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(View) : " + view.getId());

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

        hideDialog();
    }

    // todo
    private void registerListenerHere() {
        ringerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isTimerCancel = Boolean.TRUE;
                changeIcon(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        this.registerReceiver(ringerModeReceiver, filter);
    }

    private void changeIcon(int mode) {
        Log.v(TAG, "changeIcon");

        switch (mode) {
            case AudioManager.RINGER_MODE_SILENT:
                this.getQsTile().setIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_do_not_disturb_on));
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                this.getQsTile().setIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_do_not_disturb_off));
                break;
        }

        this.getQsTile().updateTile();
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

    private void showDialog()
    {
        if (dialog == null)
        {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.content_dialog_driver_radio);
            dialog.setTitle(R.string.Do_Not_Disturb);
        }

        showDialog(dialog);
    }

    private void hideDialog()
    {
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }

    // todo
    private void getPermission() {
        isAllowed = true;
    }


    private void prepareDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.content_dialog_driver_radio);
        dialog.setTitle(R.string.Do_Not_Disturb);

        progressText = (TextView) dialog.getWindow().findViewById(R.id.progressText);

        seekBar = (SeekBar) dialog.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }

    private void startCountdownTimer(long duration) {
        isTimerCancel = Boolean.FALSE;

        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                if (isTimerCancel)
                {
                    cancel();
                    isTimerCancel = Boolean.FALSE;
                }
            }

            public void onFinish() {
                changeMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }.start();
    }
}
