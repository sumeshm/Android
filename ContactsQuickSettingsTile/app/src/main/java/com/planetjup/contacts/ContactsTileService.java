package com.planetjup.contacts;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * This class will manage the Quick Dial Tile-Service in the quick settings
 * <p>
 * Created by Sumesh Mani on 1/31/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactsTileService extends TileService {

    private static final String TAG = ContactsTileService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate()");
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.v(TAG, "onClick()");

        Intent intent = new Intent();
        intent.setClass(this, ContactsListActivity.class);
        startActivityAndCollapse(intent);
    }
}
