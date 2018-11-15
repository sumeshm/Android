package planetjup.com.quickexpense.adapters;

import android.annotation.SuppressLint;
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

import planetjup.com.quickexpense.R;
import planetjup.com.quickexpense.pojo.UserDetails;

/**
 * This is an custom ListView adapter for User Details
 * Created by Sumesh Mani on 2/16/18.
 */

public class UserDetailsArrayAdapter extends ArrayAdapter<UserDetails> implements View.OnClickListener {

    private static final String TAG = UserDetailsArrayAdapter.class.getSimpleName();

    private final Context context;
    private final ArrayList<UserDetailsWrapper> pojoList = new ArrayList<>();
    private final int[] bgGradient = new int[]{R.drawable.gradient_odd, R.drawable.gradient_even};


    public UserDetailsArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<UserDetails> list) {
        super(context, resource, list);

        this.context = context;

        for (UserDetails userDetails : list) {
            UserDetailsWrapper pojoWrapper = new UserDetailsWrapper(userDetails);
            pojoList.add(pojoWrapper);
        }
    }

    public ArrayList<UserDetails> getSelectedItems() {
        ArrayList<UserDetails> retList = new ArrayList<>();

        for (UserDetailsWrapper pojoWrapper : pojoList) {
            if (pojoWrapper.isChecked()) {
                retList.add(pojoWrapper.getUserDetails());
            }
        }

        return retList;
    }

    @SuppressLint("Range")
    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick()");

        if (view.getTag() == null || view.getTag().getClass() != UserDetailsWrapper.class) {
            Log.v(TAG, "onClick() : no valid POJO in tag");
            return;
        }

        switch (view.getId()) {
            case R.id.checkBox:
                CheckBox checkBox = view.findViewById(R.id.checkBox);
                UserDetailsWrapper pojoWrapper = (UserDetailsWrapper) view.getTag();
                pojoWrapper.setChecked(checkBox.isChecked());
                break;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_view, null);
        }

        int colorPosition = position % bgGradient.length;
        convertView.setBackground(this.context.getDrawable(bgGradient[colorPosition]));

        TextView textView = convertView.findViewById(R.id.text_userName);
        textView.setText(pojoList.get(position).getUserDetails().getFullName());

        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        checkBox.setTag(pojoList.get(position));
        checkBox.setOnClickListener(this);

        return convertView;
    }


    private class UserDetailsWrapper {
        private final UserDetails userDetails;
        private boolean isChecked;

        private UserDetailsWrapper(@NonNull UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        public UserDetails getUserDetails() {
            return userDetails;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
