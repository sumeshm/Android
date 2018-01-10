package planetjup.com.dndquicktoggle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.service.quicksettings.TileService;
import android.util.Log;

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
        registerListener();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.v(TAG, "onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.v(TAG, "onTileRemoved");
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.v(TAG, "onClick");

        switch (audioMgr.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                audioMgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                Intent intent = new Intent(this, DriverActivity.class);
                startActivity(intent);
                Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeIntent);
                break;
        }
    }


    protected void changeIcon(int mode) {
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

    protected void registerListener() {
        ringerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                changeIcon(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        this.registerReceiver(ringerModeReceiver, filter);
    }
}
