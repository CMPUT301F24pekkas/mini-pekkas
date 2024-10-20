package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static
        androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A simple UI test, delete when we have actual UI tests
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleUITest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Test the navigation bar functionality
     */
    @Test
    public void testUserNavigation() {
        onView(withId(R.id.navigation_event)).perform(click());
        onView(withId(R.id.navigation_camera)).perform(click());
        onView(withId(R.id.navigation_notifications)).perform(click());
        onView(withId(R.id.navigation_profile)).perform(click());
        onView(withId(R.id.navigation_home)).perform(click());

        // Add any checks to see if we're at home screen
        // ...
    }
}
