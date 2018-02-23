package com.planetjup.dnd;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * This class will manage the Do-Not-Disturb app
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */

public class DndMainActivity extends AppCompatActivity {

    private static final String TAG = DndMainActivity.class.getSimpleName();
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 0;

    private static boolean isAllowed = Boolean.FALSE;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (!getPermission()) {
            Log.v(TAG, "exitActivity : msg=" + R.string.Err_permission);
            //finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");

        if (isAllowed) {
            startDndService();
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult() :: requestCode=" + requestCode);

        if (requestCode == DndMainActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE && notificationManager.isNotificationPolicyAccessGranted()) {
            Log.v(TAG, "onActivityResult: ON_DO_NOT_DISTURB_CALLBACK_CODE = Granted");
            isAllowed = true;
        }
        else {
            Log.v(TAG, "onActivityResult: ON_DO_NOT_DISTURB_CALLBACK_CODE = NOT Granted");
            finish();
        }
    }

    private boolean getPermission() {
        Log.v(TAG, "getPermission() : isAllowed=" + isAllowed);

        if (isAllowed) {
            startDndService();
            return Boolean.TRUE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {

                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                startActivityForResult(intent, DndMainActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE);
            } else {
                isAllowed = Boolean.TRUE;
            }
        }

        return isAllowed;
    }

    private void startDndService()
    {
        Log.v(TAG, "startDndService()");

        Intent dndIntent = new Intent(this, DndTileService.class);
        dndIntent.setAction(DndTileService.ACTION_SHOW_POPUP);
        startService(dndIntent);
    }
}
