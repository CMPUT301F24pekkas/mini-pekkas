package com.example.mini_pekkas.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.mini_pekkas.databinding.FragmentOrganizerProfileBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Fragment representing the organizer's profile view, allowing users to view and edit
 * their personal details, profile picture, and other information.
 */
public class OrganizerProfileFragment extends Fragment {

    private FragmentOrganizerProfileBinding binding;
    private OrganizerProfileViewModel organizerProfileViewModel;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private StorageReference profileImageRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileImageRef = FirebaseStorage.getInstance().getReference("profile_pictures");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        organizerProfileViewModel = new ViewModelProvider(this, new OrganizerProfileViewModelFactory(getActivity()))
                .get(OrganizerProfileViewModel.class);

        binding = FragmentOrganizerProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize profile elements
        final TextView firstName = binding.firstName;
        final TextView lastName = binding.lastName;
        final TextView emailInput = binding.emailInput;
        final TextView phoneInput = binding.phoneInput;
        final TextView profileText = binding.profileText;
        final TextView organizerLocationInput = binding.organizationInput;
        final ImageView profileImage = binding.userProfileImage;
        final ImageButton editButton = binding.editButton;
        final ImageButton profileEdit = binding.pfpEdit;

        // Load profile picture or show initial text
        organizerProfileViewModel.getProfilePictureUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                Glide.with(this).load(url).into(profileImage);
                profileText.setVisibility(View.GONE);
            } else {
                String name = organizerProfileViewModel.getFirstName().getValue();
                if (name != null && !name.isEmpty()) {
                    profileText.setText(String.valueOf(name.charAt(0)).toUpperCase());
                    profileText.setVisibility(View.VISIBLE);
                }
            }
        });

        // Initialize the image picker
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    uploadImageToFirebase(selectedImageUri);
                }
            }
        });

        organizerProfileViewModel.getFirstName().observe(getViewLifecycleOwner(), name -> {
            firstName.setText(name);
            if (organizerProfileViewModel.getProfilePictureUrl().getValue() == null || organizerProfileViewModel.getProfilePictureUrl().getValue().isEmpty()) {
                if (name != null && !name.isEmpty()) {
                    profileText.setText(String.valueOf(name.charAt(0)).toUpperCase());
                    profileText.setVisibility(View.VISIBLE);
                } else {
                    profileText.setVisibility(View.GONE);
                }
            }
        });


        // Bind profile data to views
        organizerProfileViewModel.getFirstName().observe(getViewLifecycleOwner(), firstName::setText);
        organizerProfileViewModel.getLastName().observe(getViewLifecycleOwner(), lastName::setText);
        organizerProfileViewModel.getEmail().observe(getViewLifecycleOwner(), emailInput::setText);
        organizerProfileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneInput::setText);
        organizerProfileViewModel.getOrganizerLocation().observe(getViewLifecycleOwner(), organizerLocationInput::setText);

        // Set click listeners
        profileEdit.setOnClickListener(v -> showProfilePictureOptionsDialog());
        editButton.setOnClickListener(v -> showEditDialog());

        return root;
    }

    /**
     * Shows a dialog allowing the user to choose between uploading a new profile picture or deleting the current one.
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
     * Opens the device's gallery for selecting a new profile picture.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    /**
     * Deletes the current profile picture, resets the profile image to a placeholder, and updates Firebase.
     */
    private void deleteProfilePicture() {
        organizerProfileViewModel.setProfilePictureUrl("");
        organizerProfileViewModel.updateProfileInFirebase();
        binding.userProfileImage.setImageResource(R.drawable.user_picture);
        String name = organizerProfileViewModel.getFirstName().getValue();
        if (name != null && !name.isEmpty()) {
            binding.profileText.setText(String.valueOf(name.charAt(0)).toUpperCase());
            binding.profileText.setVisibility(View.VISIBLE);
        }
        Toast.makeText(getActivity(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * Uploads a new profile image to Firebase Storage and updates the ViewModel with the image's URL.
     *
     * @param imageUri URI of the selected image to upload.
     */
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference imageRef = profileImageRef.child(System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    organizerProfileViewModel.setProfilePictureUrl(downloadUrl);
                    organizerProfileViewModel.updateProfileInFirebase();
                    Glide.with(this).load(downloadUrl).into(binding.userProfileImage);
                }).addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                ));
    }

    /**
     * Displays a dialog to edit organizer profile details including name, email, phone number, and location.
     */
    private void showEditDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_organizer_profile, null);

        EditText firstNameInput = dialogView.findViewById(R.id.first_name_input);
        EditText lastNameInput = dialogView.findViewById(R.id.last_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        EditText phoneInput = dialogView.findViewById(R.id.dialog_phone_input);
        EditText organizerLocationInput = dialogView.findViewById(R.id.dialog_organizer_input);

        firstNameInput.setText(organizerProfileViewModel.getFirstName().getValue());
        lastNameInput.setText(organizerProfileViewModel.getLastName().getValue());
        emailInput.setText(organizerProfileViewModel.getEmail().getValue());
        phoneInput.setText(organizerProfileViewModel.getPhoneNumber().getValue());
        organizerLocationInput.setText(organizerProfileViewModel.getOrganizerLocation().getValue());

        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    organizerProfileViewModel.setFirstName(firstNameInput.getText().toString());
                    organizerProfileViewModel.setLastName(lastNameInput.getText().toString());
                    organizerProfileViewModel.setEmail(emailInput.getText().toString());
                    organizerProfileViewModel.setPhoneNumber(phoneInput.getText().toString());
                    organizerProfileViewModel.setOrganizerLocation(organizerLocationInput.getText().toString());
                    organizerProfileViewModel.updateProfileInFirebase();
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
