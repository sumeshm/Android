package com.planetjup.tasks.utils;

import java.util.Comparator;

/**
 * This class represents the quick task
 * Created by Sumesh Mani on 2/16/18.
 */

public class TaskDetailsExtended extends  TaskDetails implements Comparator<TaskDetailsExtended>, Comparable<TaskDetailsExtended> {
    public static final String[] monthNames = {"JAN", "FEB", "MAR", "APRIL", "MAY", "JUNE", "JULY", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private final int dayOfMonth;
    private final int monthOfYear;

    public TaskDetailsExtended(String name, boolean isSelected, int dayOfMonth, int monthOfYear) {
        super(name, isSelected);

        if (monthOfYear < 1) {
            this.monthOfYear = 1;
        } else {
            this.monthOfYear = monthOfYear;
        }

        if (dayOfMonth < 1) {
            this.dayOfMonth = 1;
        } else {
            this.dayOfMonth = dayOfMonth;
        }
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public String getDayMonthName() {
        return monthNames[monthOfYear - 1] + " " + String.format("%02d", dayOfMonth);
    }

    public static String[] getMonths() {
        return monthNames;
    }

    @Override
    public int compare(TaskDetailsExtended left, TaskDetailsExtended right) {
        if (left.getMonthOfYear() > right.getMonthOfYear()) {
            return 1;
        } else if (left.getMonthOfYear() < right.getMonthOfYear()) {
            return -1;
        } else {
            if (left.getDayOfMonth() > right.getDayOfMonth()) {
                return 1;
            } else if (left.getDayOfMonth() < right.getDayOfMonth()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int compareTo(TaskDetailsExtended other) {
        if (this.getMonthOfYear() > other.getMonthOfYear()) {
            return 1;
        } else if (this.getMonthOfYear() < other.getMonthOfYear()) {
            return -1;
        } else {
            if (this.getDayOfMonth() > other.getDayOfMonth()) {
                return 1;
            } else if (this.getDayOfMonth() < other.getDayOfMonth()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
