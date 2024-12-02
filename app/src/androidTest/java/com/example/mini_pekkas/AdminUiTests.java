package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

import android.content.Context;
import android.provider.Settings;
import android.view.KeyEvent;

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
    public Event testEvent;
    public User testUser;

    /**
     * Creates a test profile for the admin. Giving it the right permission to access the right UI
     * @param context The context of the activity
     */
    public void CreateTestProfile(Context context) {
        this.firebaseHelper = new Firebase(context);
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

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
     * Creates a test user object for the admin to use when testing
     */
    public void CreateTestUser() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Android Unit Testing");
        map.put("lastname", "Testing");
        map.put("email", "william.henry.moody@my-own-personal-domain.com");
        map.put("phone", "1234567890");
        map.put("profilePicture", "");
        map.put("facility", "real location");   // This is an organizer
        map.put("deviceID", "");
        this.testUser = new User(map);
        firebaseHelper.InitializeThisUser(testUser, () -> {CreateTestEvent();});
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
        map.put("maxAttendees", (long) 100);
        map.put("QrCode", "Test QR Code");
        map.put("geo", true);
        map.put("facilityGeoLat", (double) 100.0);
        map.put("facilityGeoLong", (double) 100.0);
        map.put("price", (double) 100.10);
        map.put("attendees", new ArrayList<>());
        map.put("waitlist", new ArrayList<>());
        map.put("eventHost", testUser);
        //map.put("id", "Test ID");
        map.put("posterUrl", null);
        map.put("facility", "Test Facility");
        this.testEvent = new Event(map);

        firebaseHelper.addEvent(testEvent);
    }

    @Before
    public void setup() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        CreateTestProfile(context);
        CreateTestUser();   // Which also calls test event

        Thread.sleep(2000); // TODO bad idea but simple way to wait for adding events

        // TODO add a poster to browse and delete
        onView(withId(R.id.adminBrowseEvents)).perform(click());
    }

    /**
     * US 03.04.01 - Browse events as an organizer
     */
    @Test
    public void testBrowseEvents() {
        onView(withId(R.id.adminBrowseEvents)).perform(click());
        // Press enter to search for an event, check if it appears
        onView(withId(R.id.admin_search_events)).perform(typeText("Test Event")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withText("Test Event")).check(matches(isDisplayed()));

        // Search for an non-existent event, check if nothing appears
        onView(withId(R.id.admin_search_events)).perform(typeText("Non-existent &%&@()&++!*+!*()@#$ Event")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withText("Non-existent &%&@()&++!*+!*()@#$ Event")).check(doesNotExist());
    }

    /**
     * Check if browse facility tab works
     */
    @Test
    public void testBrowseFacility() {
        onView(withId(R.id.adminBrowseFacilities)).perform(click());
        onView(withId(R.id.admin_search_facilities)).perform(typeText("Test Facility")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withText("Test Facility")).check(matches(isDisplayed()));
    }

    /**
     * US 03.05.01 - Browse profiles
     * TODO need to make a mock user for this
     */
    @Test
    public void testBrowseProfile() {
        onView(withId(R.id.adminBrowseProfiles)).perform(click());
        onView(withId(R.id.admin_search_profiles)).perform(typeText("Android Unit Testing")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withText("Android Unit Testing")).check(matches(isDisplayed()));
    }

    /**
     * US 03.06.01 As an administrator, I want to be able to browse images.
     */
    @Test
    public void testBrowseImages(){

    }

    /**
     * US 03.01.01 As an administrator, I want to be able to remove events.
     * @throws InterruptedException
     */
    @Test
    public void testDeleteEvent() throws InterruptedException {
        CreateTestEvent();
        Thread.sleep(2000);
        onView(withId(R.id.adminBrowseEvents)).perform(click());
        onView(withId(R.id.admin_search_events)).perform(typeText("Test Event")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        Thread.sleep(1000);

        // Check if clicking no does not delete the event
        onData(hasEntry(equalTo("name"), equalTo("Test Event")))
                .inAdapterView(withId(R.id.adminEventListView)) // Replace with your list view ID
                .atPosition(0) // Specify the position of the item if needed
                .perform(longClick());
        Thread.sleep(1000);
        onView(withText("No")).perform(click());
        onView(withText("Test Event")).check(matches(isDisplayed()));

        // Check if clicking yes does delete the event
        onData(hasEntry(equalTo("name"), equalTo("Test Event")))
                .inAdapterView(withId(R.id.adminEventListView)) // Replace with your list view ID
                .atPosition(0) // Specify the position of the item if needed
                .perform(longClick());
        Thread.sleep(1000);
        onView(withText("Yes")).perform(click());
        onView(withText("Test Event")).check(doesNotExist());
    }
    /**
     * US 03.02.01 As an administrator, I want to be able to remove profiles.
     */
    @Test
    public void testDeleteProfiles(){

    }
    /**
     * US 03.03.01 As an administrator, I want to be able to remove images.
     */
    @Test
    public void testRemoveImages(){

    }

    /**
     * US 03.07.01 As an administrator I want to remove facilities that violate app policy
     */
    @Test
    public void testRemoveFacilities(){

    }
    /**
     * Delete all test documents after the test, even if any tests fail
     */
    @After
    public void cleanUp() {
        firebaseHelper.deleteUser(testUser, () -> {});
        firebaseHelper.deleteEvent(testEvent);
    }
}
