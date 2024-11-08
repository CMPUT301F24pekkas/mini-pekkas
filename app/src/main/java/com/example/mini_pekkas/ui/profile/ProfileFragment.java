package com.example.mini_pekkas.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentProfileBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Fragment that handles the user's profile display and management.
 * It allows the user to view and edit their profile information and profile picture.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private StorageReference profileImageRef;

    /**
     * Initializes the fragment, including setting up Firebase storage reference
     * and checking for notification permission on devices with SDK TIRAMISU or higher.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileImageRef = FirebaseStorage.getInstance().getReference("profile_pictures");

        // Check if notification permission is granted (for devices with SDK TIRAMISU or higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    /**
     * Inflates the view for the profile fragment and sets up observers to update UI elements
     * based on LiveData values from the ViewModel.
     *
     * @param inflater The LayoutInflater used to inflate the view.
     * @param container The container that holds the fragment's UI.
     * @param savedInstanceState The saved instance state for restoring UI state.
     * @return The root view for this fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(getActivity()))
                .get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // UI elements
        final TextView firstName = binding.firstName;
        final TextView lastName = binding.lastName;
        final TextView emailInput = binding.emailInput;
        final TextView phoneInput = binding.phoneInput;
        final TextView profileText = binding.profileText;
        final ImageView profileImage = binding.profileImage;
        final ImageButton editButton = binding.editButton;
        final ImageButton profileEdit = binding.pfpEdit;

        // Observe the profile picture URL from the ViewModel
        profileViewModel.getProfilePictureUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                Glide.with(this).load(url).into(profileImage);
                profileText.setVisibility(View.GONE);
            } else {
                String name = profileViewModel.getFirstName().getValue();
                if (name != null && !name.isEmpty()) {
                    profileText.setText(String.valueOf(name.charAt(0)).toUpperCase());
                    profileText.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set up image picker launcher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    uploadImageToFirebase(selectedImageUri);
                }
            }
        });

        // Set up observers for other profile details
        profileViewModel.getFirstName().observe(getViewLifecycleOwner(), firstName::setText);
        profileViewModel.getLastName().observe(getViewLifecycleOwner(), lastName::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), emailInput::setText);
        profileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneInput::setText);

        // Set up listeners for profile edit and profile picture options
        profileEdit.setOnClickListener(v -> showProfilePictureOptionsDialog());
        editButton.setOnClickListener(v -> showEditDialog());

        return root;
    }

    /**
     * Shows a dialog with options to either choose a new profile picture or delete the current one.
     */
    private void showProfilePictureOptionsDialog() {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_pfp_edit_delete, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        Button chooseNewPictureButton = popupView.findViewById(R.id.choose_new_picture_button);
        Button deletePictureButton = popupView.findViewById(R.id.delete_picture_button);

        chooseNewPictureButton.setOnClickListener(v -> {
            openGallery();
            dialog.dismiss();
        });

        deletePictureButton.setOnClickListener(v -> {
            deleteProfilePicture();
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Opens the device's gallery to allow the user to choose a new profile picture.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    /**
     * Deletes the current profile picture and resets the profile picture to a default image.
     */
    private void deleteProfilePicture() {
        profileViewModel.setProfilePictureUrl("");
        profileViewModel.updateProfileInFirebase();
        binding.profileImage.setImageResource(R.drawable.user_picture);
        String name = profileViewModel.getFirstName().getValue();
        if (name != null && !name.isEmpty()) {
            binding.profileText.setText(String.valueOf(name.charAt(0)).toUpperCase());
            binding.profileText.setVisibility(View.VISIBLE);
        }
        Toast.makeText(getActivity(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * Uploads a new profile picture to Firebase Storage and updates the profile with the image URL.
     *
     * @param imageUri The URI of the image to be uploaded.
     */
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference imageRef = profileImageRef.child(System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    profileViewModel.setProfilePictureUrl(downloadUrl);
                    profileViewModel.updateProfileInFirebase();
                    Glide.with(this).load(downloadUrl).into(binding.profileImage);
                }).addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                ));
    }

    /**
     * Displays a dialog allowing the user to edit their profile information.
     */
    private void showEditDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        EditText firstNameInput = dialogView.findViewById(R.id.dialogue_first_name_input);
        EditText lastNameInput = dialogView.findViewById(R.id.dialogue_last_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        EditText phoneInput = dialogView.findViewById(R.id.dialog_phone_input);

        firstNameInput.setText(profileViewModel.getFirstName().getValue());
        lastNameInput.setText(profileViewModel.getLastName().getValue());
        emailInput.setText(profileViewModel.getEmail().getValue());
        phoneInput.setText(profileViewModel.getPhoneNumber().getValue());

        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    profileViewModel.setFirstName(firstNameInput.getText().toString());
                    profileViewModel.setLastName(lastNameInput.getText().toString());
                    profileViewModel.setEmail(emailInput.getText().toString());
                    profileViewModel.setPhoneNumber(phoneInput.getText().toString());
                    profileViewModel.updateProfileInFirebase();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Cleans up resources when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
