package com.planetjup.tasks.utils;

/**
 * This class represents the quick task
 * Created by Sumesh Mani on 2/16/18.
 */

public class TaskDetails {
    private final String taskName;

    private boolean isCompleted;

    public TaskDetails(String name, boolean isSelected) {
        this.taskName = name;
        this.isCompleted = isSelected;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean selected) {
        isCompleted = selected;
    }
}
