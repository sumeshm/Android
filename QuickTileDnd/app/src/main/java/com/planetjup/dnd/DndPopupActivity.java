package com.planetjup.dnd;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private Button modeTotal;
    private Button modePriority;
    private Button modeAlarm;

    private TextView textViewSeek;
    private SeekBar seekBar;
    private int interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_dnd_popup);

        modeTotal = this.getWindow().findViewById(R.id.buttonModeTotal);
        modePriority = this.getWindow().findViewById(R.id.buttonModePriority);
        modeAlarm = this.getWindow().findViewById(R.id.buttonModeAlarm);

        textViewSeek = this.getWindow().findViewById(R.id.textViewSeek);
        seekBar = this.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
        interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(View) : view_id=" + view.getId());
        Log.v(TAG, "onClick(View) : interruptionMode=" + interruptionMode);

        Intent dndIntent = new Intent(this, DndTileService.class);
        dndIntent.putExtra(DndTileService.KEY_INTERRUPTION_FILTER, interruptionMode);

        switch (view.getId()) {
            case R.id.buttonTime_15:
                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, 15);
                break;

            case R.id.buttonTime_30:
                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, 30);
                break;

            case R.id.buttonTime_60:
                dndIntent.setAction(DndTileService.ACTION_START_TIMER);
                dndIntent.putExtra(DndTileService.KEY_DND_DURATION, 60);
                break;

            case R.id.buttonTime_Infinity:
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
                dndIntent.putExtra(DndTileService.KEY_INTERRUPTION_FILTER, interruptionMode);
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

    public void onButtonClick(View view) {
        Log.v(TAG, "onButtonClick(View) : view_id=" + view.getId());

        switch (view.getId()) {
            case R.id.buttonModeTotal:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_NONE;
                break;
            case R.id.buttonModePriority:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_PRIORITY;
                break;
            case R.id.buttonModeAlarm:
                interruptionMode = NotificationManager.INTERRUPTION_FILTER_ALARMS;
                break;
        }

        setModeButton();
    }

    private void setModeButton() {
        Log.v(TAG, "setModeButton() : interruptionMode=" + interruptionMode);

        switch (interruptionMode) {
            case NotificationManager.INTERRUPTION_FILTER_NONE:
                modeTotal.setBackground(getDrawable(R.drawable.gradient_clicked));

                modePriority.setBackground(getDrawable(R.drawable.gradient));
                modeAlarm.setBackground(getDrawable(R.drawable.gradient));
                break;
            case NotificationManager.INTERRUPTION_FILTER_PRIORITY:
                modePriority.setBackground(getDrawable(R.drawable.gradient_clicked));

                modeTotal.setBackground(getDrawable(R.drawable.gradient));
                modeAlarm.setBackground(getDrawable(R.drawable.gradient));
                break;
            case NotificationManager.INTERRUPTION_FILTER_ALARMS:
                modeAlarm.setBackground(getDrawable(R.drawable.gradient_clicked));

                modeTotal.setBackground(getDrawable(R.drawable.gradient));
                modePriority.setBackground(getDrawable(R.drawable.gradient));
                break;
        }
    }

    private int getSeekProgress() {
        Log.v(TAG, "getSeekProgress() : progress=" + seekBar.getProgress());
        return seekBar.getProgress();
    }
}
