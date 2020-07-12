package com.planetjup.tasks.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.planetjup.tasks.utils.PersistenceManager;
import com.planetjup.tasks.utils.TaskDetails;
import com.planetjup.tasks.utils.TaskDetailsArrayAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import planetjup.com.tasks.R;

public class FragmentImpl  extends Fragment {

    private static final String TAG = FragmentImpl.class.getSimpleName();

    private int resourceId;
    private ArrayList<TaskDetails> tasksList;
    private TaskDetailsArrayAdapter arrayAdapter;

    public FragmentImpl(int resourceId, ArrayList<TaskDetails> tasksList) {
        this.resourceId = resourceId;
        this.tasksList = tasksList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");
        return inflater.inflate(resourceId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onViewCreated(): tasksList=" + tasksList.toString());

        arrayAdapter = new TaskDetailsArrayAdapter(view.getContext(), R.layout.text_view, tasksList);

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
    }

    public void addItem(String newItem) {
        Log.v(TAG, "addItem(): newItem=" + newItem);
        arrayAdapter.add(new TaskDetails(newItem, Boolean.FALSE));
        arrayAdapter.notifyDataSetChanged();
    }

    public ArrayList<TaskDetails> getList() {
        return tasksList;
    }

    public void reset() {
        arrayAdapter.resetListView();
    }
}
