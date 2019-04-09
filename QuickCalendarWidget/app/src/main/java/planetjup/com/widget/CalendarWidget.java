package planetjup.com.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class CalendarWidget extends AppWidgetProvider {

    private static final String TAG = MainActivity.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        ArrayList<String> dayList = new ArrayList<>();
        dayList.add(context.getString(R.string.day_monday));
        dayList.add(context.getString(R.string.day_tuesday));
        dayList.add(context.getString(R.string.day_wednesday));
        dayList.add(context.getString(R.string.day_thursday));
        dayList.add(context.getString(R.string.day_friday));
        dayList.add(context.getString(R.string.day_saturday));
        dayList.add(context.getString(R.string.day_sunday));

        ArrayList<Integer> idList = new ArrayList<>();
        idList.add(R.id.textDay1);
        idList.add(R.id.textDay2);
        idList.add(R.id.textDay3);
        idList.add(R.id.textDay4);
        idList.add(R.id.textDay5);
        idList.add(R.id.textDay6);
        idList.add(R.id.textDay7);

        idList.add(R.id.textDate1);
        idList.add(R.id.textDate2);
        idList.add(R.id.textDate3);
        idList.add(R.id.textDate4);
        idList.add(R.id.textDate5);
        idList.add(R.id.textDate6);
        idList.add(R.id.textDate7);

        idList.add(R.id.textEvent1);
        idList.add(R.id.textEvent2);
        idList.add(R.id.textEvent3);
        idList.add(R.id.textEvent4);
        idList.add(R.id.textEvent5);
        idList.add(R.id.textEvent6);
        idList.add(R.id.textEvent7);

        Calendar today = Calendar.getInstance();
        today.setFirstDayOfWeek(Calendar.MONDAY);
        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        int firstDay = today.getFirstDayOfWeek();
        int delta = firstDay - dayOfWeek;
        today.add(Calendar.DAY_OF_WEEK, delta);

        // initializing widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        for (int count = 0; count < dayList.size(); count++) {
            int idDay = idList.get(count);
            int idDate = idList.get(count + 7);
            int idEvent = idList.get(count + 14);

            remoteViews.setTextViewText(idDay, dayList.get(count));
            remoteViews.setTextViewText(idDate, "" + today.get(Calendar.DAY_OF_MONTH));
            remoteViews.setTextViewText(idEvent, "e-" + today.get(Calendar.DAY_OF_MONTH));

            // mark current day
            int newDayOfMonth = today.get(Calendar.DAY_OF_MONTH);
            if (newDayOfMonth == dayOfMonth) {
                remoteViews.setTextColor(idDay, context.getColor(R.color.colorAccent));
                remoteViews.setTextColor(idDate, context.getColor(R.color.colorAccent));
                remoteViews.setTextColor(idEvent, context.getColor(R.color.colorAccent));
            }

            today.add(Calendar.DAY_OF_WEEK, 1);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

