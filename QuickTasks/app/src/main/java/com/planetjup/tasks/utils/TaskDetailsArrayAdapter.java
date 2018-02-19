package com.planetjup.tasks.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import planetjup.com.tasks.R;

/**
 * This is an custom ListView adapter
 * Created by Sumesh Mani on 2/16/18.
 */

public class TaskDetailsArrayAdapter extends ArrayAdapter<TaskDetails> implements View.OnClickListener {

    private static final String TAG = TaskDetailsArrayAdapter.class.getSimpleName();

    final private Context context;

    final private ArrayList<TaskDetails> tasksList;


    public TaskDetailsArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TaskDetails> list) {
        super(context, resource, list);

        this.context = context;
        this.tasksList = new ArrayList<>(list);
    }

    public ArrayList<TaskDetails> getTasksList() {
        return tasksList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.text_view, null);
        }

        ListItemManager listItemManager = new ListItemManager(tasksList.get(position), convertView, this);

        return convertView;
    }

    @Override
    public void add(@Nullable TaskDetails taskDetails) {
        Log.v(TAG, "add()");
        tasksList.add(taskDetails);
        super.add(taskDetails);
    }

    @Override
    public void remove(@Nullable TaskDetails taskDetails) {
        Log.v(TAG, "remove()");
        tasksList.remove(getPosition(taskDetails));
        super.remove(taskDetails);
    }



    private class ListItemManager {
        final private TaskDetails taskDetails;
        final private View view;
        final TextView textView;
        final private CheckBox checkBox;
        final private ImageButton deleteButton;
        final private ImageButton refreshButton;

        public ListItemManager(@NonNull TaskDetails taskDetails, @NonNull View view, @NonNull View.OnClickListener listener) {
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

        private void updateLook(boolean isChecked)
        {
            int colorInt = isChecked ? ContextCompat.getColor(view.getContext(), R.color.colorOrangeLight) : Color.WHITE;

            // update state
            this.checkBox.setChecked(isChecked);
            this.checkBox.setEnabled(!isChecked);
            this.taskDetails.setCompleted(isChecked);

            // update background color
            this.view.setBackgroundColor(colorInt);
            this.deleteButton.setBackgroundColor(colorInt);
            this.refreshButton.setBackgroundColor(colorInt);

            if (isChecked)
            {
                this.checkBox.setVisibility(View.INVISIBLE);
            }
            else
            {
                this.checkBox.setVisibility(View.VISIBLE);
            }
        }
    }
}
