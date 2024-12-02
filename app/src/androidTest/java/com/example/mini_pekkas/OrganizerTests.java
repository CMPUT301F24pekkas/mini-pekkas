package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        userProfile.put("profilePhoto", null);
        database.collection("users")
                .document(deviceId)
                .set(userProfile);
    }
    public void CreateTestUserProfile() {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", "John");
        userProfile.put("lastname", "Doe");
        userProfile.put("email", "test@gmail.com");
        userProfile.put("phone", "7801234567");
        userProfile.put("facility", "");
        userProfile.put("profilePhoto", "");
        userProfile.put("userID", "testUserID");
        database.collection("users")
                .document("Test User")
                .set(userProfile);
    }
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        CreateTestProfile(context);
        CreateTestUserProfile();
    }

    /**
     * deletes the test events and user
     */
    @After
    public void tearDown() {
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
     * deletes the user-event document in the firebase based on device id
     */
    private void deleteUserEvent() {
        database.collection("user-events")
                .whereEqualTo("userID", deviceId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    document.getReference().delete();
                });
    }
//
//    /**
//     * US 02.01.01 As an organizer I want to create a new event and generate a unique promotional QR code
//     * that links to the event description and event poster in the app
//     */
//    @Test
//    public void testCreateQR() throws InterruptedException {
//        Thread.sleep(3000);
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.editMaxWait)).perform(ViewActions.typeText("1"));
//        onView(withId(R.id.editMaxPart)).perform(ViewActions.typeText("1"));
//        onView(withId(R.id.editStartDate)).perform(ViewActions.typeText("2024-03-14"));
//        onView(withId(R.id.editEndDate)).perform(ViewActions.typeText("2024-03-15"));
//        onView(withId(R.id.editStartTime)).perform(ViewActions.typeText("10:00"));
//        onView(withId(R.id.editEndTime)).perform(ViewActions.typeText("10:00"));
//        onView(withId(R.id.editDescription)).perform(ViewActions.typeText("Testing Description"));
//        onView(withId(R.id.addEventButton)).perform(scrollTo(), click());
//        onView(withId(R.id.qrDialogueImageView)).check(matches(isDisplayed()));
//        database.collection("events")
//                .whereEqualTo("name", "Oilers Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    document.getReference().delete();
//                });
//        deleteUserEvent();
//    }
//    /**
//     * US 02.01.02 As an organizer I want to store hash data of the generated QR code in my database
//     */
//    @Test
//    public void testStoreHash() throws InterruptedException {
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.addEventButton)).perform(click());
//        onView(withId(R.id.qrDialogueImageView)).check(matches(isDisplayed()));
//        database.collection("events")
//                .whereEqualTo("name", "Oilers Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    assertNotNull("QrCode should not be null", document.getString("QrCode"));
//                    document.getReference().delete();
//                });
//        deleteUserEvent();
//    }
    /**
     * US 02.02.01 As an organizer I want to view the list of entrants who joined my event waiting list
     */
    @Test
    public void testViewEntrants() throws InterruptedException {
        CreateEvent();
        AddWaitlist();
        onView(withId(R.id.navigation_org_home)).perform(click());
        onView(withId(R.id.EditEvent)).perform(click());
        onView(withId(R.id.waitButton));
        Thread.sleep(3000);
        onView(withText("John")).check(matches(isDisplayed()));
    }
//    /**
//     * US 02.02.02 As an organizer I want to see on a map where entrants joined my event waiting list from.
//     */
//    @Test
//    public void testEntrantMap(){
//
//    }
//    /**
//     * US 02.02.03 As an organizer I want to enable or disable the geolocation requirement for my event.
//     */
//    @Test
//    public void testEnableGeolocation() throws InterruptedException {
//        Thread.sleep(3000);
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Test Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.geoCheckBox)).perform(click());
//        onView(withId(R.id.addEventButton)).perform(click());
//        database.collection("events")
//                .whereEqualTo("name", "Test Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    assertEquals("Geo field should be true", Boolean.TRUE, document.getBoolean("geo"));
//                    document.getReference().delete();
//                });
//        deleteUserEvent();
//    }
//    /**
//     * US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
//     */
//    @Test
//    public void testLimitEntrants() throws InterruptedException {
//        Thread.sleep(3000);
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Test Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.maxPartCheckBox)).perform(click());
//        onView(withId(R.id.editMaxPart)).perform(ViewActions.typeText("300"));
//        onView(withId(R.id.addEventButton)).perform(click());
//        database.collection("events")
//                .whereEqualTo("name", "Test Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    assertEquals(300L, document.getLong("maxAttendees").longValue());
//                    document.getReference().delete();
//                });
//        deleteUserEvent();
//    }
//    /**
//     * US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants
//     */
//    @Test
//    public void testEventPoster() throws InterruptedException {
//        Thread.sleep(3000);
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Test Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.addEventPicture)).perform(click());
//    }
//
//    /**
//     * US 02.04.02 As an organizer I want to update an event poster to provide visual information to entrants
//     */
//    @Test
//    public void testUpdateEventPoster() throws InterruptedException {
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.addEventButton)).perform(click());
//        onView(withId(R.id.qrDialogueImageView)).check(matches(isDisplayed()));
//        onView(withId(R.id.confirmQrButton)).perform(click());
//        onView(withId(R.id.navigation_org_home)).perform(click());
//        onView(withId(R.id.EditEvent)).perform(click());
//        onView(withId(R.id.editEventPictureButton)).perform(click());
//        database.collection("events")
//                .whereEqualTo("name", "Oilers Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    document.getReference().delete();
//                });
//        deleteUserEvent();
//        Thread.sleep(2000);
//    }
//    /**
//     * US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events.
//     */
//    @Test
//    public void testNotifyEntrants() throws InterruptedException {
//        CreateEvent();
//        AddWaitlist();
//        onView(withId(R.id.navigation_org_home)).perform(click());
//        onView(withId(R.id.EditEvent)).perform(click());
//        onView(withId(R.id.chooseButton)).perform(click());
//        Thread.sleep(2000);
//        onView(withId(R.id.navigation_org_notifications)).perform(click());
//        onView(withId(R.id.etNotificationTitle)).perform(ViewActions.typeText("Test Notification"));
//        onView(withId(R.id.etNotificationMessage)).perform(ViewActions.typeText("Test Message"));
//        onView(withId(R.id.spinnerPriority)).perform(click());
//        onView(withText("Oilers Event")).perform(click());
//        onView(withId(R.id.spinnerPriority2)).perform(click());
//        onView(withText("enrolled")).perform(click());
//        onView(withId(R.id.btnSubmitNotification)).perform(click());
//        database.collection("events")
//                .whereEqualTo("name", "Oilers Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    document.getReference().delete();
//                });
//    }
//    /**
//     * US 02.05.02 As an organizer I want to set the system to sample a specified number of attendees to register for the event
//     */
//    @Test
//    public void testSampleEntrants() throws InterruptedException {
//        CreateEvent();
//        AddWaitlist();
//        onView(withId(R.id.navigation_org_home)).perform(click());
//        onView(withId(R.id.EditEvent)).perform(click());
//        onView(withId(R.id.chooseButton)).perform(click());
//        database.collection("user-events")
//                .document("Test User")
//                .get()
//                .addOnCompleteListener(task -> {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            String status = document.getString("status");
//                            if ("pending".equals(status)) {
//                                assertTrue("test user is pending", true);
//                            } else {
//                                Log.d("Firestore", "Status is not pending. Current status: " + status);
//                            }
//                        }
//                });
//
//
//
//    }
//    /**
//     * US 02.05.03 As an organizer I want to be able to draw a replacement applicant from the pooling system
//     * when a previously selected applicant cancels or rejects the invitation
//     */
//    @Test
//    public void testDrawReplacement(){
//
//    }
//    /**
//     * US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply
//     */
//    @Test
//    public void testViewChosen() throws InterruptedException {
//        CreateEvent();
//        AddWaitlist();
//        onView(withId(R.id.navigation_org_home)).perform(click());
//        onView(withId(R.id.EditEvent)).perform(click());
//        onView(withId(R.id.chooseButton)).perform(click());
//        onView(withId(R.id.chosenButton)).perform(click());
//        Thread.sleep(3000);
//        onView(withText("John")).check(matches(isDisplayed()));
//    }
//    /**
//     * US 02.06.02 As an organizer I want to see a list of all the cancelled entrants
//     */
//    @Test
//    public void testViewCancelled() throws InterruptedException {
//        CreateEvent();
//        AddWaitlist();
//        onView(withId(R.id.navigation_org_home)).perform(click());
//        onView(withId(R.id.EditEvent)).perform(click());
//        onView(withId(R.id.chooseButton)).perform(click());
//        database.collection("user-events")
//                .document("Test User")
//                .update("status", "cancelled");
//        onView(withId(R.id.canceledButton)).perform(click());
//        Thread.sleep(3000);
//        onView(withText("John")).check(matches(isDisplayed()));
//
//    }
//    /**
//     * US 02.06.03 As an organizer I want to see a final list of entrants who enrolled for the event
//     */
//    @Test
//    public void testViewEnrolled() throws InterruptedException {
//        CreateEvent();
//        AddWaitlist();
//        onView(withId(R.id.navigation_org_home)).perform(click());
//        onView(withId(R.id.EditEvent)).perform(click());
//        onView(withId(R.id.chooseButton)).perform(click());
//        database.collection("user-events")
//                .document("Test User")
//                .update("status", "enrolled");
//        onView(withId(R.id.enrolledButton)).perform(click());
//        Thread.sleep(3000);
//        onView(withText("John")).check(matches(isDisplayed()));
//    }
//    /**
//     * US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event
//     */
//    @Test
//    public void testCancelEntrants(){
//
//    }
//    /**
//     * US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
//     */
//    @Test
//    public void testNotifyWait() throws InterruptedException {
//        CreateEvent();
//        onView(withId(R.id.navigation_org_notifications)).perform(click());
//        onView(withId(R.id.etNotificationTitle)).perform(ViewActions.typeText("Test Notification"));
//        onView(withId(R.id.etNotificationMessage)).perform(ViewActions.typeText("Test Message"));
//        onView(withId(R.id.spinnerPriority)).perform(click());
//        onView(withText("Oilers Event")).perform(click());
//        onView(withId(R.id.spinnerPriority2)).perform(click());
//        onView(withText("waitlisted")).perform(click());
//        onView(withId(R.id.btnSubmitNotification)).perform(click());
//        database.collection("events")
//                .whereEqualTo("name", "Oilers Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    document.getReference().delete();
//                });
//
//    }
//    /**
//     * US 02.07.02 As an organizer I want to send notifications to all selected entrants
//     */
//    @Test
//    public void testNotifySelected() throws InterruptedException {
//        CreateEvent();
//        onView(withId(R.id.navigation_org_notifications)).perform(click());
//        onView(withId(R.id.etNotificationTitle)).perform(ViewActions.typeText("Test Notification"));
//        onView(withId(R.id.etNotificationMessage)).perform(ViewActions.typeText("Test Message"));
//        onView(withId(R.id.spinnerPriority)).perform(click());
//        onView(withText("Oilers Event")).perform(click());
//        onView(withId(R.id.spinnerPriority2)).perform(click());
//        onView(withText("pending")).perform(click());
//        onView(withId(R.id.btnSubmitNotification)).perform(click());
//        database.collection("events")
//                .whereEqualTo("name", "Oilers Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    document.getReference().delete();
//                });
//
//    }
//    /**
//     * US 02.07.03 As an organizer I want to send a notification to all cancelled entrants
//     */
//    @Test
//    public void testNotifyCancelled() throws InterruptedException {
//        CreateEvent();
//        onView(withId(R.id.navigation_org_notifications)).perform(click());
//        onView(withId(R.id.etNotificationTitle)).perform(ViewActions.typeText("Test Notification"));
//        onView(withId(R.id.etNotificationMessage)).perform(ViewActions.typeText("Test Message"));
//        onView(withId(R.id.spinnerPriority)).perform(click());
//        onView(withText("Oilers Event")).perform(click());
//        onView(withId(R.id.spinnerPriority2)).perform(click());
//        onView(withText("cancelled")).perform(click());
//        onView(withId(R.id.btnSubmitNotification)).perform(click());
//        database.collection("events")
//                .whereEqualTo("name", "Oilers Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    document.getReference().delete();
//                });
//
//    }
//    /**
//     * US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
//     */
//    @Test
//    public void testChosenNotifications() throws InterruptedException {
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//        // create an event
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.editMaxWait)).perform(ViewActions.typeText("1"));
//        onView(withId(R.id.maxPartCheckBox)).perform(click());
//        onView(withId(R.id.editMaxPart)).perform(ViewActions.typeText("1"));
//        onView(withId(R.id.editStartDate)).perform(ViewActions.typeText("2024-03-14"));
//        onView(withId(R.id.editEndDate)).perform(ViewActions.typeText("2024-03-15"));
//        onView(withId(R.id.editStartTime)).perform(ViewActions.typeText("10:00"));
//        onView(withId(R.id.editEndTime)).perform(ViewActions.typeText("10:00"));
//        onView(withId(R.id.editDescription)).perform(ViewActions.typeText("Testing Description"));
//        onView(withId(R.id.addEventButton)).perform(scrollTo(), click());
//        onView(withId(R.id.confirmQrButton)).perform(click());
//        Thread.sleep(3000);
//
//        database.collection("user-events")
//                .whereEqualTo("userID", deviceId)
//                .whereEqualTo("status", "organized")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                        // Get the first matching document
//                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                        String eventID = document.getString("eventID");
//                        Log.d("Firestore", "Found eventID: " + eventID);
//                        Map<String, Object> mockUserEvent = new HashMap<>();
//                        mockUserEvent.put("eventID", eventID);
//                        mockUserEvent.put("status", "waitlisted");
//                        mockUserEvent.put("userID", "testUserID");
//                        database.collection("user-events")
//                                .document("Test Event Waitlist")
//                                .set(mockUserEvent);
//                    } else if (task.getResult().isEmpty()) {
//                        Log.d("Firestore", "No matching documents found");
//                    } else {
//                        Log.e("Firestore", "Error querying documents", task.getException());
//                    }
//                });
////        // choose the participant
//        Thread.sleep(2000);
//        onView(withId(R.id.chooseButton)).perform(click());
//        Thread.sleep(2000);
//        Task<QuerySnapshot> queryTask = database.collection("user-notifications")
//                .whereEqualTo("userID", "testUserID")
//                .get();
//
//        queryTask.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                if (!task.getResult().isEmpty()) {
//                    // Assert that the document exists
//                    assertTrue("Document exists for userID: testUserID", true);
//
//                    // Delete the document(s)
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        database.collection("user-notifications")
//                                .document(document.getId())
//                                .delete();
//                    }
//                } else {
//                    fail("No document found for userID: testUserID");
//                }
//            }
//        });
//    }
//
//    /**
//     * US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
//     */
//    @Test
//    public void testLoseNotifications() throws InterruptedException {
//        onView(withId(R.id.navigation_org_add)).perform(click());
//        Thread.sleep(3000);
//
//        // Create an event
//        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
//        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
//        onView(withId(R.id.editMaxWait)).perform(ViewActions.typeText("1"));
//        onView(withId(R.id.maxPartCheckBox)).perform(click());
//        onView(withId(R.id.editMaxPart)).perform(ViewActions.typeText("0"));
//        onView(withId(R.id.editStartDate)).perform(ViewActions.typeText("2024-03-14"));
//        onView(withId(R.id.editEndDate)).perform(ViewActions.typeText("2024-03-15"));
//        onView(withId(R.id.editStartTime)).perform(ViewActions.typeText("10:00"));
//        onView(withId(R.id.editEndTime)).perform(ViewActions.typeText("10:00"));
//        onView(withId(R.id.editDescription)).perform(ViewActions.typeText("Testing Description"));
//        onView(withId(R.id.addEventButton)).perform(scrollTo(), click());
//        Thread.sleep(2000);
//        onView(withId(R.id.confirmQrButton)).perform(click());
//        Thread.sleep(3000);
//
//        // Query Firestore for the event ID
//        database.collection("user-events")
//                .whereEqualTo("userID", deviceId)
//                .whereEqualTo("status", "organized")
//                .get()
//                .addOnSuccessListener(task -> {
//                    if (!task.isEmpty()) {
//                        String eventID = task.getDocuments().get(0).getString("eventID");
//
//                        // Add a test user to the waitlist
//                        Map<String, Object> mockUserEvent = new HashMap<>();
//                        mockUserEvent.put("eventID", eventID);
//                        mockUserEvent.put("status", "waitlisted");
//                        mockUserEvent.put("userID", "testUserID");
//                        database.collection("user-events").document("Test Event Waitlist").set(mockUserEvent);
//                    }
//                });
//
//        Thread.sleep(2000);
//        onView(withId(R.id.chooseButton)).perform(click());
//        Thread.sleep(2000);
//
//        // Check for user notifications
//        database.collection("user-notifications")
//                .whereEqualTo("userID", "testUserID")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                        assertTrue("Notification exists for testUserID", true);
//
//                        // Delete the notifications
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            database.collection("user-notifications")
//                                    .document(document.getId())
//                                    .delete();
//                        }
//                    } else {
//                        fail("No notification found for testUserID");
//                    }
//                });
//
//        // Cleanup test data
//        database.collection("user-events").document("Test Event Waitlist").delete();
//        database.collection("user-events")
//                .whereEqualTo("userID", deviceId)
//                .whereEqualTo("status", "organized")
//                .get()
//                .addOnSuccessListener(query -> {
//                    for (DocumentSnapshot document : query.getDocuments()) {
//                        database.collection("user-events").document(document.getId()).delete();
//                    }
//                });
//    }
    private void CreateEvent() throws InterruptedException {
        onView(withId(R.id.navigation_org_add)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.createEventEditText)).perform(ViewActions.typeText("Oilers Event"));
        onView(withId(R.id.createEventLocationEditText)).perform(ViewActions.typeText("Stadium"));
        onView(withId(R.id.editMaxWait)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.maxPartCheckBox)).perform(click());
        onView(withId(R.id.editMaxPart)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.editStartDate)).perform(ViewActions.typeText("2024-03-14"));
        onView(withId(R.id.editEndDate)).perform(ViewActions.typeText("2024-03-15"));
        onView(withId(R.id.editStartTime)).perform(ViewActions.typeText("10:00"));
        onView(withId(R.id.editEndTime)).perform(ViewActions.typeText("10:00"));
        onView(withId(R.id.editDescription)).perform(ViewActions.typeText("Testing Description"));
        onView(withId(R.id.addEventButton)).perform(scrollTo(), click());
        Thread.sleep(2000);
        onView(withId(R.id.confirmQrButton)).perform(click());
    }

    private void AddWaitlist() {
        database.collection("user-events")
        .whereEqualTo("userID", deviceId)
        .whereEqualTo("status", "organized")
        .get()
        .addOnSuccessListener(task -> {
            if (!task.isEmpty()) {
                String eventID = task.getDocuments().get(0).getString("eventID");

                // Add a test user to the waitlist
                Map<String, Object> mockUserEvent = new HashMap<>();
                mockUserEvent.put("eventID", eventID);
                mockUserEvent.put("status", "waitlisted");
                mockUserEvent.put("userID", "testUserID");
                database.collection("user-events").document("Test Event Waitlist").set(mockUserEvent);
            }
        });
    }
}