package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentCreateEventBinding;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventCreateFragment extends Fragment {

    private FragmentCreateEventBinding binding;
    private Firebase firebaseHelper;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //initialize firebase
        firebaseHelper = new Firebase(requireContext());
        // button to show qr confirm dialogue
        Button addButton = binding.addEventButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = CreateEvent();
                firebaseHelper.addEvent(event);

                // inflate the fragment
                FragmentCreateQrBinding qrBinding = FragmentCreateQrBinding.inflate(LayoutInflater.from(getContext()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(qrBinding.getRoot());
                AlertDialog dialog = builder.create();

                // generate the qr code
                Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode("https://example.com", 300, 300); // Replace with your desired URL
                if (qrCodeBitmap != null) {
                    qrBinding.qrDialogueImageView.setImageBitmap(qrCodeBitmap); // Set the generated QR code to the ImageView
                }

                dialog.show();
            }

        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = CreateEvent();
                firebaseHelper.addEvent(event);
                //clear input
                ClearInput();
                // inflate the fragment
                FragmentCreateQrBinding qrBinding = FragmentCreateQrBinding.inflate(LayoutInflater.from(getContext()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(qrBinding.getRoot());
                AlertDialog dialog = builder.create();

                // generate the qr code
                Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode("https://example.com", 300, 300); // Replace with your desired URL
                if (qrCodeBitmap != null) {
                    qrBinding.qrDialogueImageView.setImageBitmap(qrCodeBitmap); // Set the generated QR code to the ImageView
                }

                dialog.show();

            }

        });
        Button cancelButton = binding.cancelEventButton;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearInput();
            }
        });

        return root;

    }
    /*TODO
    get geolocation (optional)
    price
    waitlist
    checkinID
    checkinRQ (is this the QR code??)
     */
    public Event CreateEvent(){
        //get all input data
        //hardcoded values set (change later)
        double latitude = 40.730610;
        double longitude = -73.935242;
        float price = 90.45f;
        String event_id = "eventIdPlaceholder";
        String QrCode = "QrCodePlaceholder";
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
        String EventLocation = editEventLocation.getText().toString();
        String StartDate = editStartDate.getText().toString();
        String EndDate = editEndDate.getText().toString();
        String StartTime = editStartTime.getText().toString();
        String EndTime = editEndTime.getText().toString();
        String EventDescription = editEventDescription.getText().toString();
        String eventDetails = editEventDetails.getText().toString();


        User Host = firebaseHelper.getThisUser();
        String facility = Host.getFacility();
        Event event = new Event(event_id, eventTitle, Host,EventDescription, StartDate, EndDate,
                price, facility, latitude, longitude, maxCapacity, waitlist, QrCode, checked);
        return event;
    }
    private void ClearInput(){
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