package com.planetjup.widget;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.planetjup.widget.util.Constants;
import com.planetjup.widget.util.PersistenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class CalendarWidget extends AppWidgetProvider {

    private static final String TAG = CalendarWidget.class.getSimpleName();

    // View ids
    private static final List<Integer> dayIdList = Arrays.asList(R.id.textDay1, R.id.textDay2,
            R.id.textDay3, R.id.textDay4, R.id.textDay5, R.id.textDay6, R.id.textDay7);
    private static final List<Integer> dateIdList = Arrays.asList(R.id.textDate1, R.id.textDate2,
            R.id.textDate3, R.id.textDate4, R.id.textDate5, R.id.textDate6, R.id.textDate7);
    private static final List<Integer> eventIdList = Arrays.asList(R.id.textEvent1, R.id.textEvent2,
            R.id.textEvent3, R.id.textEvent4, R.id.textEvent5, R.id.textEvent6, R.id.textEvent7);

    // default values
    private static int alpha = 20;
    private static boolean isClockVisible = false;
    private static int bgColor = Color.LTGRAY;
    private static int clockColor = Color.BLUE;
    private static int dayColor = Color.BLACK;
    private static int dateColor = Color.BLACK;
    private static int eventColor = Color.YELLOW;
    private static int todayColor = Color.BLUE;

    private static void updateUI(Context context, RemoteViews remoteViews) {
        Log.v(TAG, "updateUI()");

        // Update alpha
        // alpha is a scale from 0 to 10 (KEY_SEEK_BAR_MAX), representing 0 to 100%
        // translate that % onto color-alpha of 255 scale
        int effectiveAlpha = 0;
        if (alpha != 0) {
            effectiveAlpha = (255 * alpha) / Constants.KEY_SEEK_BAR_MAX;
        }
        int color = Color.argb(effectiveAlpha, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor));
        Log.v(TAG, "updateBackground(): effectiveAlpha=" + effectiveAlpha + ", color=" + color);

        remoteViews.setInt(R.id.widget_frame, "setBackgroundColor", color);

        // update day-box with name of the day
        List<String> dayList = new ArrayList<>();
        dayList.add(context.getString(R.string.day_monday));
        dayList.add(context.getString(R.string.day_tuesday));
        dayList.add(context.getString(R.string.day_wednesday));
        dayList.add(context.getString(R.string.day_thursday));
        dayList.add(context.getString(R.string.day_friday));
        dayList.add(context.getString(R.string.day_saturday));
        dayList.add(context.getString(R.string.day_sunday));

        // make Monday first day of the week
        Calendar today = Calendar.getInstance();
        today.setFirstDayOfWeek(Calendar.MONDAY);

        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

        // decide the calender window to display
        int delta = 0;
        if (dayOfWeek < Calendar.MONDAY) {
            delta = -6;
        } else if (dayOfWeek > Calendar.MONDAY) {
            delta = Calendar.MONDAY - dayOfWeek;
        }

        // move date to starting of the week
        today.add(Calendar.DAY_OF_WEEK, delta);

        // get all events for this week
        Map<Integer, Set<String>> eventMap = readCalendarEvents(context, today);

        // add seven days of calendar data - Day, Date, Events
        for (int count = 0; count < 7; count++) {
            int idDay = dayIdList.get(count);
            int idDate = dateIdList.get(count);
            int idEvent = eventIdList.get(count);

            StringBuilder builder = new StringBuilder();
            Set<String> eventList = eventMap.get(today.get(Calendar.DAY_OF_YEAR));
            if (eventList != null) {
                int eventCount = 0;
                for (String eventTitle : eventList) {
                    int maxLen = 9;
                    if (eventTitle.length() > maxLen) {
                        eventTitle = eventTitle.substring(0, maxLen);
                    }

                    // limit display to two events per day
                    if (eventCount > 3) {
                        break;
                    }

                    // add line break if multiple events found for same day
                    if (eventCount > 0) {
                        builder.append("\n");
                    }
                    builder.append(eventTitle);

                    eventCount++;
                }

                // add place holders for missing events
                if (eventList.size() == 1) {
                    builder.append("\n");
                }
            } else {
                builder.append("");
                builder.append("\n");
            }

            // add data to text-view fields
            remoteViews.setTextViewText(idDay, dayList.get(count));
            remoteViews.setTextViewText(idDate, "" + today.get(Calendar.DAY_OF_MONTH));
            remoteViews.setTextViewText(idEvent, builder.toString());

            // add color to text-view fields
            if (today.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                // highlight current day
                remoteViews.setTextColor(idDay, todayColor);
                remoteViews.setTextColor(idDate, todayColor);
                remoteViews.setTextColor(idEvent, todayColor);
            } else {
                remoteViews.setTextColor(idDay, dayColor);
                remoteViews.setTextColor(idDate, dateColor);
                remoteViews.setTextColor(idEvent, eventColor);
            }

            // update Clock color
            remoteViews.setTextColor(R.id.clock, clockColor);
            remoteViews.setInt(R.id.clock, "setVisibility", isClockVisible ? View.VISIBLE : View.INVISIBLE);

            // increment date
            today.add(Calendar.DAY_OF_WEEK, 1);
        }

        // add click listener for the whole widget
        // will get notified at 'onReceive'
        Intent intent = new Intent(context, CalendarWidget.class);
        intent.setAction(Constants.ACTION_SHOW_CALENDAR);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_frame, pendingIntent);
    }

    private static Map<Integer, Set<String>> readCalendarEvents(Context context, Calendar filterDay) {
        Map<Integer, Set<String>> retMap = new HashMap<>();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return retMap;
        }

        Map<String, String> eventNameMap = new HashMap<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri eventUri = CalendarContract.Events.CONTENT_URI;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        String[] instColumns = { CalendarContract.Instances.EVENT_ID };
        String[] columnNames = new String[] { CalendarContract.Events.TITLE };
        String selection = CalendarContract.Events._ID + " = ? ";

        Log.v(TAG, "readCalendarEvents(): filterDay=" + filterDay.get(Calendar.DAY_OF_YEAR));
        for (int delta = 0; delta < 7; delta++) {
            startTime.set(filterDay.get(Calendar.YEAR), filterDay.get(Calendar.MONTH), filterDay.get(Calendar.DAY_OF_MONTH) + delta, 0, 0, 0);
            endTime.set(startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            int instanceDay = startTime.get(Calendar.DAY_OF_YEAR);

            // fetch Instances form table for one day
            Uri.Builder intentUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(intentUriBuilder, startTime.getTimeInMillis());
            ContentUris.appendId(intentUriBuilder, endTime.getTimeInMillis());

            Cursor instCursor = contentResolver.query(intentUriBuilder.build(), instColumns, null, null, null);
            Log.v(TAG, "readCalendarEvents(): instanceDay=" + instanceDay + ", instanceCount=" + instCursor.getCount());

            while (instCursor.moveToNext()) {
                String eventId = instCursor.getString(instCursor.getColumnIndex(CalendarContract.Instances.EVENT_ID));
                String[] selectionArgs = new String[]{eventId};

                // fetch event name from table and memoize it
                if (eventNameMap.get(eventId) == null) {
                    Cursor eventCursor = contentResolver.query(eventUri, columnNames, selection, selectionArgs, null);
                    int count = eventCursor.getCount();
                    if (eventCursor.moveToFirst()) {
                        String eventTitle = eventCursor.getString( eventCursor.getColumnIndex(CalendarContract.Reminders.TITLE) );
                        eventNameMap.put(eventId, eventTitle);
                    }
                    eventCursor.close();
                }
                Log.v(TAG, "readCalendarEvents(): ------- eventId=" + eventId + ", eventTitle=" +  eventNameMap.get(eventId));

                Set<String> titleList = retMap.get(instanceDay);
                if (titleList == null) {
                    titleList = new HashSet<>();
                    retMap.put(instanceDay, titleList);
                }
                titleList.add(eventNameMap.get(eventId));

            }
            instCursor.close();
        }

        Log.v(TAG, "readCalendarEvents(): retMap=" + retMap);
        return retMap;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate(): appWidgetIds.length=" + appWidgetIds.length);

        readSettings(context);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.v(TAG, "onUpdate(): appWidgetId=" + appWidgetId);

            // initializing widget layout
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            updateUI(context, remoteViews);

            // Notify widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v(TAG, "onReceive(): action=" + intent.getAction());

        if (Constants.ACTION_SHOW_CALENDAR.equals(intent.getAction())) {
            // bring up System Calendar
            ComponentName componentName = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
            Intent calIntent = new Intent();
            calIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            calIntent.setComponent(componentName);
            context.startActivity(calIntent);

        } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())
                || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
                || Constants.ACTION_UI_REFRESH_HOURLY.equals(intent.getAction())
                || Constants.ACTION_SETTINGS_REFRESH.equals(intent.getAction())) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            readSettings(context);

            // re-build UI
            updateUI(context, remoteViews);

            // Instruct the widget manager to update the widget
            ComponentName calendarWidget = new ComponentName(context, CalendarWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(calendarWidget, remoteViews);
        }
    }

    private void readSettings(Context context) {

        Map<String, Integer> settingsMap = PersistenceManager.readSettings(context);
        alpha = settingsMap.get(Constants.KEY_ALPHA);
        isClockVisible = settingsMap.get(Constants.KEY_CLOCK_CHECKED) > 0 ? true : false;
        bgColor = settingsMap.get(Constants.KEY_BG_COLOR);
        clockColor = settingsMap.get(Constants.KEY_CLOCK_COLOR);
        dayColor = settingsMap.get(Constants.KEY_DAY_COLOR);
        dateColor = settingsMap.get(Constants.KEY_DATE_COLOR);
        eventColor = settingsMap.get(Constants.KEY_EVENT_COLOR);
        todayColor = settingsMap.get(Constants.KEY_TODAY_COLOR);
    }
}
