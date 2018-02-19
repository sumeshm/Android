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

import java.util.ArrayList;

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

        TaskDetailsReaderWriter.setStringArrayPref(this, arrayAdapter.getTasksList());
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
                sendNotification();
            }
        });

        builder.show();
    }


    private void populateListView() {
        Log.v(TAG, "populateListView()");

//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd");
//        String currDate = sdf.format(calendar.getTime());

        ArrayList<TaskDetails> tasksList = TaskDetailsReaderWriter.getStringArrayPref(this);

        arrayAdapter = new TaskDetailsArrayAdapter(this, R.layout.text_view, tasksList);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
    }

    private void sendNotification() {
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
                .setContentText(getString(R.string.msg_notification))
                .setColor(getColor(R.color.colorOrange))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());

        notificationManager.notify(0, notificationBuilder.build());
    }
}

