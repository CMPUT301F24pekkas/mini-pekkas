package com.example.mini_pekkas.ui.notifications;

import android.os.Bundle;
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
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentCreateNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsCreateFragment extends Fragment {

    private FragmentCreateNotificationBinding binding;
    private Spinner spinnerEvents;
    private EditText etTitle, etMessage;
    private Button btnCreateNotification;
    private ArrayList<Event> eventList;
    private ArrayAdapter<String> eventAdapter;
    private Firebase firebaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI components
        spinnerEvents = root.findViewById(R.id.spinnerPriority);
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

        // Fetch events from Firebase
        fetchEvents();

        // Handle notification creation
        //btnCreateNotification.setOnClickListener(view -> createNotification());



        return root;
    }

    /**
     * Fetch events from Firebase and populate the spinner.
     */
    private void fetchEvents() {
        firebaseHelper.searchAllEvents(events -> {     // TODO This retrieves every single event, even ones the user doesn't own - ryan
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
     * Create a notification for the selected event.
     */
//    private void createNotification() {
//        String title = etTitle.getText().toString().trim();
//        String description = etMessage.getText().toString().trim();
//        String selectedEvent = (String) spinnerEvents.getSelectedItem();
//
//        if (title.isEmpty() || description.isEmpty() || selectedEvent == null) {
//            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a Notifications object
//        Notifications notification = new Notifications(
//                title,
//                description,
//                new Date(), // Current date
//                1, // Default priority for now; update as needed
//                selectedEvent // Using selected event as fragment destination
//        );
//
//        // Push the notification to Firebase
//        firebaseHelper.addNotification(notification, new Firebase.DataRetrievalListener() {
//            @Override
//            public void onRetrievalCompleted(String id) {
//                Log.d("FirebaseHelper", "Notification added successfully with ID: " + id);
//                Toast.makeText(getContext(), "Notification created successfully with ID: " + id, Toast.LENGTH_SHORT).show();
//                clearFields();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("FirebaseHelper", "Error adding notification", e);
//                Toast.makeText(getContext(), "Failed to create notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }



    /**
     * Clear input fields after successful submission.
     */
    private void clearFields() {
        etTitle.setText("");
        etMessage.setText("");
        if (!eventList.isEmpty()) {
            spinnerEvents.setSelection(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
