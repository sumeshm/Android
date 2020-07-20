package com.planetjup.tasks.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.planetjup.tasks.utils.TaskDetails;
import com.planetjup.tasks.utils.TaskDetailsArrayAdapter;
import com.planetjup.tasks.utils.TaskDetailsExtended;
import com.planetjup.tasks.utils.TaskDetailsExtendedArrayAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import planetjup.com.tasks.R;

public class FragmentExtendedImpl extends Fragment {

    private static final String TAG = FragmentExtendedImpl.class.getSimpleName();

    private final int resourceId;
    private final ArrayList<TaskDetailsExtended> tasksList;
    private TaskDetailsExtendedArrayAdapter arrayAdapter;

    public FragmentExtendedImpl(int resourceId, ArrayList<TaskDetailsExtended> tasksList) {
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

        arrayAdapter = new TaskDetailsExtendedArrayAdapter(view.getContext(), R.layout.text_view_extended, tasksList);

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
    }

    public void addItem(String newItem, int day, int month) {
        Log.v(TAG, "addItem(): newItem=" + newItem);
        arrayAdapter.add(new TaskDetailsExtended(newItem, Boolean.FALSE, day, month));
        arrayAdapter.notifyDataSetChanged();
    }

    public ArrayList<TaskDetailsExtended> getList() {
        return tasksList;
    }

    public void setList(ArrayList<TaskDetailsExtended> newList) {
        if (arrayAdapter != null) {
            arrayAdapter.resetListData(newList);
        }
    }

    public void reset() {
        arrayAdapter.resetListView();
    }
}
