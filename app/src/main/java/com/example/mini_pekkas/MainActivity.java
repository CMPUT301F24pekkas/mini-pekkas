package com.example.mini_pekkas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mini_pekkas.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText facilityInput;
    private Button submitButton;
    private Firebase firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase helper
        firebaseHelper = new Firebase(this);

        // Check if the device ID already exists in Firebase
        firebaseHelper.checkThisUserExist(exist -> {
            if (!exist) {
                setContentView(R.layout.home_page);
                initializeViews();
            } else {
                // User already exists
                User epicUser = firebaseHelper.getThisUser();
                String fac = epicUser.getFacility();

                if (fac == null || fac.isEmpty()) {
                    // Facility is null, navigate to UserActivity
                    Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(userIntent);
                } else {
                    // Facility is not null, navigate to OrganizerActivity
                    Intent organizerIntent = new Intent(MainActivity.this, OrganizerActivity.class);
                    startActivity(organizerIntent);
                }
                finish(); // Close MainActivity
            }
        });
    }

    private void initializeViews() {
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
    }

    private void navigateBasedOnFacility() {
        String firstname = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String facility = facilityInput.getText().toString().trim();

        // Create a map and put values into it
        Map<String, Object> map = new HashMap<>();
        map.put("name", firstname);
        map.put("lastName", lastName);
        map.put("email", email);
        map.put("facility", facility);

        User tempUser = new User(map);

        // Initialize user in Firebase
        firebaseHelper.InitializeThisUser(tempUser, () -> {
            // User successfully initialized, now navigate
            if (facility.isEmpty()) {
                Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(userIntent);
            } else {
                Intent organizerIntent = new Intent(MainActivity.this, OrganizerActivity.class);
                startActivity(organizerIntent);
            }
            finish(); // Close MainActivity
        });
    }
}
