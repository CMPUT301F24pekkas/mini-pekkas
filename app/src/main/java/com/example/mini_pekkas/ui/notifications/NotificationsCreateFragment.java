package com.example.mini_pekkas.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.notification.Notifications;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentCreateNotificationBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsCreateFragment extends Fragment {

    private FragmentCreateNotificationBinding binding;
    private Spinner spinnerEvents, spinnerStatus;
    private EditText etTitle, etMessage;
    private Button btnCreateNotification;
    private ArrayList<Event> eventList;
    private ArrayAdapter<String> eventAdapter, statusAdapter;
    private Firebase firebaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI components
        spinnerEvents = root.findViewById(R.id.spinnerPriority);
        spinnerStatus = root.findViewById(R.id.spinnerPriority2);
        etTitle = root.findViewById(R.id.etNotificationTitle);
        etMessage = root.findViewById(R.id.etNotificationMessage);
        btnCreateNotification = root.findViewById(R.id.btnSubmitNotification);

        // Initialize Firebase helper
        firebaseHelper = new Firebase(requireContext());
        eventList = new ArrayList<>();
        List<String> eventNames = new ArrayList<>();
        eventAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventNames);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvents.setAdapter(eventAdapter);

        // Initialize status spinner
        List<String> statuses = List.of("organized", "pending", "waitlisted", "enrolled", "cancelled");
        statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Fetch events from Firebase
        fetchEvents();

        // Handle notification creation
        btnCreateNotification.setOnClickListener(view -> createNotification());

        return root;
    }

    /**
     * Fetch events from Firebase and populate the spinner.
     */
    private void fetchEvents() {
        firebaseHelper.getOrganizedEvents (events -> { // Retrieves all events
            if (events.isEmpty()) {
                Toast.makeText(getContext(), "No events available.", Toast.LENGTH_SHORT).show();
                return;
            }

            eventList.clear();
            eventList.addAll(events);

            List<String> eventNames = new ArrayList<>();
            for (Event event : eventList) {
                eventNames.add(event.getName()); // Assuming Event has a getName() method
            }

            eventAdapter.clear();
            eventAdapter.addAll(eventNames);
            eventAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Create a notification for the selected event and status.
     */
    private void createNotification() {
        String title = etTitle.getText().toString().trim();
        String description = etMessage.getText().toString().trim();
        String selectedEventName = (String) spinnerEvents.getSelectedItem();
        String selectedStatus = (String) spinnerStatus.getSelectedItem();

        if (title.isEmpty() || description.isEmpty() || selectedEventName == null || selectedStatus == null) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Event selectedEvent = null;
        for (Event event : eventList) {
            if (event.getName().equals(selectedEventName)) {
                selectedEvent = event;
                break;
            }
        }

        if (selectedEvent == null) {
            Toast.makeText(getContext(), "Selected event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Notifications object
        Notifications notification = new Notifications(
                title,
                description,
                new Date(), // Current date
                1, // Default priority for now; update as needed
                selectedEvent.getName()
        );

        // Push the notification to Firebase for users with the selected status
        firebaseHelper.sendEventNotificationByStatus(notification, selectedEvent, selectedStatus, new Firebase.InitializationListener() {
            @Override
            public void onInitialized() {
                Toast.makeText(getContext(), "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                clearFields();
            }

            @Override
            public void onError(Exception e) {
                Log.e("FirebaseHelper", "Error adding notification", e);
                Toast.makeText(getContext(), "Failed to create notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Clear input fields after successful submission.
     */
    private void clearFields() {
        etTitle.setText("");
        etMessage.setText("");
        if (!eventList.isEmpty()) {
            spinnerEvents.setSelection(0);
        }
        spinnerStatus.setSelection(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
