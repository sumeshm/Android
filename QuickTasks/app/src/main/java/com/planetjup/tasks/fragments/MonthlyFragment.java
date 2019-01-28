package com.planetjup.tasks.fragments;

import android.os.Bundle;
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


public class MonthlyFragment extends TaskListFragment {

    private static final String TAG = MonthlyFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);

        ArrayList<TaskDetails> tasksList = PersistenceManager.readMonthlyTasksList(getContext());
        arrayAdapter = new TaskDetailsArrayAdapter(getContext(), R.layout.text_view, tasksList);

        listView = view.findViewById(R.id.listViewMonthly);
        listView.setAdapter(arrayAdapter);

        return view;
    }

    protected void persistTaskList() {
        Log.v(TAG, "populateListView()");

        PersistenceManager.writeMonthlyTasksList(getContext(), arrayAdapter.getTaskList());
    }
}
