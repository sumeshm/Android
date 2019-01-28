package com.planetjup.tasks.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.planetjup.tasks.adapter.TaskDetailsArrayAdapter;
import com.planetjup.tasks.utils.TaskDetails;

import java.util.ArrayList;

public abstract class TaskListFragment extends Fragment {

    protected static final String TAG = DailyFragment.class.getSimpleName();

    protected TaskDetailsArrayAdapter arrayAdapter;
    protected ListView listView;

    public TaskListFragment() {
        // Required empty public constructor
    }

    public void addTask(String taskName) {
        Log.v(TAG, "resetListView()");
        arrayAdapter.add(new TaskDetails(taskName, Boolean.FALSE));
        arrayAdapter.notifyDataSetChanged();
    }

    public void addMultipleTasks(ArrayList<TaskDetails> newTaskList) {
        Log.v(TAG, "addMultipleTasks()");

        for (TaskDetails task : newTaskList) {
            arrayAdapter.add(new TaskDetails(task.getTaskName(), task.isCompleted()));
        }

        arrayAdapter.notifyDataSetChanged();
    }

    public void clearListView() {
        Log.v(TAG, "clearListView()");
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    public void resetListView() {
        Log.v(TAG, "resetListView()");
        arrayAdapter.resetListView();
    }

    public ArrayList<TaskDetails> getTaskList() {
        Log.v(TAG, "getTaskList()");
        return arrayAdapter.getTaskList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");

        persistTaskList();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");

        persistTaskList();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");

        persistTaskList();
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    protected abstract void persistTaskList();
}