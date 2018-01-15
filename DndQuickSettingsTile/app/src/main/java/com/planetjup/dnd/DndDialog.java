package com.planetjup.dnd;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This class will manage the Do-Not-Disturb dialog which seeks user input
 * <p>
 * Created by Sumesh Mani on 1/15/18.
 */

public class DndDialog extends Dialog implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = DndDialog.class.getSimpleName();

    private final Context context;
    private final RadioGroup radioGroupMode;
    private final RadioGroup radioGroupTime;
    private final TextView textViewSeek;
    private final SeekBar seekBar;

    public DndDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.layout_dnd_dialog);
        setTitle(R.string.app_name);

        this.context = context;
        radioGroupMode = this.getWindow().findViewById(R.id.radio_group_mode);
        radioGroupTime = this.getWindow().findViewById(R.id.radio_group_time);
        textViewSeek = this.getWindow().findViewById(R.id.textViewSeek);
        seekBar = this.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void show() {
        super.show();
        Log.v(TAG, "show()");
        radioGroupTime.clearCheck();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textViewSeek.setText(progress + " " + context.getString(R.string.Min));
        Log.v(TAG, "onProgressChanged() : progress=" + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public int getInterruptionMode() {
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

    public int getSeekProgress() {
        Log.v(TAG, "getSeekProgress() : progress=" + seekBar.getProgress());
        return seekBar.getProgress();
    }
}
