package com.planetjup.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will manage the Quick Dial list which is the Favorite Contacts.
 * User can click on the list items to dial corresponding phone number
 * <p>
 * Created by Sumesh Mani on 1/31/18.
 */
public class ContactsListActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = ContactsListActivity.class.getSimpleName();

    private static final int ON_CONTACTS_PERMISSION_CALLBACK_CODE = 0;
    private static final int ON_DIALER_PERMISSION_CALLBACK_CODE = 0;
    private static final int ON_DIALER_OPENED_CALLBACK_CODE = 0;

    private static boolean isContactsAllowed = Boolean.FALSE;
    private static boolean isDialerAllowed = Boolean.FALSE;

    private List<String> contactNames;
    private List<String> contactNumbers;


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
        Log.v(TAG, "onRequestPermissionsResult :: requestCode=" + requestCode);

        if (requestCode == ON_CONTACTS_PERMISSION_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_CONTACTS_PERMISSION_CALLBACK_CODE");
            isContactsAllowed = true;
            populateListView();
        } else if (requestCode == ON_DIALER_PERMISSION_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_DIALER_PERMISSION_CALLBACK_CODE");
            isDialerAllowed = true;
        } else if (requestCode == ON_DIALER_OPENED_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_DIALER_OPENED_CALLBACK_CODE");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        Log.v(TAG, "onItemClick() : index=" + index + ", phone_number=" + contactNumbers.get(index));

        if (getDialerPermission()) {
            String phoneNumber = this.contactNumbers.get(index);
            Log.v(TAG, "onItemClick() : dialing=" + phoneNumber);

            String phone = "tel:" + phoneNumber;
            Intent dialerIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phone));
            startActivityForResult(dialerIntent, ON_DIALER_OPENED_CALLBACK_CODE);
        }
    }

    private void populateListView() {
        String[] columns = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER, ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.STARRED + "='1'";

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, columns, selection, null, null);
        if (cursor == null) {
            Toast.makeText(this, getResources().getString(R.string.err_reading_contacts), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "populateListView() : Count=" + cursor.getCount());

        if (cursor.getCount() > 0) {
            contactNumbers = new ArrayList<>();
            contactNames = new ArrayList<>();

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                Log.i(TAG, "populateListView() : Id=" + id);
                Log.i(TAG, "populateListView() : HasPhone=" + hasPhone);

                if (hasPhone > 0) {
                    Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cursorPhone != null && cursorPhone.moveToFirst()) {
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String phone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i(TAG, "populateListView() : Name=" + name);
                        Log.i(TAG, "populateListView() : Phone=" + phone);
                        cursorPhone.close();

                        contactNumbers.add(phone);
                        contactNames.add(name);
                    }
                }
            }

            cursor.close();

            ArrayAdapter<String> cursorAdapter = new ArrayAdapter<>(this, R.layout.text_view, contactNames);
            ListView listView = findViewById(R.id.listView);
            listView.setOnItemClickListener(this);
            listView.setAdapter(cursorAdapter);
        } else {
            Toast.makeText(this, getResources().getString(R.string.err_invalid_contacts), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getContactsPermission() {
        Log.v(TAG, "getContactsPermission : isContactsAllowed=" + isContactsAllowed);

        if (isContactsAllowed) {
            return Boolean.TRUE;
        }

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)
                || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)) {

            String[] permissionList = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
            requestPermissions(permissionList, ON_CONTACTS_PERMISSION_CALLBACK_CODE);
            isContactsAllowed = Boolean.FALSE;
        } else {
            isContactsAllowed = Boolean.TRUE;
        }

        return isContactsAllowed;
    }

    private boolean getDialerPermission() {
        Log.v(TAG, "getDialerPermission : isContactsAllowed=" + isContactsAllowed);

        if (isDialerAllowed) {
            return Boolean.TRUE;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissionList = {Manifest.permission.CALL_PHONE};
            requestPermissions(permissionList, ON_DIALER_PERMISSION_CALLBACK_CODE);
            isDialerAllowed = Boolean.FALSE;
        } else {
            isDialerAllowed = Boolean.TRUE;
        }

        return isDialerAllowed;
    }
}