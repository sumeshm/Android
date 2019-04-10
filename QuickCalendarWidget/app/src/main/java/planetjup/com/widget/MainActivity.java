package planetjup.com.widget;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ON_CALENDAR_PERMISSION_CALLBACK_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getContactsPermission();

//        setContentView(R.layout.activity_main);
//        populateTableView();

        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult :: requestCode=" + requestCode);

        if (requestCode == ON_CALENDAR_PERMISSION_CALLBACK_CODE) {
            Log.v(TAG, "onActivityResult: ON_CONTACTS_PERMISSION_CALLBACK_CODE");

            // todo: persist the permission result for widget - SUCCESS
        }
    }

    private void getContactsPermission() {
        Log.v(TAG, "getContactsPermission()");

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CALENDAR)) {
            Log.v(TAG, "getContactsPermission : READ_CALENDAR=FALSE");
            // todo: persist the permission result for widget - PENDING

            String[] permissionList = {Manifest.permission.READ_CALENDAR};
            requestPermissions(permissionList, ON_CALENDAR_PERMISSION_CALLBACK_CODE);

        } else {
            Log.v(TAG, "getContactsPermission : READ_CALENDAR=TRUE");
            // todo: persist the permission result for widget - SUCCESS
        }
    }

    private void populateTableView() {
        ArrayList<String> dayList = new ArrayList<>();
        dayList.add(getString(R.string.day_monday));
        dayList.add(getString(R.string.day_tuesday));
        dayList.add(getString(R.string.day_wednesday));
        dayList.add(getString(R.string.day_thursday));
        dayList.add(getString(R.string.day_friday));
        dayList.add(getString(R.string.day_saturday));
        dayList.add(getString(R.string.day_sunday));

        TableRow rowDay = new TableRow(this);
        rowDay.setBackground(getBaseContext().getDrawable(R.drawable.gradient));

        TableRow rowDate = new TableRow(this);
        rowDate.setBackground(getBaseContext().getDrawable(R.drawable.gradient));

        TableRow rowEvent = new TableRow(this);
        rowEvent.setBackground(getBaseContext().getDrawable(R.drawable.gradient));

        Calendar today = Calendar.getInstance();
        today.setFirstDayOfWeek(Calendar.MONDAY);
        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        int firstDay = today.getFirstDayOfWeek();
        int delta = firstDay - dayOfWeek;
        today.add(Calendar.DAY_OF_WEEK, delta);

        for (int count = 0; count < dayList.size(); count++) {
            TextView textDay = new TextView(this);
            textDay.setText(dayList.get(count));
            textDay.setGravity(Gravity.CENTER);
            textDay.setTextSize(20);
            rowDay.addView(textDay);

            TextView textDate = new TextView(this);
            textDate.setTextSize(20);
            textDate.setText("" + today.get(Calendar.DAY_OF_MONTH));
            textDate.setGravity(Gravity.CENTER);
            rowDate.addView(textDate);

            TextView textEvent = new TextView(this);
            textEvent.setText("event-" + today.get(Calendar.DAY_OF_MONTH));
            textEvent.setGravity(Gravity.CENTER);
            rowEvent.addView(textEvent);

            int newDayOfMonth = today.get(Calendar.DAY_OF_MONTH);
            if (newDayOfMonth == dayOfMonth) {
                textDay.setTextColor(getBaseContext().getColor(R.color.colorAccent));
                textDate.setTextColor(getBaseContext().getColor(R.color.colorAccent));
                textEvent.setTextColor(getBaseContext().getColor(R.color.colorAccent));
            }

            today.add(Calendar.DAY_OF_WEEK, 1);
        }

        TableLayout customTableLayout = findViewById(R.id.tableView);
        customTableLayout.removeAllViews();

        customTableLayout.addView(rowDay);
        customTableLayout.addView(rowDate);
        customTableLayout.addView(rowEvent);
    }
}
