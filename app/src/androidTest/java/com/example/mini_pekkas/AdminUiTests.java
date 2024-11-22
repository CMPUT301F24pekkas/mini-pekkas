package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AdminUiTests {
    @Rule
    public ActivityScenarioRule<AdminActivity> scenario = new
            ActivityScenarioRule<>(AdminActivity.class);
    public Firebase firebaseHelper;
    public String deviceId;
    public Event testEvent;

    /**
     * Creates a test profile for the admin. Giving it the right permission to access the right UI
     * @param context The context of the activity
     */
    public void CreateTestProfile(Context context) {
        Firebase firebaseHelper = new Firebase(context);
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Make sure the document exist, create if not
        firebaseHelper.isThisUserAdmin(exist -> {
            if (!exist) {
                // Manually add myself to the admin collection
                Map<String, Object> adminDoc = new HashMap<>();
                adminDoc.put("name", "Android Unit Testing");
                adminDoc.put("deviceID", deviceId);
                FirebaseFirestore.getInstance().collection("admins").add(adminDoc);
            }
        });
    }

    /**
     * Creates a test event for the admin to use when testing
     */
    public void CreateTestEvent() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Test Event");
        map.put("location", "Test Location");
        map.put("description", "Test Description");
        map.put("date", "Test Date");
        map.put("time", "Test Time");
        map.put("maxAttendees", 100);
        map.put("QrCode", "Test QR Code");
        map.put("geo", true);
        map.put("facilityGeoLat", 100.0);
        map.put("facilityGeoLong", 100.0);
        map.put("price", 100.0);
        map.put("attendees", new ArrayList<>());
        map.put("waitlist", new ArrayList<>());
        map.put("eventHost", new User());
        //map.put("id", "Test ID");
        map.put("posterUrl", null);
        map.put("facility", "Test Facility");
        testEvent = new Event(map);

        firebaseHelper.addEvent(testEvent); // Id is updated in here
    }

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        CreateTestProfile(context);
        CreateTestEvent();
        // TODO add a poster to browse and delete
        onView(withId(R.id.adminBrowseEvents)).perform(click());
    }

    /**
     * US 03.04.01 - Browse events as an organizer
     */
    @Test
    public void testBrowseEvents() {
        onView(withId(R.id.adminBrowseEvents)).perform(click());
        onView(withId(R.id.admin_search_events)).perform(typeText("Test Event"));
        onView(withText("Test Event")).check(matches(isDisplayed()));
    }

    /**
     * Delete all test documents after the test, even if any tests fail
     */
    @After
    public void cleanUp() {
        firebaseHelper.deleteEvent(testEvent);
    }
}
