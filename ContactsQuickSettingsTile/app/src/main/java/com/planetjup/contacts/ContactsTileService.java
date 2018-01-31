package com.planetjup.contacts;

import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

/**
 * Created by summani on 1/30/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactsTileService extends TileService {

    private static final String TAG = ContactsTileService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
