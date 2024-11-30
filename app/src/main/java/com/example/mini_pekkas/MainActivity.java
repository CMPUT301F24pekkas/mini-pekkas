package com.example.mini_pekkas;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mini_pekkas.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
/**
 * The MainActivity class is the entry point of the application. It checks if the user
 * already exists in Firebase and navigates to the appropriate activity based on the user's information.
 * If the user does not exist, it displays a form to create a new user.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextInputEditText facilityInput;
    private Button submitButton;
    private Firebase firebaseHelper;


    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.

                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    /**
     * Called when the activity is first created. Initializes Firebase, checks if the user
     * exists in Firebase, and either navigates to the appropriate activity or displays a form for new users.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the saved state data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase helper
        firebaseHelper = new Firebase(this);
        askNotificationPermission();    // Ask for notification permission

        // First check if this user is an admin
        firebaseHelper.isThisUserAdmin(isAdmin -> {
            if (isAdmin) {
                // User is an admin, show admin activity
                Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(adminIntent);
                finish(); // Close MainActivity
            } else {
                // User is not an admin
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
        });


    }

    /**
     * Initializes the views for the user form and sets the onClickListener for the submit button.
     * This method is only called if the user does not already exist in Firebase.
     */
    private void initializeViews() {
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        facilityInput = findViewById(R.id.facilityInput);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBasedOnFacility();
            }
        });
    }

    /**
     * Gathers the user input from the form fields, creates a temporary User object,
     * and initializes the user in Firebase. Based on the facility information,
     * this method navigates the user to either UserActivity or OrganizerActivity.
     */
    private void navigateBasedOnFacility() {
        String firstname = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String facility = facilityInput.getText().toString().trim();

        // Input validation
        if (firstname.isEmpty()) {
            firstNameInput.setError("First name cannot be empty");
            firstNameInput.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name cannot be empty");
            lastNameInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            emailInput.requestFocus();
            return;
        }

        if (!phone.matches("\\d+")) {
            phoneInput.setError("Phone number must contain only digits");
            phoneInput.requestFocus();
            return;
        }

        if (phone.length() < 10 || phone.length() > 15) {
            phoneInput.setError("Phone number must be between 10 and 15 digits");
            phoneInput.requestFocus();
            return;
        }

        // Create a map and put values into it
        Map<String, Object> map = new HashMap<>();
        map.put("name", firstname);
        map.put("lastname", lastName);
        map.put("email", email);
        map.put("phone", phone);
        map.put("facility", facility); // Facility can be empty

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