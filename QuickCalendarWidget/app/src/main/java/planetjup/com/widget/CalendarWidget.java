package planetjup.com.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class CalendarWidget extends AppWidgetProvider {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ACTION_SHOW_CALENDAR = "ACTION_SHOW_CALENDAR";

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

        if (ACTION_SHOW_CALENDAR.equals(intent.getAction())) {
            ComponentName componentName = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
            Intent calIntent = new Intent();
            calIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            calIntent.setComponent(componentName);
            context.startActivity(calIntent);

        } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction()) || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
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
        Log.v(TAG, "onUpdate()");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.v(TAG, "onUpdate(): appWidgetId=" + appWidgetId);
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

        // make Monday first day of the week
        Calendar today = Calendar.getInstance();
        today.setFirstDayOfWeek(Calendar.MONDAY);

        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        int firstDay = today.getFirstDayOfWeek();
        int delta = firstDay - dayOfWeek;

        // move date to starting of the week
        today.add(Calendar.DAY_OF_WEEK, delta);

        // add seven days of calendar data - Day, Date, Events
        for (int count = 0; count < dayList.size(); count++) {
            int idDay = idList.get(count);
            int idDate = idList.get(count + 7);
            int idEvent = idList.get(count + 14);

            List<EventDetails> eventList = readCalendarEvents(context, today);
            StringBuilder builder = new StringBuilder();
            for (EventDetails eventDetails : eventList) {
                int maxLen = 9;
                String title = eventDetails.getTitle();
                if (title.length() > maxLen) {
                    title = title.substring(0, maxLen);
                }

                builder.append(title);
                builder.append("\n");
            }

            // data
            remoteViews.setTextViewText(idDay, dayList.get(count));
            remoteViews.setTextViewText(idDate, "" + today.get(Calendar.DAY_OF_MONTH));
            remoteViews.setTextViewText(idEvent, builder.toString());

            // color
            remoteViews.setTextColor(idEvent, Color.WHITE);

            // highlight current day
            if (today.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                remoteViews.setTextColor(idDay, Color.BLACK);
                remoteViews.setTextColor(idDate, Color.BLUE);
            } else {
                remoteViews.setTextColor(idDay, Color.GRAY);
                remoteViews.setTextColor(idDate, Color.DKGRAY);
            }

            // increment date
            today.add(Calendar.DAY_OF_WEEK, 1);
        }

        Intent intent = new Intent(context, CalendarWidget.class);
        intent.setAction(ACTION_SHOW_CALENDAR);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.textDay1, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDay2, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDay3, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDay4, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDay5, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDay6, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDay7, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDate1, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDate2, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDate3, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDate4, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDate5, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDate6, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textDate7, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textEvent1, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textEvent2, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textEvent3, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textEvent4, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textEvent5, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textEvent6, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.textEvent7, pendingIntent);
    }

    private static List<EventDetails> readCalendarEvents(Context context, Calendar filterDay) {
        List<EventDetails> eventList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return eventList;
        }

        Log.v(TAG, "readCalendarEvents(): Date=" + filterDay.get(Calendar.DAY_OF_MONTH));

        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events.TITLE,          // 0
                CalendarContract.Events.DTSTART,        // 1
                CalendarContract.Events.EVENT_COLOR     // 2
        };
        ContentResolver contentResolver = context.getContentResolver();
        Uri eventUri = CalendarContract.Events.CONTENT_URI;

        Calendar startTime = Calendar.getInstance();
        startTime.set(filterDay.get(Calendar.YEAR), filterDay.get(Calendar.MONTH), filterDay.get(Calendar.DAY_OF_MONTH), 00, 00, 00);

        Calendar endTime = Calendar.getInstance();
        endTime.set(filterDay.get(Calendar.YEAR), filterDay.get(Calendar.MONTH), filterDay.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))";


        Cursor cursor = contentResolver.query(eventUri, EVENT_PROJECTION, selection, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Log.v(TAG, "readCalendarEvents(): Title" + cursor.getString(0));
            Log.v(TAG, "readCalendarEvents(): Start=" + new Date(cursor.getLong(1)));
            Log.v(TAG, "readCalendarEvents(): Color=" + cursor.getString(2));

            EventDetails eventDetails = new EventDetails(cursor.getString(0), cursor.getString(2));
            eventList.add(eventDetails);
            cursor.moveToNext();
        }
        cursor.close();

        return eventList;
    }

    private static void readCalendarList(Context context, Calendar day) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        String selection = "(("
                + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";

        // TODO: how to get email id
        String[] selectionArgs = new String[]{"sumesh.mani@gmail.com", "sumesh.mani@gmail.com", "sumesh.mani@gmail.com"};

        Cursor cursor = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        int temp = cursor.getCount();
        Log.v(TAG, "readCalendarList(): cursor.getCount()=" + cursor.getCount());

        int PROJECTION_ID_INDEX = 0;
        int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        int PROJECTION_DISPLAY_NAME_INDEX = 2;
        int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        while (cursor.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cursor.getLong(PROJECTION_ID_INDEX);
            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            Log.v(TAG, "readCalendarList(): calID=" + calID);
            Log.v(TAG, "readCalendarList(): displayName=" + displayName);
            Log.v(TAG, "readCalendarList(): accountName=" + accountName);
            Log.v(TAG, "readCalendarList(): ownerName=" + ownerName);
            // Do something with the values...
        }
    }


    static class EventDetails {
        private String title;
        private String colorCode;

        public EventDetails(String title, String colorCode) {
            this.title = title;
            this.colorCode = colorCode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getColorCode() {
            return colorCode;
        }

        public void setColorCode(String colorCode) {
            this.colorCode = colorCode;
        }
    }
}

