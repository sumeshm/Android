package planetjup.com.dndquicktoggle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by summani on 1/9/18.
 */

public class DndTileService extends TileService {

    private static final String TAG = DndTileService.class.getSimpleName();

    private BroadcastReceiver ringerModeReceiver = null;
    private AudioManager audioMgr;

    @Override
    public void onCreate() {
        super.onCreate();

        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        ringerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                    changeIcon(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
                }
            }
        };
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.v(TAG, "onTileAdded");
        // 1. check DND status and update icon to corresponding value
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.v(TAG, "onTileRemoved");
        // 1. change icon to app icon
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.v(TAG, "onClick");

        // 1. check current status
        // 2. change icon
        // 3. get timer value and start it
        // 4. startActivityAndCollapse(calendarIntent)

        Intent intent = new Intent(this, DriverActivity.class);
        startActivity(intent);

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeIntent);
    }

    protected void changeIcon(int mode)
    {
        Log.v(TAG, "changeIcon");

        switch (mode) {
            case AudioManager.RINGER_MODE_SILENT:
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                break;
        }
    }
}
