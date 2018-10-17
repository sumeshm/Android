package com.planetjup.tasks;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.planetjup.tasks.reminder.AlarmManager;
import com.planetjup.tasks.utils.PersistenceManager;
import com.planetjup.tasks.utils.ReminderDetails;
import com.planetjup.tasks.utils.TaskDetails;
import com.planetjup.tasks.utils.TaskDetailsArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import planetjup.com.tasks.R;


/**
 * This class will manage a quick tasks list.
 * <p>
 * Created by Sumesh Mani on 1/16/18.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TaskDetailsArrayAdapter arrayAdapter;
    private List<ReminderDetails> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        reminderList = PersistenceManager.readReminderList(this);
        for (ReminderDetails details : reminderList) {
            startAlarmBroadcast(details);
        }

        setContentView(R.layout.activity_main);
        populateListView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");

        PersistenceManager.writeTasksList(this, arrayAdapter.getTasksList());
        PersistenceManager.writeReminderList(this, reminderList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");

        PersistenceManager.writeTasksList(this, arrayAdapter.getTasksList());
        PersistenceManager.writeReminderList(this, reminderList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");

        PersistenceManager.writeTasksList(this, arrayAdapter.getTasksList());
        PersistenceManager.writeReminderList(this, reminderList);
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
                arrayAdapter.resetListView();
                break;

            case R.id.menuReminderOne:
                showPickerDialog(reminderList.get(0));
                break;

            case R.id.menuReminderTwo:
                showPickerDialog(reminderList.get(1));
                break;

            case R.id.menuImport:
                Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuExport:
                Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }


    private void populateListView() {
        Log.v(TAG, "populateListView()");

        ArrayList<TaskDetails> tasksList = PersistenceManager.readTasksList(this);

        arrayAdapter = new TaskDetailsArrayAdapter(this, R.layout.text_view, tasksList);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
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
                    arrayAdapter.add(new TaskDetails(newTask, Boolean.FALSE));
                    arrayAdapter.notifyDataSetChanged();
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

        final NumberPicker pickerMonth = dialogPicker.findViewById(R.id.pickerDay);
        pickerMonth.setMinValue(1);
        pickerMonth.setMaxValue(28);
        pickerMonth.setValue(reminderDetails.getDay());

        final NumberPicker pickerHour = dialogPicker.findViewById(R.id.pickerHour);
        pickerHour.setMinValue(0);
        pickerHour.setMaxValue(23);
        pickerHour.setValue(reminderDetails.getHour());

        final NumberPicker pickerMinute = dialogPicker.findViewById(R.id.pickerMinute);
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

                startAlarmBroadcast(reminderDetails);
                PersistenceManager.writeReminderList(view.getContext(), reminderList);

                dialog.cancel();
            }
        });
    }

    private void startAlarmBroadcast(ReminderDetails details) {
        Log.v(TAG, "startAlarmBroadcast(): TYPE=" + details.getReminderType());
        Log.v(TAG, "startAlarmBroadcast():  DAY=" + details.getDay());
        Log.v(TAG, "startAlarmBroadcast(): HOUR=" + details.getHour());
        Log.v(TAG, "startAlarmBroadcast():  MIN=" + details.getMinute());

        Intent intent = new Intent(this, AlarmManager.class);
        intent.setAction(AlarmManager.ACTION_START_ALARM);
        intent.putExtra(AlarmManager.EXTRA_REMINDER_TYPE, details.getReminderType().getValue());
        intent.putExtra(AlarmManager.EXTRA_REMINDER_DAY, details.getDay());
        intent.putExtra(AlarmManager.EXTRA_REMINDER_HOUR, details.getHour());
        intent.putExtra(AlarmManager.EXTRA_REMINDER_MINUTE, details.getMinute());
        sendBroadcast(intent);
    }
}

