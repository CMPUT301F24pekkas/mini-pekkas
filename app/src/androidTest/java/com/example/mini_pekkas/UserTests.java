package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for users according to user stories
 */
@RunWith(AndroidJUnit4.class)
public class UserTests {
    @Rule
    public IntentsTestRule<UserActivity> intentsRule = new IntentsTestRule<>(UserActivity.class);
    private FirebaseFirestore database;
    public String deviceId;
    private Firebase firebaseHelper;

    /**
     * Creates a test profile
     * @param context
     */
    public void CreateTestProfile(Context context) {
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", "John");
        userProfile.put("lastname", "Doe");
        userProfile.put("email", "test@gmail.com");
        userProfile.put("phone", "7801234567");
        userProfile.put("facility", "");
        userProfile.put("profilePhoto", "");
        userProfile.put("userID", "testUserID");
        database.collection("users")
                .document(deviceId)
                .set(userProfile);
    }

    /**
     * Creates a test event for users to join
     */
    public void CreateTestEvent(){
        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date endDate = calendar.getTime();
        Map<String, Object> mockEventData = new HashMap<>();
        mockEventData.put("name", "Test Cases Event");
        mockEventData.put("geo", true);
        mockEventData.put("id", "Test Event");
        mockEventData.put("description", "Test Description");
        mockEventData.put("maxAttendees", 1);
        mockEventData.put("maxWaitlist", 1);
        mockEventData.put("startDate", startDate);
        mockEventData.put("endDate", endDate);
        mockEventData.put("waitlist", new ArrayList<>());
        mockEventData.put("eventHost", new HashMap<String, String>() {{
            put("name", "Peter");
            put("email", "organizer@gmail.com");
            put("id",  deviceId);
            put("lastname", "Griffin");
            put("phone", "123456789");
        }});
        mockEventData.put("facility", "facility");
        mockEventData.put("QrCode", "testQRCodeData");
        database.collection("events")
                .document("Test Event")
                .set(mockEventData);
//        Map<String, Object> mockUserEvent = new HashMap<>();
//        mockUserEvent.put("eventID", "Test Event Organizer");
//        mockUserEvent.put("status", "organized");
//        mockUserEvent.put("userID", "Test Organizer ID");
//        database.collection("user-events")
//                .document("Test Event Organizer")
//                .set(mockUserEvent);


    }
    public void deleteCurrentProfile(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        database.collection("users")
                .whereEqualTo("userID", deviceId)
                .get()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Test", "Profile successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Test", "Error deleting profile");
                });
    }
    /**
     * set up tests by adding a test user and also turning off notifications which satisfies
     * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
     */
    @Before
    public void setUp() throws InterruptedException {
        database = FirebaseFirestore.getInstance();
        Context context = ApplicationProvider.getApplicationContext();
        deleteCurrentProfile(context);
        CreateTestProfile(context);
        CreateTestEvent();
    }

    /**
     * deletes the test events and user
     */
    @After
    public void tearDown() {
        database.collection("events")
                .document("Test Event")
                .delete();
        database.collection("users")
                .document(deviceId)
                .delete();
        database.collection("user-events").whereEqualTo("userID", deviceId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        document.getReference().delete();
                    }
                });
        database.collection("events").whereEqualTo("name", "Oilers Event").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        document.getReference().delete();
                    }
                });
    }

    /**
     * Checks if the notifications dialog is shown and presses allow
     * @throws InterruptedException
     */
    private void checkNotifs() throws InterruptedException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Thread.sleep(2000);
        try {
            UiObject allowButton = device.findObject(new UiSelector().text("Allow"));
            if (allowButton.exists()) {
                allowButton.click();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkCamera() throws InterruptedException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            UiObject allowButton = device.findObject(new UiSelector().text("While using the app"));
            if (allowButton.exists()) {
                allowButton.click();
                onView(withId(R.id.cameraButton)).perform(click());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * US 01.01.01 As an entrant, I want to join the waiting list for a specific event
     */
    @Test
    public void testJoinWait() throws InterruptedException {
        checkNotifs();
        joinWaitList();
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot) {
                            String status = document.getString("status");
                            if ("waitlisted".equals(status)) {
                                assertTrue("User is waitlisted", true);
                            } else {
                                fail("User is not waitlisted");
                            }
                        }
                    } else {
                        fail("No documents found for the given criteria");
                    }
                });
        Thread.sleep(2000);
    }
    /**
     * US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
     */
    @Test
    public void testLeaveWait() throws InterruptedException {
        checkNotifs();
        joinWaitList();
        Thread.sleep(2000);
        onView(withId(R.id.leaveWaitButton)).perform(click());
        onView(withId(R.id.leaveWaitConfirmButton)).perform(click());
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot) {
                            String status = document.getString("status");
                            if ("cancelled".equals(status)) {
                                assertTrue("User is cancelled from waitlist", true);
                            } else {
                                fail("User is not waitlisted");
                            }
                        }
                    } else {
                        fail("No documents found for the given criteria");
                    }
                });
        Thread.sleep(2000);
    }
    /**
     * US 01.02.02 As an entrant I want to update information such as name, email and contact information on my profile
     */
    @Test
    public void testUpdateProfileDetails() throws InterruptedException {
        checkNotifs();
        Thread.sleep(3000);
        onView(withId(R.id.navigation_profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.edit_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.dialogue_first_name_input)).perform(ViewActions.clearText(),ViewActions.typeText("Connor"));
        onView(withId(R.id.dialogue_last_name_input)).perform(ViewActions.clearText(),ViewActions.typeText("McDavid"));
        onView(withId(R.id.dialog_email_input)).perform(ViewActions.clearText(),ViewActions.typeText("Connor@gmail.com"));
        onView(withId(R.id.dialog_phone_input)).perform(ViewActions.clearText(),ViewActions.typeText("7809876543"));
        onView(withText("Save")).perform(click());
        Thread.sleep(3000);
        onView(withText("Connor")).check(matches(isDisplayed()));
        onView(withText("McDavid")).check(matches(isDisplayed()));
        onView(withText("Connor@gmail.com")).check(matches(isDisplayed()));
        onView(withText("7809876543")).check(matches(isDisplayed()));
        Thread.sleep(3000);
        database.collection("users").document(deviceId).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    assertEquals("Connor", document.getString("name"));
                    assertEquals("McDavid", document.getString("lastname"));
                    assertEquals("Connor@gmail.com", document.getString("email"));
                    assertEquals("7809876543", document.getString("phone"));
                });
    }
    /**
     * US 01.03.01 As an entrant I want to upload a profile picture for a more personalized experience
     */
    @Test
    public void testUploadProfilePicture() throws InterruptedException {
        checkNotifs();
        onView(withId(R.id.navigation_profile)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.pfp_edit)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.choose_new_picture_button)).check(matches(isDisplayed()));
    }
    /**
     * US 01.03.02 As an entrant I want remove profile picture if need be
     */
    @Test
    public void testRemoveProfilePicture() throws InterruptedException {
        checkNotifs();
        onView(withId(R.id.navigation_profile)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.pfp_edit)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.delete_picture_button)).check(matches(isDisplayed()));
    }
    /**
     * US 01.03.03 As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
     */
    @Test
    public void testNoProfilePicture() throws InterruptedException {
        checkNotifs();
        onView(withId(R.id.navigation_profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.edit_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.dialogue_first_name_input)).perform(ViewActions.clearText(),ViewActions.typeText("Connor"));
        onView(withText("Save")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.profileText)).check(matches(withText("C")));
    }
    /**
     * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
     */
    @Test
    public void testOptOutNotifications() throws InterruptedException {
        checkNotifs();
        onView(withId(R.id.navigation_notifications)).perform(click());
        onView(withId(R.id.opt_out)).perform(click());
        onView(withId(R.id.optOutNotifications)).perform(click());
        ActivityScenario.launch(UserActivity.class);
        Thread.sleep(2000);
        onView(withId(R.id.navigation_notifications)).perform(click());
        onView(withId(R.id.optIn)).check(matches(isDisplayed()));
    }
    /**
     * US 01.05.01 As an entrant I want another chance to be chosen from the waiting list if a selected user declines an invitation to sign up
     */
    @Test
    public void testChanceChosenAgain(){

    }
    /**
     * US 01.05.02 As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event
     */
    @Test
    public void acceptEvent() throws InterruptedException {
        checkNotifs();
        joinWaitList();
        onView(withId(R.id.navigation_home)).perform(click());
        Thread.sleep(2000);
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .whereEqualTo("status", "waitlisted")
                .get()
                .addOnSuccessListener(task -> {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        database.collection("user-events")
                                .document(document.getId())
                                .update("status", "pending");
                    }
                });
        Thread.sleep(2000);
        onView(withId(R.id.EditEvent)).perform(click());
        onView(withId(R.id.acceptButton)).perform(click());
        Thread.sleep(2000);
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .get()
                .addOnSuccessListener(task -> {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        String status = document.getString("status");
                        if ("enrolled".equals(status)) {
                            assertTrue("testUserID enrolled in event", true);
                            database.collection("user-notifications")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document deleted: " + document.getId()))
                                    .addOnFailureListener(e -> fail("Failed to delete document: " + e.getMessage()));
                        } else {
                            fail("Failed to enroll in event");
                        }
                    }
                });
    }
    /**
     * US 01.05.03 As an entrant I want to be able to decline an invitation when chosen to participate in an event
     */
    @Test
    public void declineEvent() throws InterruptedException {
        checkNotifs();
        joinWaitList();
        onView(withId(R.id.navigation_home)).perform(click());
        Thread.sleep(2000);
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .whereEqualTo("status", "waitlisted")
                .get()
                .addOnSuccessListener(task -> {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        database.collection("user-events")
                                .document(document.getId())
                                .update("status", "pending");
                    }
                });
        Thread.sleep(2000);
        onView(withId(R.id.EditEvent)).perform(click());
        onView(withId(R.id.declineButton)).perform(click());
        onView(withId(R.id.leaveWaitConfirmButton)).perform(click());
        Thread.sleep(2000);
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .get()
                .addOnSuccessListener(task -> {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        String status = document.getString("status");
                        if ("cancelled".equals(status)) {
                            assertTrue("testUserID cancelled event", true);
                            database.collection("user-notifications")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document deleted: " + document.getId()))
                                    .addOnFailureListener(e -> fail("Failed to delete document: " + e.getMessage()));
                        } else {
                            fail("Failed to cancel event");
                        }
                    }
                });

    }
    /**
     * US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
     */
    @Test
    public void viewEvent() throws InterruptedException {
        checkNotifs();
        Intent mockData = new Intent();
        mockData.putExtra("SCAN_RESULT", "testQRCodeData"); // Mock QR code data
        Instrumentation.ActivityResult mockResult =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, mockData);

        Intents.intending(hasAction("com.google.zxing.client.android.SCAN"))
                .respondWith(mockResult);

        onView(withId(R.id.navigation_camera)).perform(click());
        onView(withId(R.id.cameraButton)).perform(click());
        checkCamera();
        onView(withId(R.id.eventNameView)).check(matches(withText("Test Cases Event")));
        onView(withId(R.id.eventDescriptionView)).check(matches(withText("Test Description")));
        onView(withId(R.id.organizerNameView)).check(matches(withText("Peter")));
    }
    /**
     * US 01.06.02 As an entrant I want to be able to be sign up for an event by scanning the QR code
     */
    @Test
    public void signUpEvent() throws InterruptedException {
        checkNotifs();
        joinWaitList();
        onView(withId(R.id.navigation_home)).perform(click());
        Thread.sleep(2000);
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .whereEqualTo("status", "waitlisted")
                .get()
                .addOnSuccessListener(task -> {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        database.collection("user-events")
                                .document(document.getId())
                                .update("status", "pending");
                    }
                });
        Intent mockData = new Intent();
        mockData.putExtra("SCAN_RESULT", "testQRCodeData"); // Mock QR code data
        Instrumentation.ActivityResult mockResult =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, mockData);

        Intents.intending(hasAction("com.google.zxing.client.android.SCAN"))
                .respondWith(mockResult);

        onView(withId(R.id.navigation_camera)).perform(click());
        onView(withId(R.id.acceptButton)).perform(click());
        Thread.sleep(2000);
        database.collection("user-events")
                .whereEqualTo("eventID", "Test Event")
                .whereEqualTo("userID", deviceId)
                .get()
                .addOnSuccessListener(task -> {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        String status = document.getString("status");
                        if ("enrolled".equals(status)) {
                            assertTrue("testUserID enrolled in event", true);
                            database.collection("user-notifications")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document deleted: " + document.getId()))
                                    .addOnFailureListener(e -> fail("Failed to delete document: " + e.getMessage()));
                        } else {
                            fail("Failed to enroll in event");
                        }
                    }
                });
    }
    /**
     * US 01.07.01 As an entrant, I want to be identified by my device, so that I don't have to use a username and password
     */
    @Test
    public void deviceIdentify() throws InterruptedException {
        checkNotifs();
        onView(withId(R.id.user_view)).check(matches(isDisplayed()));
    }
    /**
     * US 01.08.01 As an entrant, I want to be warned before joining a waiting list that requires geolocation.
     */
    @Test
    public void geolocationWarning() throws InterruptedException {
        checkNotifs();
        Intent mockData = new Intent();
        mockData.putExtra("SCAN_RESULT", "testQRCodeData"); // Mock QR code data
        Instrumentation.ActivityResult mockResult =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, mockData);

        Intents.intending(hasAction("com.google.zxing.client.android.SCAN"))
                .respondWith(mockResult);

        onView(withId(R.id.navigation_camera)).perform(click());
        onView(withId(R.id.cameraButton)).perform(click());
        checkCamera();
        Thread.sleep(2000);
        onView(withId(R.id.joinWaitButton)).check(matches(isDisplayed()));
        onView(withId(R.id.joinWaitButton)).perform(click());
        onView(withText("This event requires participants to be in a specific area. Are you sure you would like to join?")).check(matches(isDisplayed()));
    }

    /**
     * process for joining the waitlist through qr code scan
     * @throws InterruptedException
     */
    private void joinWaitList() throws InterruptedException {
        Intent mockData = new Intent();
        mockData.putExtra("SCAN_RESULT", "testQRCodeData"); // Mock QR code data
        Instrumentation.ActivityResult mockResult =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, mockData);

        Intents.intending(hasAction("com.google.zxing.client.android.SCAN"))
                .respondWith(mockResult);

        onView(withId(R.id.navigation_camera)).perform(click());
        onView(withId(R.id.cameraButton)).perform(click());
        checkCamera();
        Thread.sleep(2000);
        onView(withId(R.id.joinWaitButton)).check(matches(isDisplayed()));
        onView(withId(R.id.joinWaitButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.joinWaitButton)).perform(click());
    }
}
