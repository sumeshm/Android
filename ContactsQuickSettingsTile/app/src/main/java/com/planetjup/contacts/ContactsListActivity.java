package com.planetjup.contacts;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will manage the Do-Not-Disturb app
 * <p>
 * Created by Sumesh Mani on 1/9/18.
 */
public class ContactsListActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = ContactsListActivity.class.getSimpleName();

    private ListView listView;
    private ArrayAdapter<String> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_contacts_list);

        List<String> dataList = new ArrayList<String>();
        dataList.add("Some Data");

        listAdapter = new ArrayAdapter<String>(this, R.layout.test_view, dataList);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(listAdapter);
//
//        listAdapter.add("Some Data");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.v(TAG, "onItemClick() : index=" + i);

    }
}
