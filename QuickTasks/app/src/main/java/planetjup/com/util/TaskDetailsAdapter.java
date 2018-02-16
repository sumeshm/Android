package planetjup.com.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import planetjup.com.tasks.R;

/**
 * Created by summani on 2/16/18.
 */

public class TaskDetailsAdapter extends ArrayAdapter<TaskDetails> implements View.OnClickListener {

    private static final String TAG = TaskDetailsAdapter.class.getSimpleName();

    private Context context;

    private ArrayList<TaskDetails> tasksList;


    public TaskDetailsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TaskDetails> list) {
        super(context, resource, list);

        this.context = context;
        this.tasksList = list;
    }

    public ArrayList<TaskDetails> getTasksList() {
        return tasksList;
    }

    @Override
    public void onClick(View view) {
        CheckBox checkBox = (CheckBox) view;

        TaskDetails taskDetails = (TaskDetails) checkBox.getTag();
        taskDetails.setSelected(checkBox.isChecked());
        Log.v(TAG, "onClick() : " + taskDetails.getTaskName());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = null;
        CheckBox checkBox = null;
        TaskDetails taskDetails = tasksList.get(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.text_view, null);
        }

        textView = (TextView) convertView.findViewById(R.id.text_checkBox);
        textView.setText(taskDetails.getTaskName());

        checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        checkBox.setChecked(taskDetails.isSelected());
        checkBox.setTag(taskDetails);
        checkBox.setOnClickListener(this);

        return convertView;
    }
}
