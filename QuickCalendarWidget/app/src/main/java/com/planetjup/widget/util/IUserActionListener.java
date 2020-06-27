package com.planetjup.widget.util;

import android.support.annotation.ColorInt;

public interface IUserActionListener {

    void radioButtonClicked(String radioGroupId, @ColorInt int color);

    void checkBoxClicked(String checkBoxId, boolean isChecked);

    void progressBarChanged(String checkBoxId, int progress);

    void saveButtonClicked();
}
