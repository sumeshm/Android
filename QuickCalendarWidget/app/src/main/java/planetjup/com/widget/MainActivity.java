package planetjup.com.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TableLayout customTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customTableLayout = findViewById(R.id.tableView);
        customTableLayout.removeAllViews();
        populateTableView();
    }

    private void populateTableView() {
        ArrayList<String> dayList = new ArrayList<>();
        dayList.add(getString(R.string.day_monday));
        dayList.add(getString(R.string.day_tuesday));
        dayList.add(getString(R.string.day_wednesday));
        dayList.add(getString(R.string.day_thursdy));
        dayList.add(getString(R.string.day_friday));
        dayList.add(getString(R.string.day_saturday));
        dayList.add(getString(R.string.day_sunday));

        TableRow rowDay = new TableRow(this);
        rowDay.setBackground(getBaseContext().getDrawable(R.drawable.gradient_odd));

        TableRow rowDate = new TableRow(this);
        rowDate.setBackground(getBaseContext().getDrawable(R.drawable.gradient_even));

        TableRow rowEvent = new TableRow(this);
        rowEvent.setBackground(getBaseContext().getDrawable(R.drawable.gradient_odd));

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

        customTableLayout.addView(rowDay);
        customTableLayout.addView(rowDate);
        customTableLayout.addView(rowEvent);
    }
}
