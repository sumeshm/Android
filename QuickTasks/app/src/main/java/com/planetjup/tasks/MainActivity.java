package com.planetjup.tasks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import planetjup.com.tasks.R;
import com.planetjup.tasks.utils.TaskDetailsReaderWriter;
import com.planetjup.tasks.utils.TaskDetails;
import com.planetjup.tasks.utils.TaskDetailsArrayAdapter;

/**
 * This class will manage a quick tasks list.
 * <p>
 * Created by Sumesh Mani on 1/16/18.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int lastUpdateMonth = -1;

    private TaskDetailsArrayAdapter arrayAdapter;

    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        setContentView(R.layout.activity_main);
        populateListView();

        ImageButton addButton = findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");

        TaskDetailsReaderWriter.writeTasksList(this, arrayAdapter.getTasksList());
        TaskDetailsReaderWriter.writeTasksRefreshDate(this, lastUpdateMonth);
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick() : view.id=" + view.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_popup);

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTask = input.getText().toString();
                arrayAdapter.add(new TaskDetails(newTask, false));
                arrayAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sendNotification(System.currentTimeMillis());
            }
        });

        builder.show();
    }


    private void populateListView() {
        Log.v(TAG, "populateListView()");

        ArrayList<TaskDetails> tasksList = null;

        if (isResetNeeded())
        {
            tasksList = new ArrayList<>();
        }
        else
        {
            tasksList = TaskDetailsReaderWriter.readTasksList(this);
        }

        arrayAdapter = new TaskDetailsArrayAdapter(this, R.layout.text_view, tasksList);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
    }

    private void sendNotification(long delay) {
        Log.v(TAG, "sendNotification() : delay=" + new Date(delay));

        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel channel = new NotificationChannel(getPackageName(),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, getPackageName())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(getString(R.string.msg_notification) + ", for date=" + new Date(delay).toString())
                .setColor(getColor(R.color.colorOrange))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(delay);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private boolean isResetNeeded()
    {
        Log.v(TAG, "isResetNeeded()");

        Calendar currCalendar = Calendar.getInstance();
        int currentYear = currCalendar.get(Calendar.YEAR);
        int currentMonth = currCalendar.get(Calendar.MONTH);
        int currentDate = currCalendar.get(Calendar.DAY_OF_MONTH);
        Log.v(TAG, "isResetNeeded() : currentMonth/currendtDate=" + currentMonth + "/" + currentDate);


        if (currentDate > 18)
        {
            currentMonth++;
        }
        if (currentMonth == Calendar.DECEMBER)
        {
            currentYear++;
        }

        lastUpdateMonth = TaskDetailsReaderWriter.readTasksRefreshDate(this);
        Log.v(TAG, "isResetNeeded() : lastUpdateMonth=" + lastUpdateMonth);
        if (lastUpdateMonth < currentMonth)
        {
            lastUpdateMonth = currentMonth;
            Calendar futureCalendar = new Calendar.Builder().
                    set(Calendar.YEAR, currentYear).
                    set(Calendar.MONTH, currentMonth).
                    set(Calendar.HOUR, 11).
                    set(Calendar.DATE, 18).
                    build();

            sendNotification(futureCalendar.getTime().getTime());
        }

        return Boolean.FALSE;
    }
}

