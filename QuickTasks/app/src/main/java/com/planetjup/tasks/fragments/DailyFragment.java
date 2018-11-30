package com.planetjup.tasks.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
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

public class DailyFragment extends Fragment {

    private static final String TAG = DailyFragment.class.getSimpleName();

    private TaskDetailsArrayAdapter arrayAdapter;

    public DailyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        populateTaskList(view);
        return view;
    }

    private void populateTaskList(View view) {
        Log.v(TAG, "populateTaskList()");

        ArrayList<TaskDetails> tasksList = PersistenceManager.readTasksList(getContext());
        tasksList.add(new TaskDetails("123", true));
        tasksList.add(new TaskDetails("456", false));

        arrayAdapter = new TaskDetailsArrayAdapter(getContext(), R.layout.text_view, tasksList);

        ListView listView = view.findViewById(R.id.listViewDaily);
        listView.setAdapter(arrayAdapter);
    }
}
