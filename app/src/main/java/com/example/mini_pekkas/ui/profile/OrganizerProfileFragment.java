package com.example.mini_pekkas.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentOrganizerProfileBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Fragment representing the organizer profile screen, displaying profile details and allowing editing.
 */
public class OrganizerProfileFragment extends Fragment {

    private FragmentOrganizerProfileBinding binding;
    private OrganizerProfileViewModel organizerProfileViewModel;

    // Define the ActivityResultLauncher as a private field
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private StorageReference profileImageRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileImageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel with factory
        organizerProfileViewModel = new ViewModelProvider(this, new OrganizerProfileViewModelFactory(getActivity()))
                .get(OrganizerProfileViewModel.class);

        // Inflate the layout using View Binding
        binding = FragmentOrganizerProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the TextView and ImageButton references
        final TextView firstName = binding.firstName;
        final TextView lastName = binding.lastName;
        final TextView emailInput = binding.emailInput;
        final TextView phoneInput = binding.phoneInput;
        final TextView organizerLocationInput = binding.organizationInput;
        final ImageView profileImage = binding.userProfileImage;
        final ImageButton editButton = binding.editButton;
        final ImageButton profileEdit = binding.pfpEdit;

        organizerProfileViewModel.getProfilePictureUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                Glide.with(this).load(url).into(profileImage);
            }
        });

        // Initialize the ActivityResultLauncher to handle the image picking result
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    uploadImageToFirebase(selectedImageUri);
                }
            }
        });

        // Observe LiveData from ViewModel and update the UI
        organizerProfileViewModel.getFirstName().observe(getViewLifecycleOwner(), firstName::setText);
        organizerProfileViewModel.getLastName().observe(getViewLifecycleOwner(), lastName::setText);
        organizerProfileViewModel.getEmail().observe(getViewLifecycleOwner(), emailInput::setText);
        organizerProfileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneInput::setText);
        organizerProfileViewModel.getOrganizerLocation().observe(getViewLifecycleOwner(), organizerLocationInput::setText);

        // Set click listener for the profile edit button
        profileEdit.setOnClickListener(v -> openGallery());

        // Set click listener for the edit button
        editButton.setOnClickListener(v -> showEditDialog());

        return root;
    }

    // Method to open the gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Define a unique name for the image
        StorageReference imageRef = profileImageRef.child(System.currentTimeMillis() + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();

                    // Update the profilePictureUrl in the ViewModel and Firebase
                    organizerProfileViewModel.setProfilePictureUrl(downloadUrl);
                    organizerProfileViewModel.updateProfileInFirebase();

                    // Load the image from URL into the ImageView
                    Glide.with(this).load(downloadUrl).into(binding.userProfileImage);

                }).addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                ));
    }

    /**
     * Shows a dialog to edit the organizer's profile information.
     * Pre-fills fields with existing values and updates the ViewModel upon saving.
     */
    private void showEditDialog() {
        // Inflate the edit dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_organizer_profile, null);

        // Access the EditText fields from the dialog layout
        EditText firstNameInput = dialogView.findViewById(R.id.first_name_input);
        EditText lastNameInput = dialogView.findViewById(R.id.last_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        EditText phoneInput = dialogView.findViewById(R.id.dialog_phone_input);
        EditText organizerLocationInput = dialogView.findViewById(R.id.dialog_organizer_input);

        // Set current values to the EditText fields
        firstNameInput.setText(organizerProfileViewModel.getFirstName().getValue());
        lastNameInput.setText(organizerProfileViewModel.getLastName().getValue());
        emailInput.setText(organizerProfileViewModel.getEmail().getValue());
        phoneInput.setText(organizerProfileViewModel.getPhoneNumber().getValue());
        organizerLocationInput.setText(organizerProfileViewModel.getOrganizerLocation().getValue());

        // Show the dialog
        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Update LiveData values in the ViewModel
                    organizerProfileViewModel.setFirstName(firstNameInput.getText().toString());
                    organizerProfileViewModel.setLastName(lastNameInput.getText().toString());
                    organizerProfileViewModel.setEmail(emailInput.getText().toString());
                    organizerProfileViewModel.setPhoneNumber(phoneInput.getText().toString());
                    organizerProfileViewModel.setOrganizerLocation(organizerLocationInput.getText().toString());

                    // Update the profile in Firebase
                    organizerProfileViewModel.updateProfileInFirebase();
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
