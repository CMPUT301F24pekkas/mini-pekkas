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
 * Test cases for users according to user stories
 */
@RunWith(AndroidJUnit4.class)
public class LoginTests {
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
    /**
     * US 01.02.01 As an entrant, I want to provide my personal information such as name, email and optional phone number in the app
     */
    @Test
    public void testCreateUser() throws InterruptedException {
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
     * US 02.01.03 As an organizer, I want to create and manage my facility profile.
     */
    @Test
    public void testCreateOrganizer() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.firstNameInput)).perform(ViewActions.typeText("John"));
        onView(withId(R.id.lastNameInput)).perform(ViewActions.typeText("Doe"));
        onView(withId(R.id.emailInput)).perform(ViewActions.typeText("test@gmail.com"));
        onView(withId(R.id.phoneInput)).perform(ViewActions.typeText("7801234567"));
        onView(withId(R.id.facilityInput)).perform(ViewActions.typeText("Facility"));
        onView(withId(R.id.submitButton)).perform(click());
        Thread.sleep(3000);
        database.collection("users").document(deviceId).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    assertEquals("John", document.getString("name"));
                    assertEquals("Doe", document.getString("lastname"));
                    assertEquals("test@gmail.com", document.getString("email"));
                    assertEquals("7801234567", document.getString("phone"));
                    assertEquals("Facility", document.getString("facility"));
                });
    }

}