package planetjup.com.dndquicktoggle;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class DriverActivity extends AppCompatActivity {

    private static final String TAG = "DriverActivity";
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 0;
    private static boolean isAllowed = false;
    private AudioManager audioMgr;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        if (!isAllowed)
        {
            getPermission();
        }

        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Button button15 = (Button) findViewById(R.id.button15);
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setInturptionMode(NotificationManager.INTERRUPTION_FILTER_NONE);
                //changeMode("button15", AudioManager.RINGER_MODE_SILENT);
            }
        });

        Button button30 = (Button) findViewById(R.id.button30);
        button30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInturptionMode(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                //changeMode("button30", AudioManager.RINGER_MODE_VIBRATE);
            }
        });

        Button button60 = (Button) findViewById(R.id.button60);
        button60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInturptionMode(NotificationManager.INTERRUPTION_FILTER_ALARMS);
                //changeMode("button60", AudioManager.RINGER_MODE_SILENT);
            }
        });

        Button buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInturptionMode(NotificationManager.INTERRUPTION_FILTER_NONE);
                //changeMode("buttonStop", AudioManager.RINGER_MODE_NORMAL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult :: requestCode=" + requestCode);

        if (requestCode == DriverActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE ) {
            Log.v(TAG, "onActivityResult: ON_DO_NOT_DISTURB_CALLBACK_CODE");
            isAllowed = true;
        }
    }

    protected void getPermission()
    {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivityForResult( intent, DriverActivity.ON_DO_NOT_DISTURB_CALLBACK_CODE );
        }
        else
        {
            isAllowed = true;
        }
    }

    protected void printAudioMode()
    {
        int ringerMode = audioMgr.getRingerMode();
        if (ringerMode == AudioManager.RINGER_MODE_VIBRATE)
        {
            Toast.makeText(DriverActivity.this, "Now in Vibrate Mode", Toast.LENGTH_LONG).show();
        }
        else if (ringerMode == AudioManager.RINGER_MODE_NORMAL)
        {
            Toast.makeText(DriverActivity.this,"Now in Ringing Mode", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(DriverActivity.this,"Now in Vibrate Mode", Toast.LENGTH_LONG).show();
        }
    }

    protected void changeMode(String msg, int mode)
    {
        Log.v(TAG, msg + " was clicked");
        if (isAllowed)
        {
            audioMgr.setRingerMode(mode);
            printAudioMode();
        }
    }

    protected void setInturptionMode(int mode)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(mode);
            printNotificationMode();
        }
    }

    protected void printNotificationMode()
    {
        int filterMode = notificationManager.getCurrentInterruptionFilter();
        if (filterMode == NotificationManager.INTERRUPTION_FILTER_ALL)
        {
            Toast.makeText(DriverActivity.this, "Complete Silence", Toast.LENGTH_LONG).show();
        }
        else if (filterMode == NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        {
            Toast.makeText(DriverActivity.this,"Priority Mode", Toast.LENGTH_LONG).show();
        }
        else if (filterMode == NotificationManager.INTERRUPTION_FILTER_ALARMS)
        {
            Toast.makeText(DriverActivity.this,"Alarms Mode", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(DriverActivity.this,"Normal Mode", Toast.LENGTH_LONG).show();
        }
    }
}
