package com.planetjup.dnd;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by summani on 1/24/18.
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DndPopupActivityTest {

    private static final String PACKAGE_NAME = "com.android.phone";


    @Rule
    public IntentsTestRule<DndPopupActivity> rule = new IntentsTestRule<>(DndPopupActivity.class);


    @Before
    public void setUp() throws Exception {

        // stub calls to service by redirecting the Intent here
        Intent resultData = new Intent();
        intending(hasAction(DndTileService.ACTION_START_TIMER)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
        intending(hasAction(DndTileService.ACTION_MUTE_RINGER)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
        intending(hasAction(DndTileService.ACTION_MUTE_MUSIC)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
        intending(hasAction(DndTileService.ACTION_MUTE_ALARM)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
        intending(toPackage("com.planetjup.dnd")).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Before
    public void grantPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS");
        }
    }


    @Test
    public void testStructure()
    {
        DndPopupActivity activity = rule.getActivity();

        // 1. test Mode radio-group
        RadioGroup radioGroupMode = activity.findViewById(R.id.radio_group_mode);
        assertEquals(radioGroupMode.getChildCount(), 3);
        assertTrue(radioGroupMode.getChildAt(0).isSelected());

        // 2. test Time radio-group
        RadioGroup radioGroupTime = activity.findViewById(R.id.radio_group_time);
        assertEquals(radioGroupTime.getChildCount(), 4);
        assertFalse(radioGroupTime.getChildAt(0).isSelected());
        assertFalse(radioGroupTime.getChildAt(1).isSelected());
        assertFalse(radioGroupTime.getChildAt(2).isSelected());

        // 3. test seek bar text
        TextView textViewSeek = activity.findViewById(R.id.textViewSeek);
        assertEquals(textViewSeek.getText(), activity.getString(R.string.Min_0));

        // 4. test SeekBar
        SeekBar seekBar = activity.findViewById(R.id.seekBar);
        assertEquals(seekBar.getProgress(), 0);
    }

    @Test
    public void testOnClick_radio_button_15()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.radio_15)).perform(click());

        intended(allOf(
                hasAction(DndTileService.ACTION_START_TIMER)
                ));
    }

    @Test
    public void testOnClick_radio_button_30()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.radio_30)).perform(click());
    }

    @Test
    public void testOnClick_radio_button_60()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.radio_60)).perform(click());
    }

    @Test
    public void testOnClick_radio_button_infinity()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.radio_infinity)).perform(click());
    }

    @Test
    public void testOnClick_radio_button_go()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.seekBar)).perform(click());
        onView(withId(R.id.buttonGo)).perform(click());
    }

    @Test
    public void testOnClick_radio_button_ringer()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.buttonRinger)).perform(click());
    }

    @Test
    public void testOnClick_radio_button_music()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.buttonMusic)).perform(click());
    }


    @Test
    public void testOnClick_radio_button_alarm()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.buttonAlarm)).perform(click());
    }

    @Test
    public void testGetInterruptionMode()
    {
        DndPopupActivity activity = rule.getActivity();
        RadioGroup radioGroupMode = activity.findViewById(R.id.radio_group_mode);

        onView(withId(R.id.radio_total)).perform(click());
        assertEquals(activity.getInterruptionMode(), NotificationManager.INTERRUPTION_FILTER_NONE);

        onView(withId(R.id.radio_priority)).perform(click());
        assertEquals(activity.getInterruptionMode(), NotificationManager.INTERRUPTION_FILTER_PRIORITY);

        onView(withId(R.id.radio_alarm)).perform(click());
        assertEquals(activity.getInterruptionMode(), NotificationManager.INTERRUPTION_FILTER_ALARMS);
    }

    @Test
    public void testGetSeekProgress()
    {
        DndPopupActivity activity = rule.getActivity();
        onView(withId(R.id.seekBar)).perform(click());

        SeekBar seekBar = activity.findViewById(R.id.seekBar);
        int progress = seekBar.getProgress();
        String progressStr = Integer.toString(progress);

        assertEquals(activity.getSeekProgress(), progress);

        TextView textViewSeek = activity.findViewById(R.id.textViewSeek);
        String temp = textViewSeek.getText().subSequence(0,2).toString();
        assertTrue(temp.equals(progressStr));
    }
}
