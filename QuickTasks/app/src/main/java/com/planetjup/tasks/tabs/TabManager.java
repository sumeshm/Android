package com.planetjup.tasks.tabs;

import android.annotation.SuppressLint;
import android.util.Log;

import com.planetjup.tasks.utils.TaskDetails;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import planetjup.com.tasks.R;

// FragmentPagerAdapter
public class TabManager extends FragmentStatePagerAdapter {

    private static final String TAG = TabManager.class.getSimpleName();

    private int focusPosition;
    private final FragmentImpl tabMonthly;
    private final FragmentImpl tabYearly;
    private final FragmentImpl tabOthers;

    @SuppressLint("WrongConstant")
    public TabManager(@NonNull FragmentManager fm, ArrayList<TaskDetails> monthly, ArrayList<TaskDetails> yearly, ArrayList<TaskDetails> other) {
        super(fm, 3);
        Log.v(TAG, "TabManager()");

        tabMonthly = new FragmentImpl(R.layout.tab_list, monthly);
        tabYearly = new FragmentImpl(R.layout.tab_list, yearly);
        tabOthers = new FragmentImpl(R.layout.tab_list, other);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.v(TAG, "getItem(): position=" + position);

        switch (position) {
            case 0:
                return tabMonthly;
            case 1:
                return tabYearly;
            default:
                return tabOthers;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Log.v(TAG, "getPageTitle(): position=" + position);

        switch (position) {
            case 0:
                return "Monthly";
            case 1:
                return "Yearly";
            default:
                return "Others";
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public int getFocusItem() {
        return focusPosition;
    }

    public void setFocusItem(int position) {
        Log.v(TAG, "setFocusItem(): position=" + position);
        this.focusPosition = position;

        switch (position) {
            case 0:
                break;
            case 1:
                break;
            default:
        }
    }

    public void addItem(String newItem) {
        Log.v(TAG, "addItem(): newItem=" + newItem);

        switch (focusPosition) {
            case 0:
                tabMonthly.addItem(newItem);
                break;
            case 1:
                tabYearly.addItem(newItem);
                break;
            default:
                tabOthers.addItem(newItem);
        }
    }

    public ArrayList<TaskDetails> getMonthlyList() {
        return tabMonthly.getList();
    }

    public ArrayList<TaskDetails> getYearlyList() {
        return tabYearly.getList();
    }

    public ArrayList<TaskDetails> getOtherList() {
        return tabOthers.getList();
    }

    public void resetFocusList() {
        Log.v(TAG, "resetFocusList(): focusPosition=" + focusPosition);

        switch (focusPosition) {
            case 0:
                tabMonthly.reset();
                break;
            case 1:
                tabYearly.reset();
                break;
            default:
                tabOthers.reset();
        }
    }
}
