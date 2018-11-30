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


public class MonthlyFragment extends Fragment {

    private static final String TAG = MonthlyFragment.class.getSimpleName();

    private TaskDetailsArrayAdapter arrayAdapter;

    public MonthlyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);
        populateTaskList(view);
        return view;
    }

    private void populateTaskList(View view) {
        Log.v(TAG, "populateListView()");

        ArrayList<TaskDetails> tasksList = PersistenceManager.readTasksList(getContext());
        tasksList.add(new TaskDetails("abc", true));
        tasksList.add(new TaskDetails("def", false));

        arrayAdapter = new TaskDetailsArrayAdapter(getContext(), R.layout.text_view, tasksList);

        ListView listView = view.findViewById(R.id.listViewMonthly);
        listView.setAdapter(arrayAdapter);
    }
}
