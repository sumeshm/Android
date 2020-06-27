package com.planetjup.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.planetjup.widget.util.Constants;
import com.planetjup.widget.util.IUserActionListener;
import com.planetjup.widget.util.PersistenceManager;

import java.util.Map;

public class PreviewBox extends LinearLayout implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = PreviewBox.class.getSimpleName();

    private final CheckBox previewCheckBox;
    private final TextView previewClock;
    private final TextView previewDay;
    private final TextView previewDate;
    private final TextView previewEvent;
    private final Button previewSaveButton;
    private final LinearLayout previewBox;
    private final GradientDrawable shape = new GradientDrawable();
    private final IUserActionListener listener;

    public PreviewBox(Context context, IUserActionListener listener) {
        super(context);
        Log.v(TAG, "PreviewBox():");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preview_box, this, true);

        this.listener = listener;

        // fetch child View objects
        previewCheckBox = findViewById(R.id.previewCheckBox);
        previewCheckBox.setOnCheckedChangeListener(this);

        previewClock = findViewById(R.id.previewClock);
        previewDay = findViewById(R.id.previewDay);
        previewDate = findViewById(R.id.previewDate);
        previewEvent = findViewById(R.id.previewEvent);

        previewSaveButton = findViewById(R.id.previewSaveButton);
        previewSaveButton.setOnClickListener(this);

        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(20f);
        shape.setColor(Color.TRANSPARENT);
        shape.setStroke(1, Color.BLACK);

        previewBox = findViewById(R.id.previewBox);
        previewBox.setBackground(shape);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.v(TAG, "onCheckedChanged(): isChecked=" + isChecked);
        setClockVisibility(isChecked);

        listener.checkBoxClicked(Constants.KEY_CLOCK_CHECKED, isChecked);
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(): view=" + view);
        listener.saveButtonClicked();
    }

    // update alpha
    public void updatePreview(Map<String, Integer> settingsMap) {
        Log.v(TAG, "updatePreview():");

        int effectiveAlpha = 0;
        if (settingsMap.get(Constants.KEY_ALPHA) != 0) {
            effectiveAlpha = (255 * settingsMap.get(Constants.KEY_ALPHA)) / Constants.KEY_SEEK_BAR_MAX;
        }

        shape.setAlpha(effectiveAlpha);
        shape.setColor(settingsMap.get(Constants.KEY_BG_COLOR));

        previewClock.setTextColor(settingsMap.get(Constants.KEY_CLOCK_COLOR));
        previewDay.setTextColor(settingsMap.get(Constants.KEY_DAY_COLOR));
        previewDate.setTextColor(settingsMap.get(Constants.KEY_DATE_COLOR));
        previewEvent.setTextColor(settingsMap.get(Constants.KEY_EVENT_COLOR));

        setClockVisibility(settingsMap.get(Constants.KEY_CLOCK_CHECKED) == 0 ? Boolean.FALSE : Boolean.TRUE);
    }

    public void setClockVisibility(Boolean isChecked) {
        previewCheckBox.setChecked(isChecked);
        previewClock.setVisibility(isChecked ? VISIBLE : INVISIBLE);
    }
}
