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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentOrganizerProfileBinding;

public class OrganizerProfileFragment extends Fragment {

    private FragmentOrganizerProfileBinding binding;
    private OrganizerProfileViewModel organizerProfileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        organizerProfileViewModel = new ViewModelProvider(this).get(OrganizerProfileViewModel.class);

        binding = FragmentOrganizerProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView firstName = binding.firstName;
        final TextView lastName = binding.lastName;
        final TextView emailInput = binding.emailInput;
        final TextView phoneInput = binding.phoneInput;
        final TextView organizerLocationInput = binding.organizationInput; // New TextView for organizer location
        final ImageView profileImage = binding.userProfileImage;
        final ImageButton editButton = binding.editButton;
        final Switch organizerToggle = binding.organizerToggle;

        // Observe LiveData
        organizerProfileViewModel.getFirstName().observe(getViewLifecycleOwner(), firstName::setText);
        organizerProfileViewModel.getLastName().observe(getViewLifecycleOwner(), lastName::setText);
        organizerProfileViewModel.getEmail().observe(getViewLifecycleOwner(), emailInput::setText);
        organizerProfileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneInput::setText);
        organizerProfileViewModel.getIsOrganizer().observe(getViewLifecycleOwner(), organizerToggle::setChecked);
        organizerProfileViewModel.getOrganizerLocation().observe(getViewLifecycleOwner(), organizerLocationInput::setText); // Observe organizer location

        editButton.setOnClickListener(v -> showEditDialog());

        organizerToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            organizerProfileViewModel.setIsOrganizer(isChecked);
        });

        return root;
    }

    private void showEditDialog() {
        // dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        // Access the EditText fields from the dialog layout
        EditText firstNameInput = dialogView.findViewById(R.id.first_name_input);
        EditText lastNameInput = dialogView.findViewById(R.id.last_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        EditText phoneInput = dialogView.findViewById(R.id.dialog_phone_input);
        EditText organizerLocationInput = dialogView.findViewById(R.id.dialog_organizer_input); // New EditText for organizer location

        // Set current values to the EditText fields
        firstNameInput.setText(organizerProfileViewModel.getFirstName().getValue());
        lastNameInput.setText(organizerProfileViewModel.getLastName().getValue());
        emailInput.setText(organizerProfileViewModel.getEmail().getValue());
        phoneInput.setText(organizerProfileViewModel.getPhoneNumber().getValue());
        organizerLocationInput.setText(organizerProfileViewModel.getOrganizerLocation().getValue()); // Set organizer location

        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // New values
                    organizerProfileViewModel.setFirstName(firstNameInput.getText().toString());
                    organizerProfileViewModel.setLastName(lastNameInput.getText().toString());
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
        binding = null;
    }
}
