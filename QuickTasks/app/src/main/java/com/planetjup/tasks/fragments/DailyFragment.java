package com.planetjup.tasks.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.planetjup.tasks.adapter.TaskDetailsArrayAdapter;
import com.planetjup.tasks.utils.PersistenceManager;
import com.planetjup.tasks.utils.TaskDetails;

import java.util.ArrayList;

import planetjup.com.tasks.R;

public class DailyFragment extends TaskListFragment {

    private static final String TAG = DailyFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        populateTaskList(view);
        return view;
    }

    protected void populateTaskList(View view) {
        Log.v(TAG, "populateTaskList()");

        ArrayList<TaskDetails> tasksList = PersistenceManager.readDailyTasksList(getContext());
        arrayAdapter = new TaskDetailsArrayAdapter(getContext(), R.layout.text_view, tasksList);

        ListView listView = view.findViewById(R.id.listViewDaily);
        listView.setAdapter(arrayAdapter);
    }
}
