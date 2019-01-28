package com.planetjup.tasks;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.planetjup.tasks.fragments.DailyFragment;
import com.planetjup.tasks.fragments.MonthlyFragment;
import com.planetjup.tasks.fragments.TaskListFragment;
import com.planetjup.tasks.reminder.AlarmJobService;
import com.planetjup.tasks.utils.PersistenceManager;
import com.planetjup.tasks.utils.ReminderDetails;
import com.planetjup.tasks.utils.ReminderSchedulerUtil;
import com.planetjup.tasks.utils.TaskDetails;

import java.util.ArrayList;
import java.util.List;

import planetjup.com.tasks.R;

public class MainTabActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = MainTabActivity.class.getSimpleName();

    private int currentTabIndex;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<ReminderDetails> reminderList = new ArrayList<>();
    private ArrayList<TaskListFragment> fragmentList;

    private final int MAX_LENGTH = 20;
    private final int[] tabIcons = {
            R.drawable.ic_monthly,
            R.drawable.ic_daily
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_tabs);

        // add trip to DB
        String tripName = getIntent().getStringExtra("tripName");
        setTitle(tripName);

        setupTabs();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupTabs() {
        Log.v(TAG, "setupTabs()");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentList = new ArrayList<>();
        fragmentList.add(new MonthlyFragment());
        fragmentList.add(new DailyFragment());
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentList.get(0));
        adapter.addFragment(fragmentList.get(1));

        this.viewPager = findViewById(R.id.viewpager);
        this.viewPager.setAdapter(adapter);

        this.tabLayout = findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(viewPager);
        this.tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        this.tabLayout.getTabAt(1).setIcon(tabIcons[1]);

        this.tabLayout.addOnTabSelectedListener(this);
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
                fragmentList.get(currentTabIndex).resetListView();
                break;

            case R.id.menuReminderOne:
                showPickerDialog(reminderList.get(0));
                break;

            case R.id.menuReminderTwo:
                showPickerDialog(reminderList.get(1));
                break;

            case R.id.menuExport:
                PersistenceManager.exportTaskLists(getApplicationContext(), fragmentList.get(0).getTaskList(), fragmentList.get(1).getTaskList());
                break;

            case R.id.menuImport:
                ArrayList<TaskDetails> monthlyTaskLists = PersistenceManager.importMonthlyTaskLists(getApplicationContext());
                fragmentList.get(0).clearListView();
                fragmentList.get(0).addMultipleTasks(monthlyTaskLists);

                ArrayList<TaskDetails> dailyTaskLists = PersistenceManager.importDailyTaskLists(getApplicationContext());
                fragmentList.get(1).clearListView();
                fragmentList.get(1).addMultipleTasks(dailyTaskLists);

                break;
        }

        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.v(TAG, "onTabSelected(): " + tab.getPosition());
        currentTabIndex = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
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

                    fragmentList.get(currentTabIndex).addTask(newTask);
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

    private void showPickerDialog(final ReminderDetails reminderDetails) {
        Log.v(TAG, "showPickerDialog(): reminderDetails=" + reminderDetails);

        final View dialogPicker = getLayoutInflater().inflate(R.layout.dialog_picker, null);
        dialogPicker.setId(reminderDetails.getReminderType().getValue());

        int titleId;
        switch (reminderDetails.getReminderType()) {
            case REMINDER_TYPE_ONE:
                titleId = R.string.title_picker_one;
                break;

            case REMINDER_TYPE_TWO:
                titleId = R.string.title_picker_two;
                break;

            default:
                titleId = R.string.title_picker;
                break;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(titleId);
        builder.setIcon(R.drawable.ic_notification);
        builder.setView(dialogPicker);

        NumberPicker.Formatter twoDigitFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        };

        final NumberPicker pickerMonth = dialogPicker.findViewById(R.id.pickerDay);
        pickerMonth.setFormatter(twoDigitFormatter);
        pickerMonth.setMinValue(1);
        pickerMonth.setMaxValue(28);
        pickerMonth.setValue(reminderDetails.getDay());

        final NumberPicker pickerHour = dialogPicker.findViewById(R.id.pickerHour);
        pickerHour.setFormatter(twoDigitFormatter);
        pickerHour.setMinValue(0);
        pickerHour.setMaxValue(23);
        pickerHour.setValue(reminderDetails.getHour());

        final NumberPicker pickerMinute = dialogPicker.findViewById(R.id.pickerMinute);
        pickerMinute.setFormatter(twoDigitFormatter);
        pickerMinute.setMinValue(0);
        pickerMinute.setMaxValue(59);
        pickerMinute.setValue(reminderDetails.getMinute());

        final AlertDialog dialog = builder.show();

        Button submitButton = dialogPicker.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminderDetails.setDay(pickerMonth.getValue());
                reminderDetails.setHour(pickerHour.getValue());
                reminderDetails.setMinute(pickerMinute.getValue());

                // kick off JobService
                final JobInfo jobInfo = ReminderSchedulerUtil.getActivityJobInfo(reminderDetails, new ComponentName(view.getContext(), AlarmJobService.class));
                final JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    final int result = jobScheduler.schedule(jobInfo);
                    Log.v(TAG, "onClick(): Reminder-" + reminderDetails.getReminderType().getValue() + " : schedule.result=" + result);
                }

                // record change to reminder
                PersistenceManager.writeReminderList(view.getContext(), reminderList);

                Toast.makeText(view.getContext(), R.string.toastReminder, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }

    class TabPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        public TabPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return getString(tabNames[position]);
            return null;
        }
    }
}
