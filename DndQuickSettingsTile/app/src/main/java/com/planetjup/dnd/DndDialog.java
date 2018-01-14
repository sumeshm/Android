package com.planetjup.dnd;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This class will manage the Do-Not-Disturb dialog which seeks user input
 * <p>
 * Created by Sumesh Mani on 1/15/18.
 */

public class DndDialog extends Dialog implements DialogInterface.OnKeyListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = DndDialog.class.getSimpleName();

    private final Context context;
    private final RadioGroup radioGroupMode;
    private final RadioGroup radioGroupTime;
    private final TextView textViewSeek;
    private final SeekBar seekBar;

    public DndDialog(@NonNull Context context) {
        super(context, R.style.dnd_dialog);

        setContentView(R.layout.layout_dnd_dialog);
        setOnKeyListener(this);
        setTitle(R.string.app_name);

        this.context = context;
        radioGroupMode = this.getWindow().findViewById(R.id.radio_group_mode);
        radioGroupTime = this.getWindow().findViewById(R.id.radio_group_time);
        textViewSeek = this.getWindow().findViewById(R.id.textViewSeek);
        seekBar = this.getWindow().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();
        radioGroupTime.clearCheck();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        Log.v(TAG, "onKeyDown() : keyCode=" + keyCode + ", event=" + event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, @NonNull KeyEvent event) {
        Log.v(TAG, "onKeyLongPress() : keyCode=" + keyCode + ", event=" + event);
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void setOnKeyListener(@Nullable OnKeyListener onKeyListener) {
        Log.v(TAG, "onKeyLongPress() : onKeyListener=" + onKeyListener);
        super.setOnKeyListener(onKeyListener);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        Log.v(TAG, "onKeyDown() : keyCode=" + keyCode + ", event=" + event);
        return false;
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
