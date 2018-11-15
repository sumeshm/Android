package planetjup.com.quickexpense.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import planetjup.com.quickexpense.R;
import planetjup.com.quickexpense.adapters.UserDetailsArrayAdapter;
import planetjup.com.quickexpense.pojo.UserDetails;

public class TripActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = TripActivity.class.getSimpleName();
    private UserDetailsArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_trip);
        populateListView();
    }


    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(View) : view_id=" + view.getId());

        switch (view.getId()) {
            case R.id.button_Go:
                EditText editText = findViewById(R.id.text_rowName);
                String tripName = editText.getText().toString();
                ArrayList<UserDetails> userList = arrayAdapter.getSelectedItems();
                if (!tripName.isEmpty() && userList.size() > 0) {
                    Log.v(TAG, "onClick(View) : button_Go: userList=" + userList.size());

                    // todo: add this trip to DB

                    Intent returnIntent = getIntent();
                    returnIntent.putExtra("tripName", tripName);
                    setResult(RESULT_OK, returnIntent);

                    finish();
                } else {
                    Toast.makeText(this, R.string.toast_trip, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void populateListView() {
        Log.v(TAG, "populateListView()");

        // todo: read from DB
        ArrayList<UserDetails> tripList = new ArrayList<>();
        tripList.add(new UserDetails("James", "Bond"));
        tripList.add(new UserDetails("Money", "Penny"));

        arrayAdapter = new UserDetailsArrayAdapter(this, R.layout.user_view, tripList);

        ListView listView = findViewById(R.id.listView_user);
        listView.setAdapter(arrayAdapter);
    }
}
