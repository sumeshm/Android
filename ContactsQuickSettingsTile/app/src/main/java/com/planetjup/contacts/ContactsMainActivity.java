package com.planetjup.contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * This class will manage the Quick Dial app
 * <p>
 * Created by Sumesh Mani on 1/31/18.
 */

public class ContactsMainActivity extends AppCompatActivity {

    private static final String TAG = ContactsMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }
}
