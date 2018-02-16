package planetjup.com.util;

/**
 * Created by summani on 2/16/18.
 */

public class TaskDetails {
    private String taskName = null;

    private boolean isSelected = false;

    public TaskDetails(String name, boolean isSelected) {
        this.taskName = name;
        this.isSelected = isSelected;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
