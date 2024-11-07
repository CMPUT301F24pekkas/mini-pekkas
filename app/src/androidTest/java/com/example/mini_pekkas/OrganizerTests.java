package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test cases for Organizers according to user stories
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerTests {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    private FirebaseFirestore database;
    public String deviceId;
    public void deleteDeviceId(Context context) {
        database = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        database.collection("users")
                .document(deviceId)
                .delete();
    }
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        deleteDeviceId(context);
    }

//    /**
//     * US 01.01.01 As an entrant, I want to join the waiting list for a specific event
//     */
//    @Test
//    public void testJoinWait(){
////        // click on the event profile
////        onView(withId(R.id.navigation_event)).perform(click());
////        onView(withId(R.id.joinWaitButton)).perform(click());
//
//    }
//    /**
//     * US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
//     */
//    @Test
//    public void testLeaveWait(){
//
//    }
    /**
     * US 01.02.01 As an entrant, I want to provide my personal information such as name, email and optional phone number in the app
     */
    @Test
    public void testAddProfileDetails() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.firstNameInput)).perform(ViewActions.typeText("John"));
        onView(withId(R.id.lastNameInput)).perform(ViewActions.typeText("Doe"));
        onView(withId(R.id.emailInput)).perform(ViewActions.typeText("test@gmail.com"));
        onView(withId(R.id.phoneInput)).perform(ViewActions.typeText("7801234567"));
        onView(withId(R.id.submitButton)).perform(click());
        Thread.sleep(3000);
        database.collection("users").document(deviceId).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    assertEquals("John", document.getString("name"));
                    assertEquals("Doe", document.getString("lastname"));
                    assertEquals("test@gmail.com", document.getString("email"));
                    assertEquals("7801234567", document.getString("phone"));
                });
    }
    /**
     * US 01.02.02 As an entrant I want to update information such as name, email and contact information on my profile
     */
    @Test
    public void testUpdateProfileDetails() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.submitButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.navigation_profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.edit_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.dialogue_first_name_input)).perform(ViewActions.typeText("John"));
        onView(withId(R.id.dialogue_last_name_input)).perform(ViewActions.typeText("Doe"));
        onView(withId(R.id.dialog_email_input)).perform(ViewActions.typeText("test@gmail.com"));
        onView(withId(R.id.dialog_phone_input)).perform(ViewActions.typeText("7801234567"));
        onView(withText("Save")).perform(click());
        Thread.sleep(3000);
        onView(withText("John")).check(matches(isDisplayed()));
        onView(withText("Doe")).check(matches(isDisplayed()));
        onView(withText("test@gmail.com")).check(matches(isDisplayed()));
        onView(withText("7801234567")).check(matches(isDisplayed()));
    }
//    /**
//     * US 01.03.01 As an entrant I want to upload a profile picture for a more personalized experience
//     */
//    @Test
//    public void testUploadProfilePicture(){
//
//    }
//    /**
//     * US 01.03.02 As an entrant I want remove profile picture if need be
//     */
//    @Test
//    public void testRemoveProfilePicture(){
//
//    }
//    /**
//     * US 01.03.03 As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
//     */
//    @Test
//    public void testNoProfilePicture(){
//
//    }
//    /**
//     * US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
//     */
//    @Test
//    public void testChosenNotifications(){
//
//    }
//
//    /**
//     * US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
//     */
//    @Test
//    public void testLoseNotifications(){
//
//    }
//    /**
//     * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
//     */
//    @Test
//    public void testOptOutNotifications(){
//
//    }
//    /**
//     * US 01.05.01 As an entrant I want another chance to be chosen from the waiting list if a selected user declines an invitation to sign up
//     */
//    @Test
//    public void testChanceChosenAgain(){
//
//    }
//    /**
//     * US 01.05.02 As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event
//     */
//    @Test
//    public void acceptEvent(){
//
//    }
//    /**
//     * US 01.05.03 As an entrant I want to be able to decline an invitation when chosen to participate in an event
//     */
//    @Test
//    public void delineEvent(){
//
//    }
//    /**
//     * US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
//     */
//    @Test
//    public void viewEvent(){
//
//    }
//    /**
//     * US 01.06.02 As an entrant I want to be able to be sign up for an event by scanning the QR code
//     */
//    @Test
//    public void signUpEvent(){
//
//    }
//    /**
//     * US 01.07.01 As an entrant, I want to be identified by my device, so that I don't have to use a username and password
//     */
//    @Test
//    public void deviceIdentify(){
//
//    }
//    /**
//     * US 01.08.01 As an entrant, I want to be warned before joining a waiting list that requires geolocation.
//     */
//    @Test
//    public void geolocationWarning(){
//
//    }
}