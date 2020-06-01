package planetjup.com.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ON_CALENDAR_PERMISSION_CALLBACK_CODE = 12345;
    private static final int ON_ALARM_CALLBACK_CODE = 12346;
    private static final int ON_ALPHA_REFRESH_CODE = 12347;

    private TextView textViewSeek;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getContactsPermission();
        startAlarm();

        setContentView(R.layout.activity_main);

        textViewSeek = this.getWindow().findViewById(R.id.textViewSeek);
        seekBar = this.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(0);

        //finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult :: requestCode=" + requestCode);

        if (requestCode == ON_CALENDAR_PERMISSION_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_CONTACTS_PERMISSION_CALLBACK_CODE");
        }
    }

    private void getContactsPermission() {
        Log.v(TAG, "getContactsPermission()");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "getContactsPermission : READ_CALENDAR=FALSE");

            String[] permissionList = {Manifest.permission.READ_CALENDAR};
            requestPermissions(permissionList, ON_CALENDAR_PERMISSION_CALLBACK_CODE);

        } else {
            Log.v(TAG, "getContactsPermission : READ_CALENDAR=TRUE");
        }
    }

    // set alarm for 12:00 am
    private void startAlarm() {
        Log.v(TAG, "startAlarm()");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 01);
        calendar.set(Calendar.SECOND, 00);

        // designate alarm handler
        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(CalendarWidget.ACTION_UI_REFRESH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), ON_ALARM_CALLBACK_CODE, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String data = Integer.toString(progress * 10);
        textViewSeek.setText(data);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        String seekText = textViewSeek.getText().toString();
        Log.v(TAG, "onStopTrackingTouch: text=" + seekText);

        // notify widget
        Intent intent = new Intent(this, CalendarWidget.class);
        intent.setAction(CalendarWidget.ACTION_ALPHA_REFRESH);
        intent.putExtra(CalendarWidget.KEY_ALPHA, Integer.valueOf(seekText));
        sendBroadcast(intent);
    }
}
