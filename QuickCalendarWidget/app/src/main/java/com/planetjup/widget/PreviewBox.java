package com.planetjup.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.planetjup.widget.util.Constants;

public class PreviewBox extends LinearLayout {

    private static final String TAG = PreviewBox.class.getSimpleName();

    private final TextView previewDay;
    private final TextView previewDate;
    private final TextView previewEvent;
    private final Button previewSaveButton;
    private final LinearLayout previewBox;
    private final GradientDrawable shape = new GradientDrawable();

    public PreviewBox(Context context, OnClickListener listener) {
        super(context);
        Log.v(TAG, "PreviewBox():");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preview_box, this, true);

        // fetch child View objects
        previewDay = findViewById(R.id.previewDay);
        previewDate = findViewById(R.id.previewDate);
        previewEvent = findViewById(R.id.previewEvent);

        previewSaveButton = findViewById(R.id.previewSaveButton);
        previewSaveButton.setOnClickListener(listener);

        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(20f);
        shape.setColor(Color.TRANSPARENT);
        shape.setStroke(1, Color.BLACK);

        previewBox = findViewById(R.id.previewBox);
        previewBox.setBackground(shape);
    }

    // update alpha
    public void updateBackground(int alpha, int bgColor, int dayColor, int dateColor, int eventColor) {
        int effectiveAlpha = 0;
        if (alpha != 0) {
            effectiveAlpha = (255 * alpha) / Constants.KEY_SEEK_BAR_MAX;
        }

        shape.setAlpha(effectiveAlpha);
        shape.setColor(bgColor);

        previewDay.setTextColor(dayColor);
        previewDate.setTextColor(dateColor);
        previewEvent.setTextColor(eventColor);
    }
}
