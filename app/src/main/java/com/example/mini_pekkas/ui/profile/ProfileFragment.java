package com.example.mini_pekkas.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.TextDrawable;
import com.example.mini_pekkas.databinding.FragmentProfileBinding;
/**
 * Fragment representing the user's profile screen, displaying and editing user information.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    /**
     * Inflates the profile layout and initializes the ViewModel.
     *
     * @param inflater LayoutInflater to inflate the layout.
     * @param container Container for the fragment's UI.
     * @param savedInstanceState Saved state for restoring the fragment.
     * @return The root view of the inflated layout.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(getActivity()))
                .get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return root view from binding
    }
    /**
     * Sets up the profile UI with data from the ViewModel and observes changes to update the UI.
     *
     * @param view Root view created for the fragment.
     * @param savedInstanceState Saved state for restoring the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView firstName = binding.firstName;
        final TextView lastName = binding.lastName;
        final TextView emailInput = binding.emailInput;
        final TextView phoneInput = binding.phoneInput;
        final TextView profileText = binding.profileText;
        final ImageView profileImage = binding.profileImage;
        final ImageButton editButton = binding.editButton;
        //final Switch organizerToggle = binding.organizerToggle;

        // Observe LiveData from the ViewModel
        profileViewModel.getFirstName().observe(getViewLifecycleOwner(), firstNameValue -> {
            firstName.setText(firstNameValue);
            updateProfileImageWithInitial(firstNameValue); // Update image after setting first name
        });
        profileViewModel.getLastName().observe(getViewLifecycleOwner(), lastName::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), emailInput::setText);
        profileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneInput::setText);
        profileViewModel.getPfpText().observe(getViewLifecycleOwner(), pfpText -> {
            if (pfpText != null && !pfpText.isEmpty()) {
                // Set only the first character of pfpText, capitalized
                profileText.setText(String.valueOf(pfpText.charAt(0)).toUpperCase());
                updateProfileImageWithInitial(pfpText); // Call to update profile image
            } else {
                profileText.setText("?"); // Set a default value if pfpText is empty or null
            }
        });

        //profileViewModel.getIsOrganizer().observe(getViewLifecycleOwner(), organizerToggle::setChecked);

        // Set click listener for the edit button
        editButton.setOnClickListener(v -> showEditDialog());

        // Toggle for organizer status
//        organizerToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                // Navigate to OrganizerProfileFragment when the switch is checked
//                NavController navController = Navigation.findNavController(view);
//                navController.navigate(R.id.action_navigation_profile_to_navigation_organizer_profile);
//            } else {
//
//            }
//        });
    }
    /**
     * Updates the profile image with an initial letter from the first name.
     *
     * @param firstName The first name used to generate the initial.
     */
    private void updateProfileImageWithInitial(String firstName) {
        String initial = firstName != null && !firstName.isEmpty() ? String.valueOf(firstName.charAt(0)).toUpperCase() : "?";
        TextDrawable textDrawable = new TextDrawable(initial);
        binding.profileImage.setImageDrawable(textDrawable);
    }
    /**
     * Shows a dialog allowing the user to edit their profile information.
     * Updates the ViewModel with new data and saves changes to Firebase.
     */
    private void showEditDialog() {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);


        EditText firstNameInput = dialogView.findViewById(R.id.first_name_input);
        EditText lastNameInput = dialogView.findViewById(R.id.last_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        EditText phoneInput = dialogView.findViewById(R.id.dialog_phone_input);


        firstNameInput.setText(profileViewModel.getFirstName().getValue());
        emailInput.setText(profileViewModel.getEmail().getValue());
        lastNameInput.setText(profileViewModel.getLastName().getValue());
        phoneInput.setText(profileViewModel.getPhoneNumber().getValue());

        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    profileViewModel.setFirstName(firstNameInput.getText().toString());
                    profileViewModel.setLastName(lastNameInput.getText().toString());
                    profileViewModel.setEmail(emailInput.getText().toString());
                    profileViewModel.setPhoneNumber(phoneInput.getText().toString());

                    // updates edited profile in firebase
                    profileViewModel.updateProfileInFirebase();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    /**
     * Clears the binding reference when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding reference
    }
}
