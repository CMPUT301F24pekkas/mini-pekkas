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

import org.w3c.dom.Text;

/**
 * Fragment representing the organizer's profile view, allowing users to view and edit
 * their personal details, profile picture, and other information.
 */
public class OrganizerProfileFragment extends Fragment {

    private FragmentOrganizerProfileBinding binding;
    private OrganizerProfileViewModel organizerProfileViewModel;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private StorageReference profileImageRef;
    private boolean isFacilityPicture = false;

    /**
     * Called when the fragment is created. Initializes the profile image reference in Firebase
     * and checks for required permissions on devices.
     *
     * @param savedInstanceState If the fragment is being reinitialized, this contains
     *                           the previous state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileImageRef = FirebaseStorage.getInstance().getReference("profile-pictures");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    /**
     * Inflates the profile fragment layout and binds the views with the data from the ViewModel.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment
     * @param container          The parent view to which the fragment's UI is attached
     * @param savedInstanceState Previously saved state information for the fragment
     * @return The root view of the fragment's layout
     */
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
        final TextView organizerLocationInput = binding.facilityInput;
        final ImageView profileImage = binding.userProfileImage;
        final ImageButton editButton = binding.editButton;
        final ImageButton profileEdit = binding.pfpEdit;
        final TextView facilityName = binding.facilityTitle;
        final TextView facilityDescription = binding.facilityDescription;
        final ImageView facilityImage = binding.facilityImage;
        final ImageButton facilityImageEdit = binding.facilityEditButton;

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

        organizerProfileViewModel.getFacilityPictureUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                Glide.with(this).load(url).into(facilityImage);
            } else {
                facilityImage.setImageResource(R.drawable.add_image);
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
        organizerProfileViewModel.getOrganizerLocation().observe(getViewLifecycleOwner(), facilityName::setText);
        organizerProfileViewModel.getFacilityDescription().observe(getViewLifecycleOwner(), facilityDescription::setText);


        // Set click listeners
        profileEdit.setOnClickListener(v -> {
            isFacilityPicture = false; // For profile picture
            showProfilePictureOptionsDialog();
        });
        editButton.setOnClickListener(v -> showEditDialog());
        facilityImageEdit.setOnClickListener(v -> {
            isFacilityPicture = true;
            showFacilityPictureOptionsDialog();
        });

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

    private void showFacilityPictureOptionsDialog() {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_facility_pfp_edit, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        Button chooseNewPictureButton = popupView.findViewById(R.id.choose_facility_picture_button);
        Button deletePicureButton = popupView.findViewById(R.id.delete_facility_picture_button);

        chooseNewPictureButton.setOnClickListener(v -> {
            openGallery();
            dialog.dismiss();
        });

        deletePicureButton.setOnClickListener(v -> {
            deleteFacilityPicture();
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

    private void deleteFacilityPicture() {
        organizerProfileViewModel.setFacilityPictureUrl("");
        organizerProfileViewModel.updateProfileInFirebase();
        binding.facilityImage.setImageResource(R.drawable.add_image);
        Toast.makeText(getActivity(), "Facility picture deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * Uploads a new image to Firebase Storage and updates the ViewModel with the image's URL.
     *
     * @param imageUri URI of the selected image to upload.
     */
    private void uploadImageToFirebase(Uri imageUri) {
        String path = isFacilityPicture ? "facility-pictures" : "profile-pictures";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference(path)
                .child(System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    if (isFacilityPicture) {
                        organizerProfileViewModel.setFacilityPictureUrl(downloadUrl);
                        Glide.with(this).load(downloadUrl).into(binding.facilityImage);
                    } else {
                        organizerProfileViewModel.setProfilePictureUrl(downloadUrl);
                        Glide.with(this).load(downloadUrl).into(binding.userProfileImage);
                    }
                    organizerProfileViewModel.updateProfileInFirebase();
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
        EditText facilityDescInput = dialogView.findViewById(R.id.dialog_facility_input);

        firstNameInput.setText(organizerProfileViewModel.getFirstName().getValue());
        lastNameInput.setText(organizerProfileViewModel.getLastName().getValue());
        emailInput.setText(organizerProfileViewModel.getEmail().getValue());
        phoneInput.setText(organizerProfileViewModel.getPhoneNumber().getValue());
        organizerLocationInput.setText(organizerProfileViewModel.getOrganizerLocation().getValue());
        facilityDescInput.setText(organizerProfileViewModel.getFacilityDescription().getValue());

        new AlertDialog.Builder(getActivity())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String firstName = firstNameInput.getText().toString().trim();
                    String lastName = lastNameInput.getText().toString().trim();
                    String email = emailInput.getText().toString().trim();
                    String phoneNumber = phoneInput.getText().toString().trim();
                    String organizerLocation = organizerLocationInput.getText().toString().trim();
                    String facilityDescription = facilityDescInput.getText().toString().trim();

                    // Validate inputs
                    if (firstName.isEmpty()) {
                        Toast.makeText(getActivity(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (lastName.isEmpty()) {
                        Toast.makeText(getActivity(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!phoneNumber.matches("\\d+")) {
                        Toast.makeText(getActivity(), "Phone number must contain only digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (phoneNumber.length() < 10 || phoneNumber.length() > 15) {
                        Toast.makeText(getActivity(), "Phone number must be between 10 and 15 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update profile details in ViewModel
                    organizerProfileViewModel.setFirstName(firstName);
                    organizerProfileViewModel.setLastName(lastName);
                    organizerProfileViewModel.setEmail(email);
                    organizerProfileViewModel.setPhoneNumber(phoneNumber);
                    organizerProfileViewModel.setOrganizerLocation(organizerLocation);
                    organizerProfileViewModel.setFacilityDescription(facilityDescription);
                    organizerProfileViewModel.updateProfileInFirebase();

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Called when the fragment's view is destroyed. Cleans up the binding to prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
