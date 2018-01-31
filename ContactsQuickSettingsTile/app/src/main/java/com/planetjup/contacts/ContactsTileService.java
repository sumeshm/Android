package com.planetjup.contacts;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by summani on 1/30/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactsTileService extends TileService {

    private static final String TAG = ContactsTileService.class.getSimpleName();
    private static final int ON_CONTACTS_PERMISSION_CALLBACK_CODE = 0;

    private static boolean isAllowed = Boolean.FALSE;

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
