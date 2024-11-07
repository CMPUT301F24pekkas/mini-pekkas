package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.QRCodeGenerator;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentCreateEventBinding;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EventCreateFragment extends Fragment {

    private FragmentCreateEventBinding binding;
    private Firebase firebaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase
        firebaseHelper = new Firebase(requireContext());

        // Button to create event and show QR code confirmation dialog
        Button addButton = binding.addEventButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = CreateEvent();

                // Generate the QR code
                Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode("https://example.com", 300, 300); // Replace with your desired URL

                if (qrCodeBitmap != null) {
                    // Convert QR code bitmap to Base64 string and set it as QrCode
                    String qrCodeBase64 = bitmapToBase64(qrCodeBitmap);
                    event.setQrCode(qrCodeBase64);

                    // Save the event to Firebase
                    firebaseHelper.addEvent(event);

                    // Clear input fields
                    ClearInput();

                    // Show the QR code in a dialog
                    FragmentCreateQrBinding qrBinding = FragmentCreateQrBinding.inflate(LayoutInflater.from(getContext()));
                    qrBinding.qrDialogueImageView.setImageBitmap(qrCodeBitmap);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(qrBinding.getRoot());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        // Button to cancel event creation and clear inputs
        Button cancelButton = binding.cancelEventButton;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearInput();
            }
        });

        return root;
    }

    // Converts Bitmap to Base64 string for storage
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Creates a new Event object from user inputs
    public Event CreateEvent() {
        // Get all input data (hardcoded values for demo, change as needed)
        double latitude = 40.730610;
        double longitude = -73.935242;
        float price = 90.45f;
        String event_id = "eventIdPlaceholder";
        ArrayList<User> waitlist = new ArrayList<>();

        EditText editEventTitle = binding.createEventEditText;
        EditText editEventLocation = binding.createEventLocationEditText;
        EditText editStartDate = binding.editStartDate;
        EditText editEndDate = binding.editEndDate;
        EditText editStartTime = binding.editStartTime;
        EditText editEndTime = binding.editEndTime;
        EditText editEventDescription = binding.editDescription;
        EditText editEventDetails = binding.editDetails;
        CheckBox checkGeo = binding.geoCheckBox;

        boolean checked = checkGeo.isChecked();

        CheckBox checkMaxCapacity = binding.maxPartCheckBox;
        int maxCapacity = -1;
        EditText editMaxCapacity = binding.editMaxPart;
        if (checkMaxCapacity.isChecked()) {
            String strMaxCapacity = editMaxCapacity.getText().toString();
            maxCapacity = Integer.parseInt(strMaxCapacity);
        }

        String eventTitle = editEventTitle.getText().toString();
        String eventLocation = editEventLocation.getText().toString();
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();
        String startTime = editStartTime.getText().toString();
        String endTime = editEndTime.getText().toString();
        String eventDescription = editEventDescription.getText().toString();
        String eventDetails = editEventDetails.getText().toString();

        User host = firebaseHelper.getThisUser();
        String facility = host.getFacility();

        // Create Event object with QR code placeholder
        Event event = new Event(event_id, eventTitle, host, eventDescription, startDate, endDate,
                price, facility, latitude, longitude, maxCapacity, waitlist, "QrCodePlaceholder", checked, null);
        return event;
    }

    // Clears input fields in the UI
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
