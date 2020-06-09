package com.planetjup.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
    private final Map<Integer, Integer> radioButtonByIdMap = new HashMap<>();
    private final Map<Integer, RadioButton> radioButtonByColorMap = new HashMap<>();
    private OnButtonClickedListener listener;
    private String radioGroupId;

    public CustomRadioGroup(Context context, String titleText, int[] colorsList) {
        super(context);
        Log.v(TAG, "CustomRadioGroup(): titleText=" + titleText);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_radio_group, this, true);

        // fetch child View objects
        textViewTitle = findViewById(R.id.customTitle);
        radioGroup = findViewById(R.id.customRadioGroup);

        createRadioButtons(context, colorsList);
        Log.v(TAG, "CustomRadioGroup(): radioGroup.ChildCount=" + radioGroup.getChildCount());
    }

    public void setUp(String customTitle, String radioGroupId, OnButtonClickedListener listener, int selectedColor) {
        Log.v(TAG, "setCustomTitle(): customTitle=" + customTitle + ", radioGroupId=" + radioGroupId + ", selectedColor=" + selectedColor);
        textViewTitle = findViewById(R.id.customTitle);
        textViewTitle.setText(customTitle);
        this.listener = listener;
        this.radioGroupId = radioGroupId;

        if (radioButtonByColorMap.get(selectedColor) != null) {
            radioButtonByColorMap.get(selectedColor).setChecked(true);
        } else {
            radioButtonByColorMap.get(Color.GRAY).setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.v(TAG, "onCheckedChanged(): radioGroupId=" + radioGroupId + ", isChecked=" + isChecked + ", buttonView=" + buttonView.toString());

        int buttonId = buttonView.getId();

        GradientDrawable roundShape = new GradientDrawable();
        roundShape.setShape(GradientDrawable.OVAL);

        if (isChecked) {
            Log.v(TAG, "onCheckedChanged(): selectedButtonId=" + buttonId + ", color=" + radioButtonByIdMap.get(buttonId));

            roundShape.setColor(radioButtonByIdMap.get(buttonView.getId()));
            roundShape.setStroke(10, Color.BLACK);
            roundShape.setSize(150, 150);
            buttonView.setBackground(roundShape);

            if (listener != null) {
                listener.radioButtonClicked(radioGroupId, radioButtonByIdMap.get(buttonId));
            }
        } else {
            roundShape.setColor(radioButtonByIdMap.get(buttonId));
            roundShape.setStroke(1, Color.BLACK);
            buttonView.setBackground(roundShape);
        }
    }

    private void createRadioButtons(Context context, int[] colorsList) {
        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 5, 15, 5);

        // checked, un-checked
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] {android.R.attr.state_checked}, new int[] {-android.R.attr.state_checked}
                },
                new int[] { Color.TRANSPARENT, Color.TRANSPARENT}
        );

        for (int i = 0; i < colorsList.length; i++) {
            int color = colorsList[i];
            Log.v(TAG, "createRadioButtons(): color=" + color);

            GradientDrawable roundShape = new GradientDrawable();
            roundShape.setShape(GradientDrawable.OVAL);
            roundShape.setStroke(2, Color.BLACK);
            roundShape.setColor(color);

            RadioButton radioButton = new RadioButton(context);
            radioButton.setBackground(roundShape);
            radioButton.setButtonTintList(colorStateList);
            radioButton.setOnCheckedChangeListener(this);

            radioGroup.addView(radioButton, layoutParams);
            radioButtonByIdMap.put(radioButton.getId(), color);
            radioButtonByColorMap.put(color, radioButton);
        }
    }

    public interface OnButtonClickedListener {
        void radioButtonClicked(String radioGroupId, @ColorInt int color);
    }

}
