package com.planetjup.tasks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.planetjup.tasks.utils.TaskDetails;

import java.util.ArrayList;

import planetjup.com.tasks.R;

/**
 * This is an custom ListView adapter
 * Created by Sumesh Mani on 2/16/18.
 */

public class TaskDetailsArrayAdapter extends ArrayAdapter<TaskDetails> implements View.OnClickListener {

    private static final String TAG = TaskDetailsArrayAdapter.class.getSimpleName();

    private final int resource;
    private final Context context;
    private final ArrayList<TaskDetails> taskList;
    private final int[] bgGradient = new int[]{R.drawable.gradient_odd, R.drawable.gradient_even};


    public TaskDetailsArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TaskDetails> list) {
        super(context, resource, list);

        this.resource = resource;
        this.context = context;
        this.taskList = new ArrayList<>(list);
    }

    public ArrayList<TaskDetails> getTaskList() {
        return taskList;
    }

    public void resetListView() {
        Log.v(TAG, "onClick()");

        for (TaskDetails taskDetails : taskList) {
            taskDetails.setCompleted(Boolean.FALSE);
        }

        notifyDataSetChanged();
    }

    @SuppressLint("Range")
    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick()");

        if (view.getTag() == null || view.getTag().getClass() != ListItemManager.class) {
            Log.v(TAG, "onClick() : no valid POJO in tag");
            return;
        }

        ListItemManager listItemManager = (ListItemManager) view.getTag();
        TaskDetails taskDetails = listItemManager.taskDetails;

        switch (view.getId()) {
            case R.id.checkBox:
                Log.v(TAG, "onClick() : CheckBox : Task=" + taskDetails.getTaskName() + ", index=" + getPosition(taskDetails));
                listItemManager.updateLook(listItemManager.checkBox.isChecked());
                break;

            case R.id.button_Delete:
                Log.v(TAG, "onClick() : Delete : Task=" + taskDetails.getTaskName() + ", index=" + getPosition(taskDetails));
                remove(taskDetails);
                notifyDataSetChanged();
                break;

            case R.id.button_Refresh:
                Log.v(TAG, "onClick() : Refresh : Task=" + taskDetails.getTaskName() + ", index=" + getPosition(taskDetails));
                if (taskDetails.isCompleted()) {
                    listItemManager.updateLook(Boolean.FALSE);
                }
                break;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.text_view, null);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(this.resource, parent, false);
        }

        int colorPosition = position % bgGradient.length;
        convertView.setBackground(this.context.getDrawable(bgGradient[colorPosition]));

        new ListItemManager(taskList.get(position), convertView, this);

        return convertView;
    }

    @Override
    public void add(@Nullable TaskDetails taskDetails) {
        Log.v(TAG, "add()");
        taskList.add(taskDetails);
        super.add(taskDetails);
    }

    @Override
    public void remove(@Nullable TaskDetails taskDetails) {
        Log.v(TAG, "remove()");
        taskList.remove(getPosition(taskDetails));
        super.remove(taskDetails);
    }


    private class ListItemManager {
        final private TaskDetails taskDetails;
        final private View view;
        final TextView textView;
        final private CheckBox checkBox;
        final private ImageButton deleteButton;
        final private ImageButton refreshButton;

        private ListItemManager(@NonNull TaskDetails taskDetails, @NonNull View view, @NonNull View.OnClickListener listener) {
            this.taskDetails = taskDetails;

            this.view = view;
            this.view.setTag(this);

            textView = view.findViewById(R.id.text_checkBox);
            textView.setText(taskDetails.getTaskName());

            checkBox = view.findViewById(R.id.checkBox);
            checkBox.setTag(this);
            checkBox.setOnClickListener(listener);

            deleteButton = view.findViewById(R.id.button_Delete);
            deleteButton.setTag(this);
            deleteButton.setOnClickListener(listener);

            refreshButton = view.findViewById(R.id.button_Refresh);
            refreshButton.setTag(this);
            refreshButton.setOnClickListener(listener);

            updateLook(taskDetails.isCompleted());
        }

        private void updateLook(boolean isChecked) {
            // update state
            this.checkBox.setChecked(isChecked);
            this.checkBox.setEnabled(!isChecked);
            this.taskDetails.setCompleted(isChecked);

            // update checkbox visibility and text color
            if (isChecked) {
                this.checkBox.setVisibility(View.INVISIBLE);
                this.textView.setTextColor(getContext().getColor(R.color.colorTextInactive));
            } else {
                this.checkBox.setVisibility(View.VISIBLE);
                this.textView.setTextColor(getContext().getColor(R.color.colorTextActive));
            }
        }
    }
}
