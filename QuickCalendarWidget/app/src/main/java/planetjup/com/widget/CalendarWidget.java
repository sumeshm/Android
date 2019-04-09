package planetjup.com.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.CalendarContract;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
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
        Log.v(TAG, "updateAppWidget()");

        // initializing widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        updateUI(context, remoteViews);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive(): action=" + intent.getAction());
        super.onReceive(context, intent);

        if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction()) || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            updateUI(context, remoteViews);

            // Instruct the widget manager to update the widget
            ComponentName calendarWidget = new ComponentName(context, CalendarWidget.class);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(calendarWidget, remoteViews);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate(): appWidgetIds=" + appWidgetIds.toString());
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.v(TAG, "onEnabled()");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void updateUI(Context context, RemoteViews remoteViews) {
        Log.v(TAG, "updateUI()");

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

        Intent intent = new Intent(context, Calendar.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);

        for (int count = 0; count < dayList.size(); count++) {
            int idDay = idList.get(count);
            int idDate = idList.get(count + 7);
            int idEvent = idList.get(count + 14);

            remoteViews.setTextViewText(idDay, dayList.get(count));
            remoteViews.setTextViewText(idDate, "" + today.get(Calendar.DAY_OF_MONTH));
            remoteViews.setTextViewText(idEvent, "e-" + today.get(Calendar.DAY_OF_MONTH));

            // mark current day
            int colorId = R.color.colorPrimaryDark;
            int newDayOfMonth = today.get(Calendar.DAY_OF_MONTH);
            if (newDayOfMonth == dayOfMonth) {
                colorId = R.color.colorAccent;
            }

            remoteViews.setTextColor(idDay, context.getColor(colorId));
            remoteViews.setTextColor(idDate, context.getColor(colorId));
            remoteViews.setTextColor(idEvent, context.getColor(colorId));

//            remoteViews.setOnClickPendingIntent(idDay, pendingIntent);
//            remoteViews.setOnClickPendingIntent(idDate, pendingIntent);
//            remoteViews.setOnClickPendingIntent(idEvent, pendingIntent);

            today.add(Calendar.DAY_OF_WEEK, 1);
        }
    }
}

