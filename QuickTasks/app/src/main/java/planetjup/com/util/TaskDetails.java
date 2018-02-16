package planetjup.com.util;

/**
 * Created by summani on 2/16/18.
 */

public class TaskDetails {
    private String taskName = null;

    private boolean isCompleted = false;

    public TaskDetails(String name, boolean isSelected) {
        this.taskName = name;
        this.isCompleted = isSelected;
    }

    public String getTaskName() {
        return taskName;
    }


    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean selected) {
        isCompleted = selected;
    }
}
