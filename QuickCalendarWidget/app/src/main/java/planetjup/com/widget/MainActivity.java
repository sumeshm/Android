package planetjup.com.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

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

        textViewSeek = findViewById(R.id.textViewSeek);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(0);
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

    public void onRadioButtonClick(View view) {
        Log.v(TAG, "onRadioButtonClick:" + view.toString());

        if (view instanceof AppCompatRadioButton) {
            AppCompatRadioButton radioButton = (AppCompatRadioButton) view;
            int color = getButtonColor(radioButton.getId());

            View grandParent = (View) view.getParent().getParent();

            switch (grandParent.getId()) {
                case R.id.bgRadioGroup:
                    Log.v(TAG, "onRadioButtonClick: grandParent=BG");
                    break;
                case R.id.dayRadioGroup:
                    Log.v(TAG, "onRadioButtonClick: grandParent=DAY");
                    break;
                case R.id.dateRadioGroup:
                    Log.v(TAG, "onRadioButtonClick: grandParent=DATE");
                    break;
                case R.id.eventRadioGroup:
                    Log.v(TAG, "onRadioButtonClick: grandParent=EVENT");
                    break;
                case R.id.todayRadioGroup:
                    Log.v(TAG, "onRadioButtonClick: grandParent=TODAY");
                    break;
            }
        }
    }

    private int getButtonColor(int viewId) {
        switch (viewId) {
            case R.id.bgRadioBlack:
                Log.v(TAG, "getButtonColor: BLACK");
                return Color.BLACK;
            case R.id.bgRadioWhite:
                Log.v(TAG, "getButtonColor: WHITE");
                return Color.WHITE;
            case R.id.bgRadioGrey:
                Log.v(TAG, "getButtonColor: DKGRAY");
                return Color.DKGRAY;
            case R.id.bgRadioRed:
                Log.v(TAG, "getButtonColor: RED");
                return Color.RED;
            case R.id.bgRadioGreen:
                Log.v(TAG, "getButtonColor: GREEN");
                return Color.GREEN;
            case R.id.bgRadioBlue:
                Log.v(TAG, "getButtonColor: BLUE");
                return Color.BLUE;
            default:
                Log.v(TAG, "getButtonColor: Default-DKGRAY");
                return Color.DKGRAY;
        }
    }
}
