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
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * This class will manage the Do-Not-Disturb quick settings toggle functionality
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */

public class DndTileServiceDialog extends TileService implements View.OnClickListener {

    private static boolean isAllowed = Boolean.FALSE;
    private static final String TAG = DndTileServiceDialog.class.getSimpleName();

    private SeekBar seekBar;
    private Button buttonOk;
    private Dialog dialog;

    private AudioManager audioManager;
    private NotificationManager notificationManager;
    private BroadcastReceiver ringerModeReceiver;

    private boolean isTimerCancel = Boolean.FALSE;


    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //registerListenerHere();

        prepareDialog();
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

        if (!getPermission()) {
            return;
        }

        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                isTimerCancel = Boolean.TRUE;
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                showDnDDialog();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(View) : view_id=" + view.getId());

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
        }

        hideDnDDialog();
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

                startActivity(intent);
                exitService();
            } else {
                isAllowed = Boolean.TRUE;
            }
        }

        return isAllowed;
    }

    private void prepareDialog() {
        Log.v(TAG, "prepareDialog");

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dnd_dialog);
        dialog.setTitle(R.string.Do_Not_Disturb);

        buttonOk = (Button) dialog.getWindow().findViewById(R.id.buttonOk);

        seekBar = (SeekBar) dialog.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressVal, boolean fromUser) {
                buttonOk.setText(progressVal + "" + getString(R.string.Min));
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
        Log.v(TAG, "changeMode : isAllowed=" + isAllowed);

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
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
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

    private void showDnDDialog() {
        Log.v(TAG, "showDnDDialog");

        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.layout_dnd_dialog);
            dialog.setTitle(R.string.Do_Not_Disturb);
        }

        showDialog(dialog);
    }

    private void hideDnDDialog() {
        Log.v(TAG, "hideDnDDialog");

        if (dialog != null) {
            final RadioGroup radioGroup = (RadioGroup) dialog.getWindow().findViewById(R.id.radio_group);
            radioGroup.clearCheck();
            dialog.dismiss();
        }
    }

    private void exitService() {
        Log.v(TAG, "exitService");

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeIntent);
    }
}
