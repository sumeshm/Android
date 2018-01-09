package planetjup.com.dndquicktoggle;

import android.content.Intent;
import android.service.quicksettings.TileService;
import android.util.Log;

/**
 * Created by summani on 1/9/18.
 */

public class DndTileService extends TileService {

    private static final String TAG = DndTileService.class.getSimpleName();

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
}
