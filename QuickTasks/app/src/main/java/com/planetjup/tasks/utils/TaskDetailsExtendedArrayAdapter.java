package com.planetjup.tasks.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import planetjup.com.tasks.R;

/**
 * This is an custom ListView adapter
 * Created by Sumesh Mani on 2/16/18.
 */

public class TaskDetailsExtendedArrayAdapter extends ArrayAdapter<TaskDetailsExtended> implements View.OnClickListener {

    private static final String TAG = TaskDetailsExtendedArrayAdapter.class.getSimpleName();

    private final Context context;
    private final int[] bgGradient = new int[]{R.drawable.gradient_odd, R.drawable.gradient_even};

    private ArrayList<TaskDetailsExtended> taskList;

    public TaskDetailsExtendedArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TaskDetailsExtended> list) {
        super(context, resource, list);

        this.context = context;
        this.taskList = new ArrayList<>(list);
        sort();
    }

    public void resetListView() {
        Log.v(TAG, "resetListView()");

        for (TaskDetailsExtended taskDetails : taskList) {
            taskDetails.setCompleted(Boolean.FALSE);
        }

        sort();
        notifyDataSetChanged();
    }

    public void resetListData(ArrayList<TaskDetailsExtended> newList) {
        Log.v(TAG, "resetListData()");

        for (TaskDetailsExtended taskDetails : taskList) {
            super.remove(taskDetails);
        }

        taskList.clear();
        for (TaskDetailsExtended taskDetails : newList) {
            add(taskDetails);
        }

        sort();
        notifyDataSetChanged();
    }

    private void sort() {
        Collections.sort(taskList);
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
        TaskDetailsExtended taskDetails = listItemManager.taskDetails;

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
            convertView = LayoutInflater.from(context).inflate(R.layout.text_view_extended, null);
        }

        int colorPosition = position % bgGradient.length;
        convertView.setBackground(this.context.getDrawable(bgGradient[colorPosition]));

        new ListItemManager(taskList.get(position), convertView, this);

        return convertView;
    }

    @Override
    public void add(@Nullable TaskDetailsExtended taskDetails) {
        Log.v(TAG, "add()");
        taskList.add(taskDetails);
        super.add(taskDetails);
        sort();
    }

    @Override
    public void remove(@Nullable TaskDetailsExtended taskDetails) {
        Log.v(TAG, "remove()");
        taskList.remove(getPosition(taskDetails));
        super.remove(taskDetails);
    }

    private class ListItemManager {
        final TextView textView;
        final private TaskDetailsExtended taskDetails;
        final private View view;
        final private CheckBox checkBox;
        final private ImageButton deleteButton;
        final private ImageButton refreshButton;
        final private TextView dayMonth;

        private ListItemManager(@NonNull TaskDetailsExtended taskDetails, @NonNull View view, @NonNull View.OnClickListener listener) {
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

            dayMonth = view.findViewById(R.id.text_date);
            dayMonth.setText(taskDetails.getDayMonthName());
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
