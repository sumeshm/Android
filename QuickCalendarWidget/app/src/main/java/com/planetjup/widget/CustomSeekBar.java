package com.planetjup.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.planetjup.widget.util.Constants;

public class CustomSeekBar extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = CustomSeekBar.class.getSimpleName();

    private static final String KEY_SEEK_BAR_PROGRESS = "PROGRESS";

    private final TextView textViewTitle;
    private final SeekBar seekBar;
    private final String titleTextTemplate;
    private int currProgress;
    private OnProgressChangedListener listener;

    public CustomSeekBar(Context context, int progress, OnProgressChangedListener listener) {
        super(context);
        Log.v(TAG, "CustomSeekBar(): progress=" + progress);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_seek_bar, this, true);

        titleTextTemplate = context.getResources().getString(R.string.seek_bar_title);
        this.currProgress = progress;
        this.listener = listener;

        // fetch child View objects
        textViewTitle = findViewById(R.id.seekBarTitle);
        String titleText = titleTextTemplate.replace(KEY_SEEK_BAR_PROGRESS, Integer.toString(progress * Constants.KEY_SEEK_BAR_MAX));
        textViewTitle.setText(titleText);

        // update seek-bar
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(progress);
        seekBar.setMax(Constants.KEY_SEEK_BAR_MAX);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.currProgress = seekBar.getProgress();
        Log.v(TAG, "onProgressChanged(): currProgress=" + currProgress);

        int factor = 100 / Constants.KEY_SEEK_BAR_MAX;
        String titleText = titleTextTemplate.replace(KEY_SEEK_BAR_PROGRESS, Integer.toString(currProgress * factor));
        textViewTitle.setText(titleText);

        if (listener != null) {
            listener.progressChanged(currProgress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public interface OnProgressChangedListener {
        void progressChanged(int progress);
    }
}
