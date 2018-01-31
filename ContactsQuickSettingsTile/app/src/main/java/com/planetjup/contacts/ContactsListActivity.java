package com.planetjup.contacts;

import android.Manifest;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class will manage the Do-Not-Disturb app
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */
public class ContactsListActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = ContactsListActivity.class.getSimpleName();
    private static final int ON_CONTACTS_PERMISSION_CALLBACK_CODE = 0;

    private static boolean isAllowed = Boolean.FALSE;

    private ListView listView;
    private SimpleCursorAdapter cursorAdapter;

    private final static String[] FROM_COLUMNS = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};

    private final static int[] TO_IDS = {
            R.id.text_view_details
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_contacts_list);

        if (!getContactsPermission()) {
            return;
        }

        populateListView();
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
            populateListView();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.v(TAG, "onItemClick() : index=" + i);

    }

    private void populateListView() {
        String[] columns = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.STARRED + "='1'";

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, columns, selection, null, null);
        if (cursor.getCount() > 0) {
            cursorAdapter = new SimpleCursorAdapter(this, R.layout.test_view, cursor, FROM_COLUMNS, TO_IDS, 0);
            listView = (ListView) findViewById(R.id.listView);
            listView.setOnItemClickListener(this);
            listView.setAdapter(cursorAdapter);
        } else {
            Toast.makeText(this, "No contacts found in facorites", Toast.LENGTH_SHORT).show();
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
