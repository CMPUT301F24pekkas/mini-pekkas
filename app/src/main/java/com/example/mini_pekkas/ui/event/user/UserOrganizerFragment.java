package com.example.mini_pekkas.ui.event.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.OrganizerActivity;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentUserOrganizerBinding;

public class UserOrganizerFragment extends Fragment {

    private FragmentUserOrganizerBinding binding;
    private Firebase firebaseHelper;
    private EditText facilityEditText;
    private Button confirmFacilityButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentUserOrganizerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase helper
        firebaseHelper = new Firebase(requireActivity());

        // Initialize UI elements
        facilityEditText = root.findViewById(R.id.newFacilityInput);
        confirmFacilityButton = root.findViewById(R.id.confirmNewFacilityButton);

        // Handle the button click to update the facility
        confirmFacilityButton.setOnClickListener(v -> {
            String newFacility = facilityEditText.getText().toString().trim();

            if (newFacility.isEmpty()) {
                Toast.makeText(requireContext(), "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the current user
            User currentUser = firebaseHelper.getThisUser();

            if (currentUser != null) {
                // Set the new facility in the user object
                currentUser.setFacility(newFacility);

                // Update the user in Firebase
                firebaseHelper.updateThisUser(currentUser, new Firebase.InitializationListener() {
                    @Override
                    public void onInitialized() {
                        // Notify the user that the update was successful
                        Toast.makeText(requireContext(), "Facility updated successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to OrganizerActivity
                        Intent organizerIntent = new Intent(requireActivity(), OrganizerActivity.class);
                        startActivity(organizerIntent);
                        requireActivity().finish();  // Close the current fragment
                    }

                    @Override
                    public void onError(Exception e) {
                        // Show error message
                        Toast.makeText(requireContext(), "Error updating facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Handle case where user is not found
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
