package com.planetjup.tasks.utils;

/**
 * This class represents the quick task
 * Created by Sumesh Mani on 2/16/18.
 */

public class ReminderDetails {
    private int day;
    private int hour;
    private int minute;
    private final REMINDER_TYPE reminderType;
    private final REMINDER_ORIGIN reminderOrigin;

    public enum REMINDER_TYPE {
        REMINDER_TYPE_NONE(-1),
        REMINDER_TYPE_ONE(0),
        REMINDER_TYPE_TWO(1);

        private final int type;

        REMINDER_TYPE(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }

        public static REMINDER_TYPE getEnum(int value) {
            switch (value) {
                case 0:
                    return REMINDER_TYPE_ONE;
                case 1:
                    return REMINDER_TYPE_TWO;
            }

            return REMINDER_TYPE_NONE;
        }
    }


    public enum REMINDER_ORIGIN {
        NONE(-1),
        ACTIVITY(0),
        SERVICE(1);

        private final int type;

        REMINDER_ORIGIN(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }

        public static REMINDER_ORIGIN getEnum(int value) {
            switch (value) {
                case 0:
                    return ACTIVITY;
                case 1:
                    return SERVICE;
            }

            return NONE;
        }
    }

    public ReminderDetails(REMINDER_TYPE reminderType, REMINDER_ORIGIN reminderOrigin, int day, int hour, int minute) {
        this.reminderType = reminderType;
        this.reminderOrigin = reminderOrigin;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public REMINDER_TYPE getReminderType() {
        return reminderType;
    }

    public REMINDER_ORIGIN getReminderOrigin() {
        return reminderOrigin;
    }
}
