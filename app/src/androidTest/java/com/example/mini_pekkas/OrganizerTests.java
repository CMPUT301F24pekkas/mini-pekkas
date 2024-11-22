package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import android.content.Context;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for Organizers according to user stories
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerTests {
    @Rule
    public ActivityScenarioRule<OrganizerActivity> scenario = new
            ActivityScenarioRule<OrganizerActivity>(OrganizerActivity.class);
    private FirebaseFirestore database;
    public String deviceId;
    public void CreateTestProfile(Context context) {
        database = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", "John");
        userProfile.put("lastname", "Doe");
        userProfile.put("email", "test@gmail.com");
        userProfile.put("phone", "7801234567");
        userProfile.put("facility", "Facility");
        database.collection("users")
                .document(deviceId)
                .set(userProfile);
    }
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        CreateTestProfile(context);
        onView(withId(R.id.navigation_org_profile)).perform(click());
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            UiObject allowButton = device.findObject(new UiSelector().text("Allow"));
            if (allowButton.exists()) {
                allowButton.click();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onView(withId(R.id.navigation_org_home)).perform(click());
    }



    /**
     * US 02.01.01 As an organizer I want to create a new event and generate a unique promotional QR code
     * that links to the event description and event poster in the app
     */
    @Test
    public void testCreateQR() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.navigation_org_add)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
        onView(withId(R.id.addEventButton)).perform(click());
        onView(withId(R.id.qrDialogueImageView)).check(matches(isDisplayed()));
        database.collection("events")
                .whereEqualTo("name", "Oilers Event")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    document.getReference().delete();
                });
    }
    /**
     * US 02.01.02 As an organizer I want to store hash data of the generated QR code in my database
     */
    @Test
    public void testStoreHash() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.navigation_org_add)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
        onView(withId(R.id.addEventButton)).perform(click());
        onView(withId(R.id.qrDialogueImageView)).check(matches(isDisplayed()));
        database.collection("events")
                .whereEqualTo("name", "Oilers Event")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    assertNotNull("QrCode should not be null", document.getString("QrCode"));
                    document.getReference().delete();
                });
    }
    /**
     * US 02.02.01 As an organizer I want to view the list of entrants who joined my event waiting list
     */
    @Test
    public void testViewEntrants() throws InterruptedException {
    }
    /**
     * US 02.02.02 As an organizer I want to see on a map where entrants joined my event waiting list from.
     */
    @Test
    public void testEntrantMap(){

    }
    /**
     * US 02.02.03 As an organizer I want to enable or disable the geolocation requirement for my event.
     */
    @Test
    public void testEnableGeolocation() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.navigation_org_add)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Test Event"));
        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
        onView(withId(R.id.geoCheckBox)).perform(click());
        onView(withId(R.id.addEventButton)).perform(click());
        database.collection("events")
                .whereEqualTo("name", "Test Event")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    assertEquals("Geo field should be true", Boolean.TRUE, document.getBoolean("geo"));
                    document.getReference().delete();
                });
    }
    /**
     * US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
     */
    @Test
    public void testLimitEntrants() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.navigation_org_add)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Test Event"));
        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
        onView(withId(R.id.maxPartCheckBox)).perform(click());
        onView(withId(R.id.editMaxPart)).perform(ViewActions.typeText("300"));
        onView(withId(R.id.addEventButton)).perform(click());
        database.collection("events")
                .whereEqualTo("name", "Test Event")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    assertEquals(300L, document.getLong("maxAttendees").longValue());
                    document.getReference().delete();
                });
    }
    /**
     * US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants
     */
    @Test
    public void testEventPoster() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.navigation_org_add)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Test Event"));
        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
        onView(withId(R.id.addEventPicture)).perform(click());
    }

    /**
     * US 02.04.02 As an organizer I want to update an event poster to provide visual information to entrants
     */
    @Test
    public void testUpdateEventPoster() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.navigation_org_add)).perform(click());
        onView(withId(R.id.editEventPictureButton)).perform(click());

    }
    /**
     * US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events.
     */
    @Test
    public void testNotifyEntrants(){

    }
    /**
     * US 02.05.02 As an organizer I want to set the system to sample a specified number of attendees to register for the event
     */
    @Test
    public void testSampleEntrants(){

    }
    /**
     * US 02.05.03 As an organizer I want to be able to draw a replacement applicant from the pooling system
     * when a previously selected applicant cancels or rejects the invitation
     */
    @Test
    public void testDrawReplacement(){

    }
    /**
     * US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply
     */
    @Test
    public void testViewChosen(){

    }
    /**
     * US 02.06.02 As an organizer I want to see a list of all the cancelled entrants
     */
    @Test
    public void testViewCancelled(){

    }
    /**
     * US 02.06.03 As an organizer I want to see a final list of entrants who enrolled for the event
     */
    @Test
    public void testViewEnrolled(){

    }
    /**
     * US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event
     */
    @Test
    public void testCancelEntrants(){

    }
    /**
     * US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
     */
    @Test
    public void testNotifyWait(){

    }
    /**
     * US 02.07.02 As an organizer I want to send notifications to all selected entrants
     */
    @Test
    public void testNotifySelected(){

    }
    /**
     * US 02.07.03 As an organizer I want to send a notification to all cancelled entrants
     */
    @Test
    public void testNotifyCancelled(){

    }
}