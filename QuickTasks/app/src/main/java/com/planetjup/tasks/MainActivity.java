package com.planetjup.tasks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.planetjup.tasks.tabs.TabManager;
import com.planetjup.tasks.utils.PersistenceManager;
import com.planetjup.tasks.utils.TaskDetails;
import com.planetjup.tasks.utils.TaskDetailsArrayAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import planetjup.com.tasks.R;


/**
 * This class will manage a quick tasks list.
 * <p>
 * Created by Sumesh Mani on 1/16/18.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final int MAX_LENGTH = 20;
    private TabManager tabManager;
    private TaskDetailsArrayAdapter arrayAdapter;
    private ArrayList<TaskDetails> monthlyList;
    private ArrayList<TaskDetails> yearlyList;
    private ArrayList<TaskDetails> otherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        monthlyList = PersistenceManager.readMonthlyTasksList(this);
        yearlyList = PersistenceManager.readYearlyTasksList(this);
        otherList = PersistenceManager.readOtherTasksList(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // build tabs
        final ViewPager viewPager = findViewById(R.id.viewPager);
        tabManager = new TabManager(getSupportFragmentManager(), monthlyList, yearlyList, otherList);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.v(TAG, "TabSelectedListener: onTabSelected(): " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
                tabManager.setFocusItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.v(TAG, "TabSelectedListener: onTabUnselected(): " + tabManager.getFocusItem());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        final PagerAdapter adapter = tabManager;
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");

        PersistenceManager.writeMonthlyTasksList(this, tabManager.getMonthlyList());
        PersistenceManager.writeYearlyTasksList(this, tabManager.getYearlyList());
        PersistenceManager.writeOtherTasksList(this, tabManager.getOtherList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAdd:
                showAddDialog();
                break;

            case R.id.menuReset:
                tabManager.resetFocusList();
                break;
        }

        return true;
    }

    private void showAddDialog() {
        Log.v(TAG, "showAddDialog()");

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_view, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.title_popup);
        builder.setIcon(R.drawable.ic_notification);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = dialogView.findViewById(R.id.editText);
                String newTask = editText.getText().toString().trim();

                if (!newTask.isEmpty()) {
                    // trim length if needed
                    if (newTask.length() > MAX_LENGTH) {
                        newTask = newTask.substring(0, MAX_LENGTH - 1);
                    }

                    tabManager.addItem(newTask);
                }
            }
        });

        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}

