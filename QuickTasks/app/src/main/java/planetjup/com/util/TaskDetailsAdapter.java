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
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import planetjup.com.tasks.R;

/**
 * Created by summani on 2/16/18.
 */

public class TaskDetailsAdapter extends ArrayAdapter<TaskDetails> implements View.OnClickListener {

    private static final String TAG = TaskDetailsAdapter.class.getSimpleName();

    final private Context context;

    final private ArrayList<TaskDetails> tasksList;


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
        Log.v(TAG, "onClick()");

        if (view.getTag() == null || view.getTag().getClass() != Container.class) {
            Log.v(TAG, "onClick() : no valid POJO in tag");
            return;
        }

        Container container = (Container) view.getTag();
        TaskDetails taskDetails = container.taskDetails;
        CheckBox checkBox = container.checkBox;

        switch (view.getId()) {
            case R.id.checkBox:
                Log.v(TAG, "onClick() : CheckBox : " + taskDetails.getTaskName());
                boolean isChecked = checkBox.isChecked();
                checkBox.setEnabled(!isChecked);
                taskDetails.setCompleted(isChecked);
                break;

            case R.id.button_Delete:
                Log.v(TAG, "onClick() : Delete : " + taskDetails.getTaskName());
                remove(taskDetails);
                break;

            case R.id.button_Refresh:
                Log.v(TAG, "onClick() : Delete : " + taskDetails.getTaskName());
                if (checkBox.isChecked()) {
                    checkBox.setChecked(Boolean.FALSE);
                    checkBox.setEnabled(Boolean.TRUE);
                    taskDetails.setCompleted(Boolean.FALSE);
                }
                break;
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TaskDetails taskDetails = tasksList.get(position);
        Container container = new Container();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.text_view, null);
        }

        TextView textView = convertView.findViewById(R.id.text_checkBox);
        textView.setText(taskDetails.getTaskName());

        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        boolean isChecked = taskDetails.isCompleted();
        checkBox.setChecked(isChecked);
        checkBox.setEnabled(!isChecked);
        checkBox.setTag(container);
        checkBox.setOnClickListener(this);

        ImageButton deleteButton = convertView.findViewById(R.id.button_Delete);
        deleteButton.setTag(container);
        deleteButton.setOnClickListener(this);

        ImageButton refreshButton = convertView.findViewById(R.id.button_Refresh);
        refreshButton.setTag(container);
        refreshButton.setOnClickListener(this);

        container.checkBox = checkBox;
        container.taskDetails = taskDetails;

        return convertView;
    }


    private class Container {
        TaskDetails taskDetails;
        CheckBox checkBox;
    }
}
