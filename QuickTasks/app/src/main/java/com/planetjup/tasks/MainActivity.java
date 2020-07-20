package com.planetjup.tasks;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.planetjup.tasks.tabs.TabManager;
import com.planetjup.tasks.utils.PersistenceManager;
import com.planetjup.tasks.utils.TaskDetails;
import com.planetjup.tasks.utils.TaskDetailsArrayAdapter;
import com.planetjup.tasks.utils.TaskDetailsExtended;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

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
    private ArrayList<TaskDetails> monthlyList;
    private ArrayList<TaskDetailsExtended> yearlyList;
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
                switch (tabManager.getFocusItem()) {
                    case 0:
                        showAddDialog();
                        break;
                    case 1:
                        showAddDialogExtended();
                        break;
                    default:
                        showAddDialog();
                        break;
                }
                break;

            case R.id.menuReset:
                tabManager.resetFocusList();
                break;

            case R.id.menuExport:
                switch (tabManager.getFocusItem()) {
                    case 0:
                        PersistenceManager.exportMonthlyTasksList(this, tabManager.getMonthlyList());
                        break;
                    case 1:
                        PersistenceManager.exportYearlyTasksList(this, tabManager.getYearlyList());
                        break;
                    default:
                        PersistenceManager.exportOtherTasksList(this, tabManager.getOtherList());
                        break;
                }
                break;

            case R.id.menuImport:
                switch (tabManager.getFocusItem()) {
                    case 0:
                        ArrayList<TaskDetails> monthlyList = PersistenceManager.importMonthlyTasksList(this);
                        tabManager.setMonthlyList(monthlyList);
                        break;
                    case 1:
                        ArrayList<TaskDetailsExtended> yearlyList = PersistenceManager.importYearlyTasksList(this);
                        tabManager.setYearlyList(yearlyList);
                        break;
                    default:
                        ArrayList<TaskDetails> otherList = PersistenceManager.importOtherTasksList(this);
                        tabManager.setOtherList(otherList);
                        break;
                }

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

                    tabManager.addItem(newTask, 0, 0);
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

    private void showAddDialogExtended() {
        Log.v(TAG, "showAddDialogExtended()");

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_extended, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.title_popup);
        builder.setIcon(R.drawable.ic_notification);
        builder.setView(dialogView);

        NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);

        NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(TaskDetailsExtended.getMonths());

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                Log.v(TAG, "showAddDialogExtended(): onValueChange: newVal=" + newVal + ", oldVal=" + oldVal);
                NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
                dayPicker.setValue(1);
                switch(newVal) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        dayPicker.setMaxValue(31);
                        break;
                    case 2:
                        int year = Calendar.getInstance().get(Calendar.YEAR);
                        if (year % 4 == 0) {
                            dayPicker.setMaxValue(29);
                        } else {
                            dayPicker.setMaxValue(28);
                        }
                        break;
                    default:
                        dayPicker.setMaxValue(30);
                        break;
                }
            }
        });

        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = dialogView.findViewById(R.id.editText);
                String newTask = editText.getText().toString().trim();

                NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
                int dayIndex = dayPicker.getValue();

                NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
                int monthIndex = monthPicker.getValue();

                if (!newTask.isEmpty()) {
                    // trim length if needed
                    if (newTask.length() > MAX_LENGTH) {
                        newTask = newTask.substring(0, MAX_LENGTH - 1);
                    }

                    tabManager.addItem(newTask, dayIndex, monthIndex);
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

