package com.example.mini_pekkas.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        // Inflate the layout using ViewBinding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Access and set the values for the views
        final TextView fullName = binding.fullName;
        final TextView userId = binding.userId;
        final ImageView profileImage = binding.profileImage;
        final TextView contactInfoLabel = binding.contactInfoLabel;
        final Switch organizerToggle = binding.organizerToggle;
        final ImageButton editButton = binding.editButton;

        // Observing ViewModel for text updates
        profileViewModel.getText().observe(getViewLifecycleOwner(), fullName::setText);

        // Set other static or dynamic values as necessary
        userId.setText("Name123"); // Set to dynamic value if available
        contactInfoLabel.setText("Contact Info");

        // You can also add click listeners for buttons like the edit button or toggle switch
        editButton.setOnClickListener(v -> {
            // Handle edit button click event
        });

        organizerToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle switch toggle
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
