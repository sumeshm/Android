package com.planetjup.contacts;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * This class will manage the Do-Not-Disturb app
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */

public class ContactsMainActivity extends AppCompatActivity {

    private static final String TAG = ContactsMainActivity.class.getSimpleName();
    private static final int ON_CONTACTS_PERMISSION_CALLBACK_CODE = 0;

    private static boolean isAllowed = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        getContactsPermission();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult :: requestCode=" + requestCode + ", permissions=" + permissions);

        if (requestCode == ON_CONTACTS_PERMISSION_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_PERMISSION_CALLBACK_CODE");
            isAllowed = true;
        }
    }

    private boolean getContactsPermission() {
        Log.v(TAG, "getContactsPermission : isAllowed=" + isAllowed);

        if (isAllowed) {
            return Boolean.TRUE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CONTACTS)) {

                String[] permissionList = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
                requestPermissions(permissionList, ON_CONTACTS_PERMISSION_CALLBACK_CODE);
            } else {
                isAllowed = Boolean.TRUE;
            }
        }

        return isAllowed;
    }
}
