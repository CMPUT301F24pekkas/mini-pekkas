package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // button to show qr confirm dialogue
        Button addButton = binding.addEventButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Event event = CreateEvent(v);

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

        return root;
    }
    /*TODO
    get geolocation (optional)
    price
    waitlist
    checkinID
    checkinRQ (is this the QR code??)
     */
    public Event CreateEvent(View view){
        //get all input data
        //hardcoded values set (change later)
        double latitude = 40.730610;
        double longitude = -73.935242;
        float price = 90.45f;
        String event_id = "eventIdPlaceholder";
        String QrCode = "QrCodePlaceholder";
        ArrayList<User> waitlist = new ArrayList<>();

        EditText editEventTitle = view.findViewById(R.id.createEventEditText);
        EditText editEventLocation = view.findViewById(R.id.createEventLocationEditText);
        EditText editStartDate = view.findViewById(R.id.editStartDate);
        EditText editEndDate = view.findViewById(R.id.editEndDate);
        EditText editStartTime = view.findViewById(R.id.editStartTime);
        EditText editEndTime = view.findViewById(R.id.editEndTime);
        EditText editEventDescription = view.findViewById(R.id.editDescription);
        EditText editEventDetails = view.findViewById(R.id.editDetails);

        CheckBox checkGeo = view.findViewById(R.id.geoCheckBox);
        boolean geo = false;
        if (checkGeo.isChecked()) {
            geo = true;
        }

        CheckBox checkMaxCapacity = view.findViewById(R.id.maxPartCheckBox);
        int maxCapacity = -1;
        if (checkMaxCapacity.isChecked()) {
            EditText editMaxCapacity = view.findViewById(R.id.editMaxPart);
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

        Firebase firebaseHelper = new Firebase(view.getContext());
        User Host = firebaseHelper.getThisUser();
        String facility = Host.getFacility();
        Event event = new Event(event_id, eventTitle, Host,EventDescription, StartDate, EndDate,
                price, facility, latitude, longitude, maxCapacity, waitlist, QrCode, geo);
        return event;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}