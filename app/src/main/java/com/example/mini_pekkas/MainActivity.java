package com.example.mini_pekkas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mini_pekkas.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText facilityInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        facilityInput = findViewById(R.id.facilityInput);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBasedOnFacility();
            }
        });


        /*
        What follows below are firebase functions I have to test in main because it needs special permissions
        Clean and remove this calls when you are done testing. They should be called in their respective UI fragment
        You can also let Android studio compress these into lambda functions for readability. I left them in here for clarity
         */

        // Initialize Firebase class
        Firebase firebaseHelper = new Firebase(this);

        // Finds the user document for the given device. Makes a new one if it doesn't exist
        // Creates a new user Class. See implementation in User.java
        firebaseHelper.getUser(new Firebase.OnDocumentRetrievedListener() {
            @Override
            public void onDocumentRetrieved(DocumentSnapshot documentSnapshot) {
                User thisUser = new User(documentSnapshot.getData());
            }

            // All my default error handler just prints the error. Not necessary to implement
            @Override
            public void onError(Exception e) {
                Firebase.OnDocumentRetrievedListener.super.onError(e);
            }
        });

        // Finds a list of events in users-in-events collection
        // Replace eventList with your own event list
        firebaseHelper.getWaitlist(new Firebase.OnDocumentListRetrievedListener() {
            @Override
            public void onDocumentsRetrieved(List<DocumentSnapshot> documentSnapshots) {
                ArrayList<Event> eventList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    Event event = new Event(documentSnapshot.getData());
                    eventList.add(event);
                };
            }
        });

        // Checks if the current user is an admin from the admins collection
        firebaseHelper.isAdmin(new Firebase.AdminCheckListener() {
            @Override
            public void onAdminCheckComplete(boolean isAdmin) {
                if (isAdmin) {
                    // User is an admin. Render admin UI
                } else {
                    // User is not an admin. Render user UI
                }
            }
        });
    }
        private void navigateBasedOnFacility() {
            // Get text from the facility input
            String facilityText = facilityInput.getText().toString().trim();

            // Check if facility field is empty
            if (facilityText.isEmpty()) {
                // Facility is empty, navigate to User UI
                Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(userIntent);
            } else {
                // Facility is filled, navigate to Organizer UI
                Intent organizerIntent = new Intent(MainActivity.this, OrganizerActivity.class);
                startActivity(organizerIntent);
            }
        }

}