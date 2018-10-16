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

import com.planetjup.tasks.reminder.ReminderBroadcastReceiver;
import com.planetjup.tasks.utils.TaskDetails;
import com.planetjup.tasks.utils.TaskDetailsArrayAdapter;
import com.planetjup.tasks.utils.TaskDetailsReaderWriter;

import java.util.ArrayList;

import planetjup.com.tasks.R;


/**
 * This class will manage a quick tasks list.
 * <p>
 * Created by Sumesh Mani on 1/16/18.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TaskDetailsArrayAdapter arrayAdapter;
    private int reminderDay = 15;
    private int reminderHour = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        // trigger a reminder every month (on 18th day at 11 am) about pending tasks
        startAlarmBroadcast();

        setContentView(R.layout.activity_main);
        populateListView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");

        TaskDetailsReaderWriter.writeTasksList(this, arrayAdapter.getTasksList());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");

        TaskDetailsReaderWriter.writeTasksList(this, arrayAdapter.getTasksList());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");

        TaskDetailsReaderWriter.writeTasksList(this, arrayAdapter.getTasksList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menuAdd:
                showAddDialog();
                break;

            case R.id.menuReset:
                arrayAdapter.resetListView();
                break;

            case R.id.menuImport:
                Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuExport:
                Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuReminder:
                showPickerDialog();
                break;
        }

        return true;
    }


    private void populateListView() {
        Log.v(TAG, "populateListView()");

        ArrayList<TaskDetails> tasksList = TaskDetailsReaderWriter.readTasksList(this);

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

    private void showPickerDialog() {
        Log.v(TAG, "showPickerDialog()");

        final View dialogPicker = getLayoutInflater().inflate(R.layout.dialog_picker, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.title_picker);
        builder.setIcon(R.drawable.ic_notification);
        builder.setView(dialogPicker);

        final NumberPicker pickerDayOfMonth = dialogPicker.findViewById(R.id.pickerDayOfMonth);
        pickerDayOfMonth.setMinValue(1);
        pickerDayOfMonth.setMaxValue(28);
        pickerDayOfMonth.setValue(reminderDay);

        final NumberPicker pickerHourOfDay = dialogPicker.findViewById(R.id.pickerHourOfDay);
        pickerHourOfDay.setMinValue(0);
        pickerHourOfDay.setMaxValue(23);
        pickerHourOfDay.setValue(reminderHour);

        final AlertDialog dialog = builder.show();

        Button submitButton = dialogPicker.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminderDay = pickerDayOfMonth.getValue();
                reminderHour = pickerHourOfDay.getValue();
                dialog.cancel();
                startAlarmBroadcast();
            }
        });
    }

    private void startAlarmBroadcast() {
        Log.v(TAG, "startAlarmBroadcast()");
        Log.v(TAG, "startAlarmBroadcast(): HOUR=" + reminderHour);
        Log.v(TAG, "startAlarmBroadcast():  DAY=" + reminderDay);
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        intent.setAction(ReminderBroadcastReceiver.ACTION_START_ALARM);
        intent.putExtra(ReminderBroadcastReceiver.EXTRA_DAY, reminderDay);
        intent.putExtra(ReminderBroadcastReceiver.EXTRA_HOUR, reminderHour);
        sendBroadcast(intent);
    }
}

