package com.planetjup.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class CustomRadioGroup extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = CustomRadioGroup.class.getSimpleName();

    // View elements
    private final RadioGroup radioGroup;
    private TextView textViewTitle;

    // metadata
    private final Map<Integer, Integer> radioButtonMap = new HashMap<>();
    private final Map<Integer, RadioButton> colorMap = new HashMap<>();
    private OnButtonClickedListener listener;
    private String listenerId;

    public CustomRadioGroup(Context context, String titleText, Map<String, Integer> colorsMap) {
        super(context);
        Log.v(TAG, "CustomRadioGroup(): titleText=" + titleText);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_radio_group, this, true);

        // fetch child View objects
        textViewTitle = findViewById(R.id.customTitle);
        radioGroup = findViewById(R.id.customRadioGroupNew);
        Log.v(TAG, "CustomRadioGroup(): radioGroup=" + radioGroup.getChildCount());
        Log.v(TAG, "CustomRadioGroup(): radioGroup=" + radioGroup.getChildAt(0));


        createRadioButtons(context, colorsMap);
    }

    public void setUp(String customTitle, String listenerId, OnButtonClickedListener listener, int selectedColor) {
        Log.v(TAG, "setCustomTitle(): customTitle=" + customTitle + ", listenerId=" + listenerId + ", selectedColor=" + selectedColor);
        textViewTitle = findViewById(R.id.customTitle);
        textViewTitle.setText(customTitle);
        this.listener = listener;
        this.listenerId = listenerId;
        colorMap.get(selectedColor).setChecked(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.v(TAG, "onCheckedChanged(): " + listenerId + ", isChecked=" + isChecked + ", buttonView=" + buttonView.toString());

        int buttonId = buttonView.getId();

        GradientDrawable shape = new GradientDrawable();
        shape.setStroke(1, Color.BLACK);

        if (isChecked) {
            Log.v(TAG, "onCheckedChanged(): selectedButtonId=" + buttonId + ", color=" + radioButtonMap.get(buttonId));

            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(radioButtonMap.get(buttonView.getId()));
            buttonView.setBackground(shape);

            if (listener != null) {
                listener.radioButtonClicked(listenerId, radioButtonMap.get(buttonId));
            }
        } else {
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(radioButtonMap.get(buttonId));
            buttonView.setBackground(shape);
        }
    }

    private void createRadioButtons(Context context, Map<String, Integer> colorsMap) {
        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 5, 15, 5);

        // checked, un-checked
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] {android.R.attr.state_checked}, new int[] {-android.R.attr.state_checked}
                },
                new int[] { Color.parseColor("#2e7d32"), Color.TRANSPARENT}
        );

        for (String colorName : colorsMap.keySet()) {
            int color = colorsMap.get(colorName);

            GradientDrawable roundShape = new GradientDrawable();
            roundShape.setShape(GradientDrawable.OVAL);
            roundShape.setStroke(1, Color.BLACK);
            roundShape.setColor(color);

            RadioButton radioButton = new RadioButton(context);
            radioButton.setBackground(roundShape);
            radioButton.setLayoutParams(new LayoutParams(100, 100));
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setButtonTintList(colorStateList);
            radioButton.setOnCheckedChangeListener(this);

            radioGroup.addView(radioButton, layoutParams);
            radioButtonMap.put(radioButton.getId(), color);
            colorMap.put(color, radioButton);
        }

        // todo: set selected
    }

    public interface OnButtonClickedListener {
        void radioButtonClicked(String listenerId, @ColorInt int color);
    }

}
