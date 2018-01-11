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

public class DnDMainActivity extends AppCompatActivity {

    private static final String TAG = DnDMainActivity.class.getSimpleName();
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 0;

    private static boolean isAllowed = Boolean.FALSE;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (!getPermission()) {
            Log.v(TAG, "exitActivity : msg=" + R.string.Err_permission);
            finish();
        }
        else {
            setContentView(R.layout.activity_dnd_driver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "unregisterReceiver() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult :: requestCode=" + requestCode);

        if (requestCode == DnDMainActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_DO_NOT_DISTURB_CALLBACK_CODE");
            isAllowed = true;
        }
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

                startActivityForResult(intent, DnDMainActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE);
            } else {
                isAllowed = Boolean.TRUE;
            }
        }

        return isAllowed;
    }

    private void exitActivity(String msg) {
        Log.v(TAG, "exitActivity : msg=" + msg);

        finish();
    }
}
