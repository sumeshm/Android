package com.planetjup.dnd;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class will manage the Do-Not-Disturb quick settings toggle functionality
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */

public class DndTileService extends TileService implements View.OnClickListener {

    private static final String TAG = DndTileService.class.getSimpleName();
    private static boolean isAllowed = Boolean.FALSE;
    private SeekBar seekBar;
    private TextView textViewSeek;
    private Dialog dialog;

    private AudioManager audioManager;
    private NotificationManager notificationManager;
    private BroadcastReceiver ringerModeReceiver;

    private boolean isTimerCancel = Boolean.FALSE;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate()");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        registerListenerHere();
        prepareDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
        unregisterReceiver(ringerModeReceiver);
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.v(TAG, "onTileAdded()");

        isAllowed = Boolean.FALSE;
        changeIcon(audioManager.getRingerMode());
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.v(TAG, "onClick()");

        if (!getPermission()) {
            return;
        }

        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                isTimerCancel = Boolean.TRUE;
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                printAudioMode();
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                showDnDDialog();
                exitService();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(View) : view_id=" + view.getId());

        int interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;

        final RadioGroup radioGroup = dialog.getWindow().findViewById(R.id.radio_group_mode);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_total:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;
                break;

            case R.id.radio_priority:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_PRIORITY;
                break;

            case R.id.radio_alarm:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_ALARMS;
                break;
        }

        switch (view.getId()) {
            case R.id.radio_15:
                isTimerCancel = Boolean.FALSE;
                changeMode(AudioManager.RINGER_MODE_SILENT, interruptionMode);
                startCountdownTimer(15 * 60 * 1000);
                break;

            case R.id.radio_30:
                isTimerCancel = Boolean.FALSE;
                changeMode(AudioManager.RINGER_MODE_SILENT, interruptionMode);
                startCountdownTimer(30 * 60 * 1000);
                break;

            case R.id.radio_60:
                isTimerCancel = Boolean.FALSE;
                changeMode(AudioManager.RINGER_MODE_SILENT, interruptionMode);
                startCountdownTimer(60 * 60 * 1000);
                break;

            case R.id.radio_infinity:
                isTimerCancel = Boolean.TRUE;
                changeMode(AudioManager.RINGER_MODE_SILENT, interruptionMode);
                break;

            case R.id.buttonOk:
                isTimerCancel = Boolean.FALSE;
                if (seekBar.getProgress() > 0) {
                    changeMode(AudioManager.RINGER_MODE_SILENT, interruptionMode);
                    startCountdownTimer(seekBar.getProgress() * 60 * 1000);
                }
                break;

            case R.id.buttonMusic:
                Log.v(TAG, "onClick(View) : MUTE media stream");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
                break;
        }

        hideDnDDialog();
    }

    private void registerListenerHere() {
        Log.v(TAG, "registerListenerHere()");
        ringerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "registerListenerHere::onReceive()");
                changeIcon(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        this.registerReceiver(ringerModeReceiver, filter);
    }

    private boolean getPermission() {
        Log.v(TAG, "getPermission() : isAllowed=" + isAllowed);

        if (isAllowed) {
            return Boolean.TRUE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {

                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                startActivity(intent);
                exitService();
            } else {
                isAllowed = Boolean.TRUE;
            }
        }

        return isAllowed;
    }

    private void prepareDialog() {
        Log.v(TAG, "prepareDialog()");

        dialog = new Dialog(this, R.style.dnd_dialog);
        dialog.setContentView(R.layout.layout_dnd_dialog);
        dialog.setTitle(R.string.app_name);

        textViewSeek = dialog.getWindow().findViewById(R.id.textViewSeek);

        seekBar = dialog.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressVal, boolean fromUser) {
                textViewSeek.setText(progressVal + " " + getString(R.string.Min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void changeIcon(int mode) {
        Log.v(TAG, "changeIcon : mode=" + mode);

        switch (mode) {
            case AudioManager.RINGER_MODE_SILENT:
                if (this.getQsTile() != null) {
                    this.getQsTile().setIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_do_not_disturb_on));
                }
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                if (this.getQsTile() != null) {
                    this.getQsTile().setIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_do_not_disturb_off));
                }
                break;
        }

        if (this.getQsTile() != null) {
            this.getQsTile().updateTile();
        }
    }

    private void changeMode(int newRingerMode, int newInterruptionMode) {
        Log.v(TAG, "changeMode : isAllowed=" + isAllowed + ", newRingerMode=" + newRingerMode + ", newInterruptionMode=" + newInterruptionMode);

        if (isAllowed) {
            int ringerMode = audioManager.getRingerMode();
            if (ringerMode != newRingerMode) {
                audioManager.setRingerMode(newRingerMode);
                if (newRingerMode == AudioManager.RINGER_MODE_SILENT) {
                    notificationManager.setInterruptionFilter(newInterruptionMode);
                }
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
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }

    private void startCountdownTimer(long duration) {
        Log.v(TAG, "startCountdownTimer() : duration=" + duration);

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
                changeMode(AudioManager.RINGER_MODE_NORMAL, NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        }.start();
    }

    private void showDnDDialog() {
        Log.v(TAG, "showDnDDialog()");

        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.layout_dnd_dialog);
            dialog.setTitle(R.string.app_name);
        }

        showDialog(dialog);
    }

    private void hideDnDDialog() {
        Log.v(TAG, "hideDnDDialog()");

        if (dialog != null) {
            final RadioGroup radioGroup = dialog.getWindow().findViewById(R.id.radio_group);
            radioGroup.clearCheck();
            dialog.dismiss();
            dialog = null;
        }
    }

    private void exitService() {
        Log.v(TAG, "exitService()");

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeIntent);
    }
}
