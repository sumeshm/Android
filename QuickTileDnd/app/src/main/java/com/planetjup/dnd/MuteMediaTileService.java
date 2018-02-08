package com.planetjup.dnd;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

/**
 * This class will manage the Mute-Media quick settings tile functionality.
 * <p>
 * Created by Sumesh Mani on 2/8/18.
 */
public class MuteMediaTileService extends TileService
{
    private static final String TAG = DndTileService.class.getSimpleName();

    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(TAG, "onCreate()");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.v(TAG, "onStartListening()");

        Tile muteMediaTile = this.getQsTile();
        if (muteMediaTile != null)
        {
            int mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.v(TAG, "onStartListening() : mediaVolume=" + mediaVolume);
            if (mediaVolume == 0)
            {
                muteMediaTile.setState(Tile.STATE_UNAVAILABLE);
            }
            else
            {
                muteMediaTile.setState(Tile.STATE_ACTIVE);
            }

            muteMediaTile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.v(TAG, "onClick()");

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        Toast.makeText(this, R.string.toast_mute_media, Toast.LENGTH_SHORT).show();

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getBaseContext().sendBroadcast(closeIntent);
    }
}
