package com.planetjup.dnd;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

/**
 * This class will manage the Do-Not-Disturb quick settings toggle tile functionality
 * 1. Provide Do-Not-Disturb features
 * 2. Allow user to select the type of DND - Total Silence, Priority Only and Alarm Only
 * 3. Allow user to enable DND with timeouts - 15 min, 30 min, 60 min, 0-120 min, indefinite
 * <p>
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */

public class DndTileService extends TileService {

    // custom actions that the dialog-activity can use to pass back user choices
    public static final String ACTION_START_TIMER = "com.planetjup.dnd.ACTION_START_TIMER";
    public static final String ACTION_MUTE_RINGER = "com.planetjup.dnd.ACTION_MUTE_RINGER";
    public static final String ACTION_MUTE_MUSIC = "com.planetjup.dnd.ACTION_MUTE_MUSIC";
    public static final String ACTION_MUTE_ALARM = "com.planetjup.dnd.ACTION_MUTE_ALARM";

    // keys for user data that will be passed along with the Intent's extra data
    public static final String KEY_INTERRUPTION_FILTER = "com.planetjup.dnd.KEY_INTERRUPTION_FILTER";
    public static final String KEY_DND_DURATION = "com.planetjup.dnd.KEY_RINGER_MODE";

    private static final String TAG = DndTileService.class.getSimpleName();

    private AudioManager audioManager;
    private NotificationManager notificationManager;
    private BroadcastReceiver ringerModeReceiver;

    private boolean isAllowed = Boolean.FALSE;
    private boolean isTimerCancel = Boolean.FALSE;
    private boolean isChangeRequested = Boolean.FALSE;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate()");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        registerListenerHere();
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.v(TAG, "onStartCommand() : action=" + action);

        switch (action) {
            case ACTION_START_TIMER:
                int interruptionMode = intent.getIntExtra(KEY_INTERRUPTION_FILTER, NotificationManager.INTERRUPTION_FILTER_NONE);
                int countDownTime = intent.getIntExtra(KEY_DND_DURATION, 0);
                Log.v(TAG, "onStartCommand() : interruptionMode=" + interruptionMode + ", countDownTime=" + countDownTime);

                changeMode(AudioManager.RINGER_MODE_SILENT, interruptionMode);
                if (countDownTime > 0) {
                    startCountdownTimer(countDownTime * 60 * 1000);
                }

                break;

            case ACTION_MUTE_RINGER:
                audioManager.setStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
                break;

            case ACTION_MUTE_MUSIC:
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
                break;

            case ACTION_MUTE_ALARM:
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
                break;
        }

        return START_STICKY;
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
                changeMode(AudioManager.RINGER_MODE_NORMAL, -1);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                showDndActivity();
                exitService();
                break;
        }
    }


    private void registerListenerHere() {
        Log.v(TAG, "registerListenerHere()");
        ringerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "registerListenerHere::onReceive() : " + intent.getAction());
                changeIcon(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));

                // show toast only if ringer mode chnage was triggered by this service
                if (isChangeRequested)
                {
                    isChangeRequested = Boolean.FALSE;
                    printAudioMode();
                }
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

    private void changeIcon(int mode) {
        Log.v(TAG, "changeIcon : mode=" + mode);

        Tile dndTile = this.getQsTile();

        if (mode == AudioManager.RINGER_MODE_SILENT) {
            int filter = notificationManager.getCurrentInterruptionFilter();
            if (filter == NotificationManager.INTERRUPTION_FILTER_NONE) {
                dndTile.setIcon(Icon.createWithResource(getBaseContext(), R.drawable.ic_dnd_tile_on_total));
            } else {
                dndTile.setIcon(Icon.createWithResource(getBaseContext(), R.drawable.ic_dnd_tile_on));
            }
        } else {
            dndTile.setIcon(Icon.createWithResource(getBaseContext(), R.drawable.ic_dnd_tile_off));
        }

        dndTile.setState(Tile.STATE_ACTIVE);
        dndTile.updateTile();
    }

    private void changeMode(int newRingerMode, int newInterruptionMode) {
        Log.v(TAG, "changeMode : isAllowed=" + isAllowed + ", newRingerMode=" + newRingerMode + ", newInterruptionMode=" + newInterruptionMode);

        if (isAllowed) {
            int ringerMode = audioManager.getRingerMode();
            if (ringerMode != newRingerMode) {
                isChangeRequested = Boolean.TRUE;
                isTimerCancel = Boolean.TRUE;

                audioManager.setRingerMode(newRingerMode);
                if (newInterruptionMode > 0 && newRingerMode == AudioManager.RINGER_MODE_SILENT) {
                    notificationManager.setInterruptionFilter(newInterruptionMode);
                }
            }
        }
    }

    private void printAudioMode() {
        String toastMsg = "";
        String toastPostfix = "";

        switch (notificationManager.getCurrentInterruptionFilter()) {
            case NotificationManager.INTERRUPTION_FILTER_NONE:
                toastPostfix = " - " + getString(R.string.toast_post_total);
                break;

            case NotificationManager.INTERRUPTION_FILTER_PRIORITY:
                toastPostfix = " - " + getString(R.string.toast_post_priority);
                break;

            case NotificationManager.INTERRUPTION_FILTER_ALARMS:
                toastPostfix = " - " + getString(R.string.toast_post_alarms);
                break;
        }

        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                toastMsg = getString(R.string.toast_silent) + toastPostfix;
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                toastMsg = getString(R.string.toast_vibrte);
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                toastMsg = getString(R.string.toast_normal);
                break;
        }

        Log.v(TAG, "printAudioMode : toastMsg=" + toastMsg);
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
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
                changeMode(AudioManager.RINGER_MODE_NORMAL, -1);
            }
        }.start();
    }

    private void showDndActivity() {
        Log.v(TAG, "showDndActivity()");

        Intent intent = new Intent();
        intent.setClass(this, DndPopupActivity.class);
        startActivity(intent);
    }

    private void exitService() {
        Log.v(TAG, "exitService()");

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeIntent);
    }
}
