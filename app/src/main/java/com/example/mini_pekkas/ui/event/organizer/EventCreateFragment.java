package com.example.mini_pekkas.ui.event.organizer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.QRCodeGenerator;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentCreateEventBinding;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;
/**
 * Fragment for creating events within the organizer's UI.
 * Handles input collection for event details, poster image selection, and
 * generating QR codes for the event.
 */
public class EventCreateFragment extends Fragment {
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude = 0.0; // Default value
    private double longitude = 0.0;
    private FragmentCreateEventBinding binding;
    private Firebase firebaseHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Event createdEvent;
    /**
     * Called to initialize the fragment's view.
     * Sets up UI components for event creation, including image selection
     * and event data collection.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          The parent view that the fragment's UI will attach to
     * @param savedInstanceState Previous state information
     * @return The root view of the fragment's layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        firebaseHelper = new Firebase(requireContext());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        CheckBox geoCheckBox = binding.geoCheckBox;
        geoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestLocation();
            } else {
                latitude = 0.0;
                longitude = 0.0;
                Toast.makeText(getContext(), "Geolocation cleared.", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton posterButton = binding.addEventPicture;
        posterButton.setOnClickListener(v -> openImageChooser());

        Button addButton = binding.addEventButton;
        addButton.setOnClickListener(v -> {
            createdEvent = CreateEvent();

            if (createdEvent == null) {
                // Validation failed; return without proceeding
                return;
            }
            // Generate a unique QR code raw string (event ID + UUID)
            String uniqueQrData = createdEvent.getId() + UUID.randomUUID().toString();
            // Set the raw QR code data in the event
            createdEvent.setQrCode(uniqueQrData);

            // Generate the QR code bitmap for display purposes
            Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(uniqueQrData, 300, 300);

            if (qrCodeBitmap != null) {
                // Upload poster image (if any) and save the event
                uploadPosterImageToFirebase(createdEvent, qrCodeBitmap);
            }

            // Update live data
            organizerEventsListViewModel.addEvent(createdEvent);
        });

        Button cancelButton = binding.cancelEventButton;
        cancelButton.setOnClickListener(v -> ClearInput());

        return root;
    }
    /**
     * Requests the device's location if permissions are granted.
     * Updates the latitude and longitude fields with the current location.
     */
    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(getContext(), "Location acquired: Lat: " + latitude + ", Lng: " + longitude, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unable to fetch location.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Handles the result of the permission request for location access.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the device's image picker to select a poster image for the event.
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image picker, setting the selected image URI
     * to the ImageButton and storing it for future use.
     *
     * @param requestCode The request code passed in startActivityForResult
     * @param resultCode  The result code returned by the child activity
     * @param data        The data returned by the image picker intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.addEventPicture.setImageURI(imageUri);  // Show selected image in ImageView
        }
    }

    /**
     * Uploads the selected poster image to Firebase Storage and sets the URL in the event object.
     * If the upload is successful, saves the event and displays the QR code in a dialog.
     *
     * @param event       The event object to associate with the uploaded image
     * @param qrCodeBitmap The QR code bitmap to display in the confirmation dialog
     */
    private void uploadPosterImageToFirebase(Event event, Bitmap qrCodeBitmap) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("poster-pictures/" + UUID.randomUUID().toString());

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        event.setPosterPhotoUrl(uri.toString());
                        firebaseHelper.addEvent(event);  // Save the event in Firebase
                        showQrCodeDialog(qrCodeBitmap);
                        ClearInput();
                    }))
                    .addOnFailureListener(e -> {
                        // Handle the error
                    });
        } else {
            firebaseHelper.addEvent(event);
            showQrCodeDialog(qrCodeBitmap);
            ClearInput();
        }
    }

    /**
     * Displays a dialog with the generated QR code for the event.
     *
     * @param qrCodeBitmap The QR code bitmap to display in the dialog
     */
    private void showQrCodeDialog(Bitmap qrCodeBitmap) {
        FragmentCreateQrBinding qrBinding = FragmentCreateQrBinding.inflate(LayoutInflater.from(getContext()));
        qrBinding.qrDialogueImageView.setImageBitmap(qrCodeBitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(qrBinding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();
        //
        Button closeButton = qrBinding.getRoot().findViewById(R.id.confirmQrButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                organizerEventsListViewModel.setSelectedEvent(createdEvent);
                NavController navController = NavHostFragment.findNavController(EventCreateFragment.this);
                navController.navigate(R.id.action_add_event_to_event_details);

            }
        });
    }

    /**
     * Converts a Bitmap image to a Base64-encoded string.
     * Used for storing QR codes in the event.
     *
     * @param bitmap The bitmap to convert
     * @return The Base64-encoded string representation of the bitmap
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    /**
     * Creates a new Event object using input data collected from the UI.
     *
     * @return A populated Event object
     */
    public Event CreateEvent() {
        String event_id = UUID.randomUUID().toString();
        float price = 90.45f; // Default price; update as needed
        ArrayList<User> waitlist = new ArrayList<>();

        // Get input values
        String eventTitle = binding.createEventEditText.getText().toString().trim();
        String eventLocation = binding.createEventLocationEditText.getText().toString().trim();
        String startDateString = binding.editStartDate.getText().toString().trim();
        String endDateString = binding.editEndDate.getText().toString().trim();
        String startTime = binding.editStartTime.getText().toString().trim();
        String endTime = binding.editEndTime.getText().toString().trim();
        String eventDescription = binding.editDescription.getText().toString().trim();
        String eventDetails = binding.editDetails.getText().toString().trim();
        boolean geolocationEnabled = binding.geoCheckBox.isChecked();

        // Validate required inputs
        if (eventTitle.isEmpty()) {
            Toast.makeText(requireContext(), "Event title is required.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (eventDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Event description is required.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (startDateString.isEmpty() || endDateString.isEmpty()) {
            Toast.makeText(requireContext(), "Start and end dates are required.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (eventLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Event location is required.", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Parse dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate, endDate;
        try {
            startDate = dateFormat.parse(startDateString);
            endDate = dateFormat.parse(endDateString);

            if (startDate.after(endDate)) {
                Toast.makeText(requireContext(), "Start date must be before or equal to the end date.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (ParseException e) {
            Toast.makeText(requireContext(), "Invalid date format. Please use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Parse max participants if applicable
        int maxCapacity;
        try {
            maxCapacity = Integer.parseInt(binding.editMaxPart.getText().toString().trim());
            if (maxCapacity <= 0) {
                Toast.makeText(requireContext(), "Max participants must be greater than 0.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid max participant number.", Toast.LENGTH_SHORT).show();
            return null;
        }
        int maxWaitlist = -1;
        if (binding.maxPartCheckBox.isChecked()) {
            try {
                maxWaitlist = Integer.parseInt(binding.editMaxWait.getText().toString().trim());
                if (maxWaitlist <= 0) {
                    Toast.makeText(requireContext(), "Max waitlist must be greater than 0.", Toast.LENGTH_SHORT).show();
                    return null;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid max waitlist number.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }


        // Get host user details
        User host = firebaseHelper.getThisUser();

        // Ensure the host is not null
        if (host == null) {
            Toast.makeText(requireContext(), "Host user data is missing. Please try again.", Toast.LENGTH_SHORT).show();
            return null;
        }

        String facility = host.getFacility();

        return new Event(
                event_id,
                eventTitle,
                host,
                eventDescription,
                startDate,
                endDate,
                price,
                facility,
                latitude,
                longitude,
                maxWaitlist,
                maxCapacity,
                waitlist,
                "QrCodePlaceholder", // Replace with actual QR code
                geolocationEnabled,
                eventDetails
        );
    }

    /**
     * Clears all input fields in the event creation UI.
     */
    private void ClearInput() {
        binding.createEventEditText.getText().clear();
        binding.createEventLocationEditText.getText().clear();
        binding.editStartDate.getText().clear();
        binding.editEndDate.getText().clear();
        binding.editStartTime.getText().clear();
        binding.editEndTime.getText().clear();
        binding.editDescription.getText().clear();
        binding.editDetails.getText().clear();
        binding.editMaxPart.getText().clear();
        binding.editMaxWait.getText().clear();
        binding.maxPartCheckBox.setChecked(false);
        binding.geoCheckBox.setChecked(false);
    }

    /**
     * Cleans up the view when the fragment is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}