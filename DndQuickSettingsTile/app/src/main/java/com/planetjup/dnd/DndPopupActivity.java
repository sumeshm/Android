package com.planetjup.dnd;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity will be the dialog being popped up by Dnd-Tile-Service
 * <p>
 * Created by Sumesh Mani on 1/16/18.
 */

public class DndPopupActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String TAG = DndPopupActivity.class.getSimpleName();

    private RadioGroup radioGroupMode;
    private TextView textViewSeek;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_dnd_popup);

        radioGroupMode = this.getWindow().findViewById(R.id.radio_group_mode);
        radioGroupMode.getChildAt(0).setSelected(true);
        textViewSeek = this.getWindow().findViewById(R.id.textViewSeek);
        seekBar = this.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(View) : view_id=" + view.getId());

        Intent dndIntent = new Intent(this, DndTileService.class);
        dndIntent.putExtra(DndTileService.KEY_INTERRUPTION_FILTER, getInterruptionMode());

        switch (view.getId()) {
            case R.id.radio_15:
                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, 15);
                break;

            case R.id.radio_30:
                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, 30);
                break;

            case R.id.radio_60:
                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, 60);
                break;

            case R.id.radio_infinity:
                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, 0);
                break;

            case R.id.buttonGo:
                if (getSeekProgress() <= 0) {
                    Toast.makeText(this, "Use the seek bat to set timeout", Toast.LENGTH_SHORT).show();
                    return;
                }

                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, getSeekProgress());
                dndIntent.putExtra(DndTileService.KEY_INTERRUPTION_FILTER, getInterruptionMode());
                break;

            case R.id.buttonMusic:
                dndIntent.setAction(DndTileService.ACTION_MUTE_MUSIC);
                break;

            case R.id.buttonAlarm:
                dndIntent.setAction(DndTileService.ACTION_MUTE_ALARM);
                break;

            case R.id.buttonRinger:
                dndIntent.setAction(DndTileService.ACTION_MUTE_RINGER);
                break;
        }

        startService(dndIntent);
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //Log.v(TAG, "onProgressChanged() : progress=" + progress);

        String data = Integer.toString(progress).concat(getString(R.string.Min));
        textViewSeek.setText(data);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    protected int getInterruptionMode() {
        int interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;

        switch (radioGroupMode.getCheckedRadioButtonId()) {
            case R.id.radio_total:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;
                break;

            case R.id.radio_priority:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_PRIORITY;
                break;

            case R.id.radio_alarm:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_ALARMS;
                break;
        }

        Log.v(TAG, "getInterruptionMode() : interruptionMode=" + interruptionMode);
        return interruptionMode;
    }

    protected int getSeekProgress() {
        Log.v(TAG, "getSeekProgress() : progress=" + seekBar.getProgress());
        return seekBar.getProgress();
    }
}
