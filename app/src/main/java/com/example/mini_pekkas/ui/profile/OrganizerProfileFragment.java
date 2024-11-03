package com.example.mini_pekkas.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentOrganizerProfileBinding;

public class OrganizerProfileFragment extends Fragment {

    private FragmentOrganizerProfileBinding binding;
    private OrganizerProfileViewModel organizerProfileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        organizerProfileViewModel = new ViewModelProvider(this, new OrganizerProfileViewModelFactory(getActivity()))
                .get(OrganizerProfileViewModel.class);
        binding = FragmentOrganizerProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView firstName = binding.firstName;
        final TextView lastName = binding.lastName;
        final TextView emailInput = binding.emailInput;
        final TextView phoneInput = binding.phoneInput;
        final TextView organizerLocationInput = binding.organizationInput; // New TextView for organizer location
        final ImageView profileImage = binding.userProfileImage;
        final ImageButton editButton = binding.editButton;
        //final Switch organizerToggle = binding.organizerToggle;

        // Observe LiveData
        organizerProfileViewModel.getFirstName().observe(getViewLifecycleOwner(), firstName::setText);
        organizerProfileViewModel.getLastName().observe(getViewLifecycleOwner(), lastName::setText);
        organizerProfileViewModel.getEmail().observe(getViewLifecycleOwner(), emailInput::setText);
        organizerProfileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneInput::setText);
        // organizerProfileViewModel.getIsOrganizer().observe(getViewLifecycleOwner(), organizerToggle::setChecked);
        organizerProfileViewModel.getOrganizerLocation().observe(getViewLifecycleOwner(), organizerLocationInput::setText); // Observe organizer location

        // Set click listener for the edit button
        editButton.setOnClickListener(v -> showEditDialog());

        // Toggle for organizer status
//        organizerToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (!isChecked) {
//                // Navigate back to ProfileFragment when the switch is unchecked
//                NavController navController = Navigation.findNavController(requireView());
//                navController.navigate(R.id.navigation_organizer_profile_to_navigation_profile);
//            } else {
//                // Optionally, you could perform some action when toggled on
////                organizerProfileViewModel.setIsOrganizer(isChecked);
//            }
//        });

        return root;
    }

    private void showEditDialog() {
        // correct layout for the organizer profile dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_organizer_profile, null); // Inflate the correct layout

        // access the EditText fields from the dialog layout
        EditText firstNameInput = dialogView.findViewById(R.id.first_name_input);
        EditText lastNameInput = dialogView.findViewById(R.id.last_name_input); // This needs to be added to your XML
        EditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        EditText phoneInput = dialogView.findViewById(R.id.dialog_phone_input);
        EditText organizerLocationInput = dialogView.findViewById(R.id.dialog_organizer_input);

        // set current values to the EditText fields
        firstNameInput.setText(organizerProfileViewModel.getFirstName().getValue());
        lastNameInput.setText(organizerProfileViewModel.getLastName().getValue()); // Set last name value
        emailInput.setText(organizerProfileViewModel.getEmail().getValue());
        phoneInput.setText(organizerProfileViewModel.getPhoneNumber().getValue());
        organizerLocationInput.setText(organizerProfileViewModel.getOrganizerLocation().getValue());

        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
    
                    organizerProfileViewModel.setFirstName(firstNameInput.getText().toString());
                    organizerProfileViewModel.setLastName(lastNameInput.getText().toString()); // Save last name
                    organizerProfileViewModel.setEmail(emailInput.getText().toString());
                    organizerProfileViewModel.setPhoneNumber(phoneInput.getText().toString());
                    organizerProfileViewModel.setOrganizerLocation(organizerLocationInput.getText().toString()); // Save organizer location
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding reference
    }
}
