package com.example.mini_pekkas;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private FirebaseDatabase database;

    @Before
    public void setUp() {
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Tests the basic functionality of the Firebase
     */
    @Test
    public void testDatabaseWriteAndRead() {
        DatabaseReference ref = database.getReference("test_key");
        ref.setValue("test_value");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assertEquals("test_value", dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fail("Database Error: " + databaseError.getMessage());
            }
        });
    }
    /**
     * US 01.01.01 As an entrant, I want to join the waiting list for a specific event
     */
    @Test
    public void testJoinWait(){
        // click on the event profile
        onView(withId(R.id.navigation_event)).perform(click());
        onView(withId(R.id.joinWaitButton)).perform(click());
        
    }
    /**
     * US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
     */
    @Test
    public void testLeaveWait(){

    }
    /**
     * US 01.02.01 As an entrant, I want to provide my personal information such as name, email and optional phone number in the app
     */
    @Test
    public void testAddProfileDetails(){

    }
    /**
     * US 01.02.02 As an entrant I want to update information such as name, email and contact information on my profile
     */
    @Test
    public void testUpdateProfileDetails(){

    }
    /**
     * US 01.03.01 As an entrant I want to upload a profile picture for a more personalized experience
     */
    @Test
    public void testUploadProfilePicture(){

    }
    /**
     * US 01.03.02 As an entrant I want remove profile picture if need be
     */
    @Test
    public void testRemoveProfilePicture(){

    }
    /**
     * US 01.03.03 As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
     */
    @Test
    public void testNoProfilePicture(){

    }
    /**
     * US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
     */
    @Test
    public void testChosenNotifications(){

    }

    /**
     * US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
     */
    @Test
    public void testLoseNotifications(){

    }
    /**
     * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
     */
    @Test
    public void testOptOutNotifications(){

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
    public void acceptEvent(){

    }
    /**
     * US 01.05.03 As an entrant I want to be able to decline an invitation when chosen to participate in an event
     */
    @Test
    public void delineEvent(){

    }
    /**
     * US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
     */
    @Test
    public void viewEvent(){

    }
    /**
     * US 01.06.02 As an entrant I want to be able to be sign up for an event by scanning the QR code
     */
    @Test
    public void signUpEvent(){

    }
    /**
     * US 01.07.01 As an entrant, I want to be identified by my device, so that I don't have to use a username and password
     */
    @Test
    public void deviceIdentify(){

    }
    /**
     * US 01.08.01 As an entrant, I want to be warned before joining a waiting list that requires geolocation.
     */
    @Test
    public void geolocationWarning(){

    }
}