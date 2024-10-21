package com.example.mini_pekkas.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.mini_pekkas.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);


        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final TextView firstName = binding.firstName;
        final TextView lastName = binding.lastName;
        final TextView emailInput = binding.emailInput;
        final TextView phoneInput = binding.phoneInput;
        final ImageView profileImage = binding.profileImage;
        final ImageButton editButton = binding.editButton;
        final Switch organizerToggle = binding.organizerToggle;


        profileViewModel.getFullName().observe(getViewLifecycleOwner(), firstName::setText);
        profileViewModel.getUserId().observe(getViewLifecycleOwner(), lastName::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), emailInput::setText);
        profileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneInput::setText);
        profileViewModel.getIsOrganizer().observe(getViewLifecycleOwner(), organizerToggle::setChecked);


        editButton.setOnClickListener(v -> showEditDialog());

        organizerToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            profileViewModel.setIsOrganizer(isChecked);
        });

        return root;
    }

    private void showEditDialog() {
        // dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        // acesses the EditText fields from the dialog layout
        EditText fullNameInput = dialogView.findViewById(R.id.full_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        EditText phoneInput = dialogView.findViewById(R.id.dialog_phone_input);

        // set current values to the EditText fields
        fullNameInput.setText(profileViewModel.getFullName().getValue());
        emailInput.setText(profileViewModel.getEmail().getValue());
        phoneInput.setText(profileViewModel.getPhoneNumber().getValue());


        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // new values
                    profileViewModel.setFullName(fullNameInput.getText().toString());
                    profileViewModel.setEmail(emailInput.getText().toString());
                    profileViewModel.setPhoneNumber(phoneInput.getText().toString());
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
