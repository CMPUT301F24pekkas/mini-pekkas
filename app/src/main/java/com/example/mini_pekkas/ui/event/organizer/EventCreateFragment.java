package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.QRCodeGenerator;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentCreateEventBinding;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Fragment for creating events within the organizer's UI.
 * Handles input collection for event details, poster image selection, and
 * generating QR codes for the event.
 */
public class EventCreateFragment extends Fragment {

    private FragmentCreateEventBinding binding;
    private Firebase firebaseHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

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

        // Initialize Firebase helper
        firebaseHelper = new Firebase(requireContext());

        // Set up poster image selection
        ImageButton posterButton = binding.imageView;
        posterButton.setOnClickListener(v -> openImageChooser());

        // Button to create event and show QR code confirmation dialog
        Button addButton = binding.addEventButton;
        addButton.setOnClickListener(v -> {
            // Create the event with user input data
            Event event = CreateEvent();

            // Generate a unique QR code using the event ID + a new UUID
            String uniqueQrData = event.getId() + "_" + UUID.randomUUID().toString();
            Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(uniqueQrData, 300, 300);

            if (qrCodeBitmap != null) {
                // Convert QR code bitmap to Base64 string and set it in the event object
                String qrCodeBase64 = bitmapToBase64(qrCodeBitmap);
                event.setQrCode(qrCodeBase64);

                // Upload poster image if available, then save the event
                uploadPosterImageToFirebase(event, qrCodeBitmap);
            }
        });

        // Button to cancel event creation and clear inputs
        Button cancelButton = binding.cancelEventButton;
        cancelButton.setOnClickListener(v -> ClearInput());

        return root;
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
            binding.imageView.setImageURI(imageUri);  // Show selected image in ImageView
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
                        event.setPosterPhotoUrl(uri.toString());  // Set the poster URL in the event
                        firebaseHelper.addEvent(event);           // Save the event with poster URL in Firebase

                        // Show the QR code in a dialog after saving
                        showQrCodeDialog(qrCodeBitmap);
                        ClearInput();
                    }))
                    .addOnFailureListener(e -> {
                        // Handle the error, e.g., show a message to the user
                    });
        } else {
            // Save event without a poster URL if no image is selected
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
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Creates a new Event object using input data collected from the UI.
     *
     * @return A populated Event object
     */
    public Event CreateEvent() {
        String event_id = UUID.randomUUID().toString();
        double latitude = 40.730610; // Example latitude
        double longitude = -73.935242; // Example longitude
        float price = 90.45f;
        ArrayList<User> waitlist = new ArrayList<>();

        String eventTitle = binding.createEventEditText.getText().toString();
        String eventLocation = binding.createEventLocationEditText.getText().toString();
        String startDate = binding.editStartDate.getText().toString();
        String endDate = binding.editEndDate.getText().toString();
        String startTime = binding.editStartTime.getText().toString();
        String endTime = binding.editEndTime.getText().toString();
        String eventDescription = binding.editDescription.getText().toString();
        String eventDetails = binding.editDetails.getText().toString();
        boolean checked = binding.geoCheckBox.isChecked();

        int maxCapacity = -1;
        if (binding.maxPartCheckBox.isChecked()) {
            maxCapacity = Integer.parseInt(binding.editMaxPart.getText().toString());
        }

        User host = firebaseHelper.getThisUser();
        String facility = host.getFacility();

        return new Event(event_id, eventTitle, host, eventDescription, startDate, endDate, price,
                facility, latitude, longitude, maxCapacity, waitlist, "QrCodePlaceholder", checked, "");
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