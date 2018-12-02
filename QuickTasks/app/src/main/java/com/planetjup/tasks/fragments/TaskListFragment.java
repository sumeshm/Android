package com.planetjup.tasks.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.planetjup.tasks.adapter.TaskDetailsArrayAdapter;
import com.planetjup.tasks.utils.PersistenceManager;
import com.planetjup.tasks.utils.TaskDetails;

import planetjup.com.tasks.R;

public abstract class TaskListFragment extends Fragment {

    protected static final String TAG = DailyFragment.class.getSimpleName();

    protected TaskDetailsArrayAdapter arrayAdapter;

    public TaskListFragment() {
        // Required empty public constructor
    }

    public void addTask(String taskName) {
        Log.v(TAG, "resetListView()");
        arrayAdapter.add(new TaskDetails(taskName, Boolean.FALSE));
        arrayAdapter.notifyDataSetChanged();
    }

    public void resetListView() {
        Log.v(TAG, "resetListView()");
        arrayAdapter.resetListView();
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

        PersistenceManager.writeMonthlyTasksList(getContext(), arrayAdapter.getTaskList());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");

        PersistenceManager.writeMonthlyTasksList(getContext(), arrayAdapter.getTaskList());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");

        PersistenceManager.writeMonthlyTasksList(getContext(), arrayAdapter.getTaskList());
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);

    protected abstract void populateTaskList(View view);
}