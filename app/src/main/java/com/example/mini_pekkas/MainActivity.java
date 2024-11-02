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
