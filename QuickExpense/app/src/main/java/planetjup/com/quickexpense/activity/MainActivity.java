package planetjup.com.quickexpense.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import planetjup.com.quickexpense.R;
import planetjup.com.quickexpense.adapters.TripArrayAdapter;
import planetjup.com.quickexpense.pojo.TripDetails;


/**
 * This class will manage a quick expense.
 * <p>
 * Created by Sumesh Mani on 1/16/18.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ACTIVITY_TRIP = 0;
    private static final int ACTIVITY_USER = 1;
    private static final int MAX_LENGTH = 20;

    private TripArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_main);
        populateListView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.trip_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAdd:
                showPopupDialog();
                break;

            case R.id.menuReminderOne:
                break;

            case R.id.menuReminderTwo:
                break;
        }

        return true;
    }

    private void populateListView() {
        Log.v(TAG, "populateListView()");

        // todo: read from DB
        ArrayList<TripDetails> tripList = new ArrayList<>();
        tripList.add( new TripDetails("Paris"));
        tripList.add( new TripDetails("Rome"));

        arrayAdapter = new TripArrayAdapter(this, R.layout.list_view_trip, tripList);

        ListView listView = findViewById(R.id.listView_trip);
        listView.setAdapter(arrayAdapter);
    }

    private void showPopupDialog() {
        Log.v(TAG, "showPopupDialog()");

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_view, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.title_popup);
        builder.setIcon(R.drawable.ic_logo);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = dialogView.findViewById(R.id.editText);
                String newTrip = editText.getText().toString().trim();

                if (!newTrip.isEmpty()) {
                    // trim length if needed
                    if (newTrip.length() > MAX_LENGTH) {
                        newTrip = newTrip.substring(0, MAX_LENGTH - 1);
                    }

                    // call Trip-Tab-Activity
                    Intent tripTabIntent = new Intent(MainActivity.this, TripTabsActivity.class);
                    tripTabIntent.putExtra("tripName", newTrip);
                    startActivity(tripTabIntent);
                }
            }
        });

        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}

