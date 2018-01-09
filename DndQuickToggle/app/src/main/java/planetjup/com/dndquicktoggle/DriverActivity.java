package planetjup.com.dndquicktoggle;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DriverActivity extends AppCompatActivity {

    private static final String TAG = DriverActivity.class.getSimpleName();
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 0;
    private static boolean isAllowed = false;

    private AudioManager audioMgr;
    private NotificationManager notificationManager;
    private CountDownTimer countDownTimer;

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
                handle15();
            }
        });

        Button button30 = (Button) findViewById(R.id.button30);
        button30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handle30();
            }
        });

        Button button60 = (Button) findViewById(R.id.button60);
        button60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handle60();
            }
        });

        Button buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMode("buttonStop", AudioManager.RINGER_MODE_NORMAL);
            }
        });

        Button buttonClose = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitApp();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {

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
    }

    protected void changeMode(String msg, int newRingerMode)
    {
        Log.v(TAG, msg + " was clicked");
        if (isAllowed)
        {
            int ringerMode = audioMgr.getRingerMode();
            if (ringerMode != newRingerMode)
            {
                audioMgr.setRingerMode(newRingerMode);
            }

            printAudioMode();
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

    protected void startCountdonwTimer(int duration)
    {
        countDownTimer = new CountDownTimer(duration, duration) {
            @Override
            public void onTick(long l) {

            }

            public void onFinish() {
                changeMode("buttonStop", AudioManager.RINGER_MODE_NORMAL);
            }
        }.start();
    }

    protected void exitApp()
    {
        // todo : ßadd other cleanup here
        finish();
    }

    protected void handle15()
    {
        changeMode("button15", AudioManager.RINGER_MODE_SILENT);
        startCountdonwTimer(15 * 60 * 60);
        exitApp();
    }

    protected void handle30()
    {
        changeMode("button15", AudioManager.RINGER_MODE_SILENT);
        startCountdonwTimer(30 * 60 * 60);
        exitApp();
    }

    protected void handle60()
    {
        changeMode("button15", AudioManager.RINGER_MODE_SILENT);
        startCountdonwTimer(60 * 60 * 60);
        exitApp();
    }
}
