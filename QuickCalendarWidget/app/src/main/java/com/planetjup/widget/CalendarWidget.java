package com.planetjup.widget;

import android.Manifest;
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
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.planetjup.widget.util.Constants;
import com.planetjup.widget.util.PersistenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class CalendarWidget extends AppWidgetProvider {

    private static final String TAG = CalendarWidget.class.getSimpleName();

    // default values
    private static int alpha = 20;
    private static int bgColor = Color.LTGRAY;
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
        Log.v(TAG, "updateBackground(): effectiveAlpha=" + effectiveAlpha);

        int color = Color.argb(effectiveAlpha, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor));

        remoteViews.setInt(R.id.day1, "setBackgroundColor", color);
        remoteViews.setInt(R.id.day2, "setBackgroundColor", color);
        remoteViews.setInt(R.id.day3, "setBackgroundColor", color);
        remoteViews.setInt(R.id.day4, "setBackgroundColor", color);
        remoteViews.setInt(R.id.day5, "setBackgroundColor", color);
        remoteViews.setInt(R.id.day6, "setBackgroundColor", color);
        remoteViews.setInt(R.id.day7, "setBackgroundColor", color);

        // Update text color and events
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
        Map<Integer, List<String>> eventMap = readCalendarEvents(context, today);

        // add seven days of calendar data - Day, Date, Events
        for (int count = 0; count < dayList.size(); count++) {
            int idDay = idList.get(count);
            int idDate = idList.get(count + 7);
            int idEvent = idList.get(count + 14);

            StringBuilder builder = new StringBuilder();
            List<String> eventList = eventMap.get(today.get(Calendar.DAY_OF_YEAR));
            if (eventList != null) {
                for (int i = 0; i < eventList.size(); i++) {
                    int maxLen = 9;
                    String eventTitle = eventList.get(i);
                    if (eventTitle.length() > maxLen) {
                        eventTitle = eventTitle.substring(0, maxLen);
                    }

                    // limit display to two events per day
                    if (i > 2) {
                        break;
                    }

                    // add line break if multiple events found for same day
                    if (i > 0) {
                        builder.append("\n");
                    }
                    builder.append(eventTitle);
                }

                // add place holders for missing events
                if (eventList.size() == 1) {
                    builder.append("\n");
                }
            } else {
                builder.append("");
                builder.append("\n");
            }

            // add data to UI
            remoteViews.setTextViewText(idDay, dayList.get(count));
            remoteViews.setTextViewText(idDate, "" + today.get(Calendar.DAY_OF_MONTH));
            remoteViews.setTextViewText(idEvent, builder.toString());

            // color settings
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

    private static Map<Integer, List<String>> readCalendarEvents(Context context, Calendar filterDay) {
        Map<Integer, List<String>> retMap = new HashMap<>();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return retMap;
        }

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

        // search range - start of given day to start of next day
        Calendar startTime = Calendar.getInstance();
        startTime.set(filterDay.get(Calendar.YEAR), filterDay.get(Calendar.MONTH), filterDay.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Log.v(TAG, "readCalendarEvents(): startTime=" + startTime.getTime());

        Calendar endTime = Calendar.getInstance();
        endTime.set(filterDay.get(Calendar.YEAR), filterDay.get(Calendar.MONTH), filterDay.get(Calendar.DAY_OF_MONTH) + 7, 0, 0, 0);
        Log.v(TAG, "readCalendarEvents(): endTime=" + endTime.getTime());

        // 1. Events
        ContentResolver contentResolver = context.getContentResolver();
        Uri eventUri = CalendarContract.Events.CONTENT_URI;
        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))";

        Log.v(TAG, "readCalendarEvents(): selection=" + selection);

        String[] columnNames = new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};

        //Cursor cursor = contentResolver.query(eventUri, columnNames, selection, null, null);
        Cursor cursor = contentResolver.query(eventUri, columnNames, null, null, null);
        boolean isMoved = cursor.moveToFirst();
        Log.v(TAG, "readCalendarEvents(): Events.isMoved=" + isMoved);

        for (int i = 0; i < cursor.getCount(); i++) {
            String eventTitle = cursor.getString(0);
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTimeInMillis(cursor.getLong(1));
            Integer eventDayOfYear = eventDate.get(Calendar.DAY_OF_YEAR);
            String eventDateString = formatter.format(eventDate.getTime());

            Log.v(TAG, "readCalendarEvents(): Event.title=" + eventTitle + ", " + eventDateString + ", " + eventDayOfYear);

            List<String> titleList = retMap.get(eventDayOfYear);
            if (titleList == null) {
                titleList = new ArrayList<>();
                retMap.put(eventDayOfYear, titleList);
            }
            titleList.add(eventTitle);

            cursor.moveToNext();
        }
        cursor.close();


        Log.v(TAG, "readCalendarEvents(): retMap=" + retMap);
        return retMap;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate(): appWidgetIds.length=" + appWidgetIds.length);

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
                || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
                || Constants.ACTION_UI_REFRESH.equals(intent.getAction())
                || Constants.ACTION_UI_REFRESH_HOURLY.equals(intent.getAction())
                || Constants.ACTION_SETTINGS_REFRESH.equals(intent.getAction())) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            Map<String, Integer> settingsMap = PersistenceManager.readSettings(context);
            alpha = settingsMap.get(Constants.KEY_ALPHA);
            bgColor = settingsMap.get(Constants.KEY_BG_COLOR);
            dayColor = settingsMap.get(Constants.KEY_DAY_COLOR);
            dateColor = settingsMap.get(Constants.KEY_DATE_COLOR);
            eventColor = settingsMap.get(Constants.KEY_EVENT_COLOR);
            todayColor = settingsMap.get(Constants.KEY_TODAY_COLOR);

            // re-build UI
            updateUI(context, remoteViews);

            // Instruct the widget manager to update the widget
            ComponentName calendarWidget = new ComponentName(context, CalendarWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(calendarWidget, remoteViews);
        }
    }
}
