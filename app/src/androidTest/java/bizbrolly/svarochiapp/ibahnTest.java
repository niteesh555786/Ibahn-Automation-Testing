package bizbrolly.svarochiapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import bizbrolly.svarochiapp.activities.ControllerActivity;
import bizbrolly.svarochiapp.activities.MainActivity;
import bizbrolly.svarochiapp.activities.TestActivity;
import bizbrolly.svarochiapp.ibahn_logic.Preferences;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static bizbrolly.svarochiapp.activities.TestActivity.MY_PREFS_NAME;
import static com.raizlabs.android.dbflow.config.FlowManager.getContext;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static android.support.test.runner.lifecycle.Stage.RESUMED;

/**
 * Created by Jaadugar on 6/30/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ibahnTest {
    public static final String MY_EMAIL = "abcd@abcd.com";
    public static final String MY_PASSWORD = "ok1234";
    public static final String MY_PREFS_NAME = "PASSWORD_SAVED_STATUS";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    private MainActivity mActivity = null;

    @Before
    public void setActivity() {
        mActivity = mActivityRule.getActivity();
    }

    private ScanWaiter scanWaiter;
    private void waitForScan(){
        scanWaiter = new ScanWaiter();
        Espresso.registerIdlingResources(scanWaiter);
    }
    //Home Screen
    @Test
    public void t2HomeTest() throws Exception {
        //waiting for the Discovering progress bar
        /*IdlingResource idlingResource_discover = new DialogFragmentIdlingResource(
                mActivityRule.getActivity().getSupportFragmentManager(),"Discovering");
        Espresso.registerIdlingResources(idlingResource_discover);
        Espresso.unregisterIdlingResources(idlingResource_discover);*/

        /*//waiting for the connecting progress bar
        IdlingResource idlingResource = new DialogFragmentIdlingResource(
                mTestActivityRule.getActivity().getSupportFragmentManager(),"Connecting");
        Espresso.registerIdlingResources(idlingResource);
        Espresso.unregisterIdlingResources(idlingResource);*/

        //Password Screen
        //TestActivity tActivity= new TestActivity();
        //SharedPreferences sp = tActivity.getSharedPreferences(MY_PREFS_NAME, Activity.MODE_PRIVATE);
        //int restoredValue = sp.getInt("pStatus", 999);

        //SystemClock.sleep(20000);
        /*Log.e("Test", "Before scan");
        waitForScan();
        Log.e("Test", "After scan");
        Espresso.unregisterIdlingResources(scanWaiter);*/

        if (!Preferences.getInstance(mActivityRule.getActivity()).isPasswordSaved()) {
            onView(withId(R.id.email_edit_text))
                    .perform(typeText(MY_EMAIL), closeSoftKeyboard());
            onView(withId(R.id.password_edit_text))
                    .perform(typeText(MY_PASSWORD), closeSoftKeyboard());
            onView(withId(R.id.confirm_password_edit_text))
                    .perform(typeText(MY_PASSWORD), closeSoftKeyboard());
            onView(withId(R.id.show_password_switch))
                    .perform(click());

            //done
            /*onView(withId(R.id.setSystem_toolbar_id))
                    .perform(click());
            openContextualActionModeOverflowMenu();
            onView(withText("Done"))
                    .perform(click());*/
            onView(withId(R.id.setup_password_done_button))
                    .perform(click());
        }



        /*
        onView(withId(R.id.password_id))
                .perform(typeText(MY_USERNAME),closeSoftKeyboard());
        onView(withId(R.id.confirm_password_id))
                .perform(typeText(MY_PASSWORD),closeSoftKeyboard());
        onView(withId(R.id.show_password_switch))
                .perform(click());
        onView(withId(R.id.confirm_password_id))
                .check(matches(isDisplayed()));

        //done
        onView(withId(R.id.setSystem_toolbar_id))
                .perform(click());
        openContextualActionModeOverflowMenu();
        onView(withText("Done"))
                .perform(click());*/
            //onView(allOf(withText("Bright & Dim"), hasSibling(withText("Warm & Cool"))))
            //        .perform(click());
        /*SystemClock.sleep(15000);
        //onView(withText("Bright & Dim"))
        //       .perform(click());*/


        /*onView(withText("Bright & Dim")).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        SystemClock.sleep(15000);
        onView(withText("Bright & Dim")).perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));*/
//        onView(withText("Bright & Dim 1"))
//                .perform(click());
            //SystemClock.sleep(20000);
            /*SystemClock.sleep(10000);
            onView(withIndex(withText("Bright & Dim"), 0)).perform(click());
            SystemClock.sleep(20000);
            onView(withIndex(withText("Bright & Dim 1"), 0)).perform(click());*/
        //Associating BnD
        /*SystemClock.sleep(20000);
        SystemClock.sleep(5000);
        onView(withIndexBnD(withText("Bright & Dim"), 0)).perform(click());
        SystemClock.sleep(20000);
        onView(withIndexBnD(withText("Bright & Dim"), 1)).perform(click());
        SystemClock.sleep(20000);
        onView(withIndexBnD(withText("Bright & Dim"), 2)).perform(click());

        //Associating WnC
        SystemClock.sleep(20000);
        onView(withIndexWnC(withText("Warm & Cool"), 0)).perform(click());
        SystemClock.sleep(20000);
        onView(withIndexWnC(withText("Warm & Cool"), 1)).perform(click());
        SystemClock.sleep(20000);
        onView(withIndexWnC(withText("Warm & Cool"), 2)).perform(click());

        //Associating RGBW
        SystemClock.sleep(20000);
        onView(withIndexRGBW(withText("RGBW"), 0)).perform(click());
        SystemClock.sleep(20000);
        onView(withIndexRGBW(withText("RGBW"), 1)).perform(click());
        SystemClock.sleep(20000);*/
        if(Preferences.getInstance(mActivityRule.getActivity()).doesFetchPreviousState()){
            openContextualActionModeOverflowMenu();
            SystemClock.sleep(2000);
            onView(withText("Disable Fetch State"))
                    .perform(click());
            SystemClock.sleep(2000);
        }





        //Create Bright & Dim group
        onView(withId(R.id.add_group_button))
                .perform(click());
        SystemClock.sleep(2000);
        onView(allOf(withClassName(endsWith("EditText")))).perform(replaceText("All BnD"), closeSoftKeyboard());
        /*onView(withText("Enter Group Name"))
                .perform(typeText("All BnD"),closeSoftKeyboard());*/
        SystemClock.sleep(2000);
        onView(withText("Add")).perform(click());

        //Create Warm & Cool group
        onView(withId(R.id.add_group_button))
                .perform(click());
        SystemClock.sleep(2000);
        onView(allOf(withClassName(endsWith("EditText")))).perform(replaceText("All WnC"), closeSoftKeyboard());
       /* onView(withText("Enter Group Name"))
                .perform(typeText("All WnC"),closeSoftKeyboard());*/
        SystemClock.sleep(2000);
        onView(withText("Add")).perform(click());

        //Create RGBW group
        onView(withId(R.id.add_group_button))
                .perform(click());
        SystemClock.sleep(2000);
        onView(allOf(withClassName(endsWith("EditText")))).perform(replaceText("All RGBW"), closeSoftKeyboard());
        /*onView(withText("Enter Group Name"))
                .perform(typeText("All RGBW"),closeSoftKeyboard());*/
        SystemClock.sleep(2000);
        onView(withText("Add")).perform(click());

        //Add lamps in the groups
        SystemClock.sleep(30000);

        //Controlling individually
        /*SystemClock.sleep(20000);
        onView(withIndex(withText("Bright & Dim 1"), 0)).perform(click());

        /*SystemClock.sleep(10000);
        onView(withIndex(withText("Warm & Cool"), 0)).perform(click());
        SystemClock.sleep(20000);
        onView(withIndex(withText("Warm & Cool 1"), 0)).perform(click());*/



            /*onView(withId(R.id.power_switch))
                    .perform(click());//OFF*/
           /* SystemClock.sleep(4000);
            onView(withId(R.id.power_switch))
                    .perform(click());//ON
        SystemClock.sleep(4000);
        onView(withId(R.id.power_switch))
                .perform(click());//ON
        SystemClock.sleep(4000);
        onView(withId(R.id.power_switch))
                .perform(click());//ON*/




            //onView(withContentDescription("Navigate up")).perform(click());
            //SystemClock.sleep(2000);
            //onView(withIndex(withText("Bright & Dim 1"), 0)).perform(click());
            //SystemClock.sleep(5000);
             //onView(withId(R.id.power_switch))
             //   .perform(click());//ON
            onView(withText("All BnD")).perform(click());
            //POWER ON & 0FF
            SystemClock.sleep(5000);
            onView(withId(R.id.power_switch)).perform(click());//ON
            SystemClock.sleep(4000);
            onView(withId(R.id.power_switch)).perform(click());//ON
            SystemClock.sleep(4000);
            onView(withId(R.id.power_switch)).perform(click());//ON
            SystemClock.sleep(4000);
            //INTENSITY TESTING IN STEPS OF 20
            onView(withId(R.id.light_intensity_slider)).perform(setProgress(20));
            SystemClock.sleep(2000);
            onView(withId(R.id.light_intensity_slider)).perform(setProgress(40));
            SystemClock.sleep(2000);
            onView(withId(R.id.light_intensity_slider)).perform(setProgress(60));
            SystemClock.sleep(2000);
            onView(withId(R.id.light_intensity_slider)).perform(setProgress(80));
            SystemClock.sleep(2000);
            onView(withId(R.id.light_intensity_slider)).perform(setProgress(200));
            SystemClock.sleep(2000);
            onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
            SystemClock.sleep(5000);
            onView(withContentDescription("Navigate up")).perform(click());
            SystemClock.sleep(5000);

        //All WnC Lamps
            onView(withText("All WnC")).perform(click());
            //POWER ON & 0FF
            SystemClock.sleep(5000);
            onView(withId(R.id.power_switch)).perform(click());//ON
            SystemClock.sleep(4000);
            onView(withId(R.id.power_switch)).perform(click());//ON
            SystemClock.sleep(4000);
            onView(withId(R.id.power_switch)).perform(click());//ON
            SystemClock.sleep(4000);
            //INTENSITY TESTING IN STEPS OF 20
            onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
            onView(withId(R.id.tunnable_slider)).perform(setProgress(100));
            SystemClock.sleep(10000);

            onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
            onView(withId(R.id.tunnable_slider)).perform(setProgress(0));
            SystemClock.sleep(10000);

            onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
            onView(withId(R.id.tunnable_slider)).perform(setProgress(255));
            SystemClock.sleep(10000);
            onView(withContentDescription("Navigate up")).perform(click());
            SystemClock.sleep(5000);

        //All RGBW Lamps
        onView(withText("All RGBW")).perform(click());
        //POWER ON & 0FF
        SystemClock.sleep(5000);
        onView(withId(R.id.power_switch)).perform(click());//ON
        SystemClock.sleep(4000);
        onView(withId(R.id.power_switch)).perform(click());//ON
        SystemClock.sleep(4000);
        onView(withId(R.id.power_switch)).perform(click());//ON
        SystemClock.sleep(4000);
        //INTENSITY TESTING IN STEPS OF 20
        onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
        onView(withId(R.id.tunnable_slider)).perform(setProgress(100));
        SystemClock.sleep(10000);

        onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
        onView(withId(R.id.tunnable_slider)).perform(setProgress(0));
        SystemClock.sleep(10000);

        onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
        onView(withId(R.id.tunnable_slider)).perform(setProgress(255));
        SystemClock.sleep(10000);

        onView(withId(R.id.color_picker_button)).perform(click());
        SystemClock.sleep(2000);

        onView(withId(com.azeesoft.lib.colorpicker.R.id.satValBox)).perform(swipeUp());


        for(int i=0;i<358;i++){
            onView(withId(com.azeesoft.lib.colorpicker.R.id.hueBar)).perform(setProgress(i));
            SystemClock.sleep(250);
        }

        onView(withId(R.id.cancelButton)).perform(click());
        SystemClock.sleep(4000);

        onView(withContentDescription("Navigate up")).perform(click());
        SystemClock.sleep(5000);

            //Tunnable Slider
                //Cool White

        /*onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
        onView(withId(R.id.tunnable_slider)).perform(setProgress(100));
        SystemClock.sleep(10000);

        onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
        onView(withId(R.id.tunnable_slider)).perform(setProgress(0));
        SystemClock.sleep(10000);

        onView(withId(R.id.light_intensity_slider)).perform(setProgress(255));
        onView(withId(R.id.tunnable_slider)).perform(setProgress(255));
        SystemClock.sleep(10000);

        onView(withId(R.id.color_picker_button)).perform(click());
        SystemClock.sleep(2000);

        onView(withId(com.azeesoft.lib.colorpicker.R.id.satValBox)).perform(swipeUp());


        /*onView(withId(com.azeesoft.lib.colorpicker.R.id.val1)).perform(typeText("255"));
        onView(withId(com.azeesoft.lib.colorpicker.R.id.val2)).perform(typeText("0"));
        onView(withId(com.azeesoft.lib.colorpicker.R.id.val3)).perform(typeText("0"));
        SystemClock.sleep(5000);*/


        /*for(int i=0;i<358;i++){
            onView(withId(com.azeesoft.lib.colorpicker.R.id.hueBar)).perform(setProgress(i));
            SystemClock.sleep(250);
        }


        onView(withId(R.id.cancelButton)).perform(click());
        SystemClock.sleep(4000);


            //Going back to Home Screen
            onView(withContentDescription("Navigate up")).perform(click());*/


            //Grouping
            /*onView(withId(R.id.add_group_button))
                    .perform(click());
            SystemClock.sleep(2000);
            onView(allOf(withClassName(endsWith("EditText")))).perform(replaceText("All BnD"), closeSoftKeyboard());
            //onView(withText("Enter Group Name"))
            //        .perform(typeText("All BnD"),closeSoftKeyboard());
            SystemClock.sleep(2000);
            onView(withText("Add"))
                    .perform(click());*/
            //X of origin
            //onView(withText("ADD"))
            //        .inRoot(isDialog()) // <---
            //        .check(matches(isDisplayed()))
            //        .perform(click());
            //onView(allOf(withClassName(equalTo("AlertDialog")),withText(is("ADD")),hasSibling(withText("CANCEL")))).perform(click());
        /*UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());*/
            /*mActivityRule.getActivity().findViewById(R.id.background_layout);*/

        // Search for correct button in the dialog.
        /*UiObject button = uiDevice.findObject(new UiSelector().text("ADD"));
        button.click();*/
        /*if (button.exists() && button.isEnabled()) {
            button.click();*/

            //onView(withText("Bright & Dim 1"))
            //        .perform(longClick());
            //SystemClock.sleep(2000);
            //onView(withText("Bright & Dim 1"))
            //        .perform(drag(184, 359,368,1077));
            //SystemClock.sleep(5000);
            /*onView(withText("All BnD"))
                    .perform(click());


            //onView(withText("Bright & Dim 1"))
            //        .perform(drag());

            /*SystemClock.sleep(2000);
            onView(withContentDescription("Navigate up")).perform(click());
            SystemClock.sleep(2000);
            onView(withText("All BnD"))
                    .perform(doubleClick());
            SystemClock.sleep(2000);
            onView(withText("Delete"))
                    .perform(click());*/


            //onView(withText("Bright & Dim 1") v).perform(drag());



        //Disassociating
        //SystemClock.sleep(2000);
        //onView(withText("Bright & Dim 1"))
        //        .perform(doubleClick());
        //Dissociating BnD
        /*onView(withText("RGBW 1"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Disassociate"))
                .perform(click());
        SystemClock.sleep(2000);
        /*onView(withText("Yes"))
                .perform(click());*/

        /*onView(withText("Bright & Dim 2"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Disassociate"))
                .perform(click());
        SystemClock.sleep(2000);

        onView(withText("Bright & Dim 3"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Disassociate"))
                .perform(click());
        SystemClock.sleep(2000);

        //Dissociating Warm n Cool
        onView(withText("Warm & Cool 4"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Disassociate"))
                .perform(click());
        SystemClock.sleep(2000);

        onView(withText("Warm & Cool 5"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Disassociate"))
                .perform(click());
        SystemClock.sleep(2000);

        onView(withText("Warm & Cool 6"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Disassociate"))
                .perform(click());
        SystemClock.sleep(2000);

        onView(withText("RGBW 7"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Disassociate"))
                .perform(click());
        SystemClock.sleep(2000);

        //Deleting Groups

        //BnD
        onView(withText("All BnD"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Delete"))
                .perform(click());

        //WnC
        onView(withText("All WnC"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Delete"))
                .perform(click());

        //RGBW
        onView(withText("All RGBW"))
                .perform(doubleClick());
        SystemClock.sleep(2000);
        onView(withText("Delete"))
                .perform(click());*/
        //YES OPTION
        // Initialize UiDevice instance
        /*UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Search for correct button in the dialog.
        UiObject button = uiDevice.findObject(new UiSelector().text("yes"));
        button.click();
        if (button.exists() && button.isEnabled()) {
            button.click();
        }
        SystemClock.sleep(2000);*/
        /*onView(withText("Yes"))
                .perform(click());*/

        }


    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(SeekBar.class);
            }
        };
    }

    public static ViewAction DragMeDown() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.BOTTOM_CENTER,
                GeneralLocation.TOP_CENTER, Press.FINGER);
    }

    public static ViewAction drag(int startX, int startY, int endX, int endY) {
        return new GeneralSwipeAction(
                Swipe.FAST,
                new CustomisableCoordinatesProvider(startX, startY),
                new CustomisableCoordinatesProvider(endX, endY),
                Press.FINGER);
    }
    /*public static int drag(Instrumentation inst, float fromX, float toX, float fromY,
                            float toY, int stepCount) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        float y = fromY;
        float x = fromX;

        float yStep = (toY - fromY) / stepCount;
        float xStep = (toX - fromX) / stepCount;

        MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        inst.sendPointerSync(event);
        for (int i = 0; i < stepCount; ++i) {
            y += yStep;
            x += xStep;
            eventTime = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, 0);
            inst.sendPointerSync(event);
        }

        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();
    }*/

    public static Matcher<View> withIndexBnD(final Matcher<View> matcher, final int indexBnD) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("Bright & Dim");
                description.appendValue(indexBnD);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == indexBnD;
            }
        };
    }

    public static ViewAction swipeUp() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_LEFT,
                GeneralLocation.TOP_RIGHT, Press.FINGER);
    }

    public static Matcher<View> withIndexWnC(final Matcher<View> matcher, final int indexWnC) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("Warm & Cool");
                description.appendValue(indexWnC);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == indexWnC;
            }
        };
    }

    public static Matcher<View> withIndexRGBW(final Matcher<View> matcher, final int indexRGBW) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("RGBW");
                description.appendValue(indexRGBW);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == indexRGBW;
            }
        };
    }

    public static Matcher<View> withIndexAvoiAmbiguiutyBnD(final Matcher<View> matcher, final int indexBnD) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(indexBnD);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == indexBnD;
            }
        };
    }

    public static Matcher<View> withIndexAvoiAmbiguiutyWnC(final Matcher<View> matcher, final int indexWnC) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(indexWnC);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == indexWnC;
            }
        };
    }

    public static Matcher<View> withIndexAvoiAmbiguiutyRGBW(final Matcher<View> matcher, final int indexRGBW) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(indexRGBW);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == indexRGBW;
            }
        };
    }


}
