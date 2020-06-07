package com.planetjup.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.planetjup.widget.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class CustomRadioGroup extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = CustomRadioGroup.class.getSimpleName();
    private Map<Integer, RadioButton> radioButtonMap = new HashMap<>();
    private GradientDrawable roundShape = new GradientDrawable();
    private String radioGroupTitle;
    private @ColorInt int color = Color.DKGRAY;
    private String selectedColor = "";
    private RadioGroup customRadioGroup;
    private TextView customTitle;

    public CustomRadioGroup(Context context, AttributeSet attrSet) {
        super(context, attrSet);

        Log.v(TAG, "CustomRadioGroup: attrSet.size=" + attrSet.getAttributeCount());
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CustomRadioGroup, 0, 0);

        try {
            radioGroupTitle = typedArray.getString(R.styleable.CustomRadioGroup_radioGroupTitle);
            Log.v(TAG, "CustomRadioGroup: radioGroupTitle=" + radioGroupTitle);
        } finally {
            typedArray.recycle();
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_radio_group, this, true);

        customTitle = findViewById(R.id.customTitle);
        customTitle.setText(radioGroupTitle);

        GradientDrawable roundShape = new GradientDrawable();
        roundShape.setShape(GradientDrawable.OVAL);
        roundShape.setStroke(1, Color.BLACK);
        roundShape.setAlpha(100);
        roundShape.setTint(Color.GREEN);

        customRadioGroup = findViewById(R.id.customRadioGroup);
        for (int i = 0; i < customRadioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) customRadioGroup.getChildAt(i);
            //radioButton.setBackground(roundShape);
            //setButtonColor(radioButton);
            radioButton.setOnCheckedChangeListener(this);

            radioButtonMap.put(radioButton.getId(), radioButton);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.v(TAG, "onCheckedChanged(): " + radioGroupTitle + ", isChecked=" + isChecked + ", buttonView=" + buttonView.toString());
        if (isChecked) {
            updateSelectedColor(buttonView.getId());
        }
    }

    public @ColorInt int getSelectedColor() {
        Log.v(TAG, "getSelectedColor(): " + radioGroupTitle + ", Color=" + selectedColor);
        return color;
    }

    public void setSelectedColor(@ColorInt int newColor) {
        Log.v(TAG, "setSelectedColor(): " + radioGroupTitle + ", newColor=" + newColor);

        switch (newColor) {
            case Color.BLACK:
                radioButtonMap.get(R.id.radioButtonBlack).setChecked(true);
                break;
            case Color.WHITE:
                radioButtonMap.get(R.id.radioButtonWhite).setChecked(true);
                break;
            case Color.DKGRAY:
                radioButtonMap.get(R.id.radioButtonGrey).setChecked(true);
                break;
            case Color.RED:
                radioButtonMap.get(R.id.radioButtonRed).setChecked(true);
                break;
            case Color.GREEN:
                radioButtonMap.get(R.id.radioButtonGreen).setChecked(true);
                break;
            case Color.BLUE:
                radioButtonMap.get(R.id.radioButtonBlue).setChecked(true);
                break;
            case Color.MAGENTA:
                radioButtonMap.get(R.id.radioButtonPurple).setChecked(true);
                break;
            case Color.YELLOW:
                radioButtonMap.get(R.id.radioButtonYellow).setChecked(true);
                break;
        }
    }

    private void updateSelectedColor(int viewId) {
        Log.v(TAG, "updateSelectedColor(): " + radioGroupTitle + ", viewId=" + viewId);

        switch (viewId) {
            case R.id.radioButtonBlack:
                color = Color.BLACK;
                selectedColor = "BLACK";
                break;
            case R.id.radioButtonWhite:
                color = Color.WHITE;
                selectedColor = "WHITE";
                break;
            case R.id.radioButtonGrey:
                color = Color.DKGRAY;
                selectedColor = "DKGRAY";
                break;
            case R.id.radioButtonRed:
                color = Color.RED;
                selectedColor = "RED";
                break;
            case R.id.radioButtonGreen:
                color = Color.GREEN;
                selectedColor = "GREEN";
                break;
            case R.id.radioButtonBlue:
                color = Color.BLUE;
                selectedColor = "BLUE";
                break;
            case R.id.radioButtonPurple:
                color = Color.MAGENTA;
                selectedColor = "MAGENTA";
                break;
            case R.id.radioButtonYellow:
                color = Color.YELLOW;
                selectedColor = "YELLOW";
                break;
            default:
                color = Color.DKGRAY;
                selectedColor = "Default-DKGRAY";
        }

        Log.v(TAG, "updateSelectedColor(): " + radioGroupTitle + ", Color=" + selectedColor);
    }


    public void setButtonColor(RadioButton button) {
        Log.v(TAG, "setButtonColor(): button=" + button);

        button.getBackground().setTint(Color.BLACK);
        switch (button.getId()) {
            case R.id.radioButtonBlack:
                button.getBackground().setTint(Color.BLACK);
                break;
            case R.id.radioButtonWhite:
                button.getBackground().setTint(Color.WHITE);
                break;
            case R.id.radioButtonGrey:
                button.getBackground().setTint(Color.DKGRAY);
                break;
            case R.id.radioButtonRed:
                button.getBackground().setTint(Color.RED);
                break;
            case R.id.radioButtonGreen:
                button.getBackground().setTint(Color.GREEN);
                break;
            case R.id.radioButtonBlue:
                button.getBackground().setTint(Color.BLUE);
                break;
            case R.id.radioButtonPurple:
                button.getBackground().setTint(Color.MAGENTA);
                break;
            case R.id.radioButtonYellow:
                button.getBackground().setTint(Color.YELLOW);
                break;
        }
    }
}
