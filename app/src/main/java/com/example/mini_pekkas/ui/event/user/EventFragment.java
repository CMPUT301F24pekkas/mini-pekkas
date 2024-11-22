package com.example.mini_pekkas.ui.event.user;

import static org.junit.Assert.assertEquals;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mini_pekkas.Firebase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentEventBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private User mockUser;
    private EventViewModel eventViewModel;
    private Firebase firebaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        firebaseHelper = new Firebase(requireContext());


        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedEventViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedEventViewModel.class);
        sharedViewModel.getQrCodeData().observe(getViewLifecycleOwner(), qrCodeData -> {
            if (qrCodeData != null) {
                fetchEventFromFirebase(qrCodeData, sharedViewModel);
            }
        });
        sharedViewModel.getEventDetails().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                binding.eventNameView.setText(event.getName());
                binding.organizerNameView.setText(event.getEventHost().getName());
                binding.eventDescriptionView.setText(event.getDescription());
            }
        });
//        // binds the text views to the view model
//        final TextView eventNameView = binding.eventNameView;
//        final TextView organizerNameView = binding.organizerNameView;
//        final TextView locationView = binding.locationView;
//        final TextView eventDescriptionView = binding.eventDescriptionView;
//        final TextView eventDetailsView = binding.eventDetailsView;
//
//        // updates text views when they are changed
//        eventViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
//        eventViewModel.getOrganizerName().observe(getViewLifecycleOwner(), organizerNameView::setText);
//        eventViewModel.getLocation().observe(getViewLifecycleOwner(), locationView::setText);
//        eventViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
//        eventViewModel.getEventDetails().observe(getViewLifecycleOwner(), eventDetailsView::setText);
//
//        // binds the images to the view model
//        final ImageView eventImage = binding.eventImageView;
//        ImageView qrImageView = root.findViewById(R.id.qrImage);
//        eventViewModel.getEventImage().observe(getViewLifecycleOwner(), eventImage::setImageResource);
//
//
//        /**
//         * Fetches the event associated with the provided Base64-encoded QR code from Firebase.
//         *
//         * @param base64QRCode The Base64-encoded QR code string.
//         */
//        private void fetchEventFromFirebase(String qrCodeData) {
//            Log.d("CameraFragment", "Fetching event with raw QR data: " + qrCodeData);
//            firebaseHelper.getEventByQRCode(qrCodeData, new Firebase.EventRetrievalListener() {
//                @Override
//                public void onEventRetrievalCompleted(Event event) {
//                    if (event != null) {
//                        Log.d("CameraFragment", "Event found: " + event.getName());
//                        Toast.makeText(getContext(), "Event Found: " + event.getName(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.d("CameraFragment", "No event found for this QR code");
//                        Toast.makeText(getContext(), "No event found for this QR code", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    Log.e("CameraFragment", "Error retrieving event: " + e.getMessage());
//                }
//            });
//        }
//
//
//        firebaseHelper.collection("events")
//                .whereEqualTo("name", "Test Event")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                    assertEquals(300L, document.getLong("maxAttendees").longValue());
//                    document.getReference().delete();
//                });
//        // placeholders for what the qr code will be
//        qrImageView.setImageResource(R.drawable.camera);
//
//        // Join/Leave button. Clicking on it will open up a confirmation dialog
//        Button joinWaitlistButton = binding.joinWaitButton;
//        joinWaitlistButton.setOnClickListener(v -> {
//            Event event = eventViewModel.getEvent().getValue();
//            if (event != null && mockUser!= null) {
//                if (event.getWaitlist().contains(mockUser)) {
//                    showLeaveWaitlistDialog(joinWaitlistButton);
//                } else {
//                    showJoinWaitlistDialog(joinWaitlistButton);
//                }
//            }
//        });
//
//        updateButtonText(joinWaitlistButton);


        return root;


    }
    private void fetchEventFromFirebase(String qrCodeData, SharedEventViewModel sharedViewModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events") // Replace with your collection name
                .whereEqualTo("QrCode", qrCodeData)
                .get()
                .addOnSuccessListener(task -> {
                    if (task.getDocuments().isEmpty()) {
                        Toast.makeText(getContext(), "No Event Found: " + qrCodeData, Toast.LENGTH_SHORT).show();
                    } else {
                        DocumentSnapshot document = task.getDocuments().get(0);
                        Event event = new Event(document.getData());
                        sharedViewModel.setEventDetails(event);
                        Toast.makeText(getContext(), "Event Found: " + qrCodeData, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventFragment", "Error fetching event: " + e.getMessage());
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

//    private void fetchEventFromFirebase(String qrCodeData, SharedEventViewModel sharedViewModel) {
//        firebaseHelper.getEventByQRCode(qrCodeData, new Firebase.EventRetrievalListener() {
//            @Override
//            public void onEventRetrievalCompleted(Event event) {
//                if (event != null) {
//                    sharedViewModel.setEventDetails(event);
//                    Toast.makeText(getContext(), "Event Found" + qrCodeData, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "No Event Found" + qrCodeData, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("EventFragment", "Error fetching event: " + e.getMessage());
//            }
//        });
//    }

//    // If user is not currently in waitlist, takes them to confirmation screen to join waitlist
//    private void showJoinWaitlistDialog(Button button) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//
//        View dialogView = inflater.inflate(R.layout.fragment_event_join_wait, null);
//        builder.setView(dialogView);
//
//        TextView messageView = dialogView.findViewById(R.id.textView11);
//        Button joinButton = dialogView.findViewById(R.id.joinWaitButton);
//        Button cancelButton = dialogView.findViewById(R.id.cancelWaitButton);
//
//        AlertDialog dialog = builder.create();
//
//        joinButton.setOnClickListener(view -> {
//            Event event = eventViewModel.getEvent().getValue();
//            if (!event.getWaitlist().contains(mockUser) && event.getWaitlist().size() < event.getMaxAttendees()) {
//                event.getWaitlist().add(mockUser);
//                firebaseHelper.waitlistEvent(event);
//                Toast.makeText(getContext(), "You have joined the waitlist!", Toast.LENGTH_SHORT).show();
//                updateButtonText(button);
//            }
//
//            dialog.dismiss();
//        });
//
//        cancelButton.setOnClickListener(v -> dialog.dismiss());
//
//        dialog.show();
//
//    }
//
//    // If use is currently in waitlist, takes them to confirmation screen to leave waitlist
//    private void showLeaveWaitlistDialog(Button button) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//
//        View dialogView = inflater.inflate(R.layout.fragment_event_leave_wait, null);
//        builder.setView(dialogView);
//
//        TextView messageView = dialogView.findViewById(R.id.textView33);
//        Button leaveButton = dialogView.findViewById(R.id.leaveWaitButton);
//        Button cancelButton = dialogView.findViewById(R.id.cancelWaitButton);
//
//        AlertDialog dialog = builder.create();
//
//        leaveButton.setOnClickListener(view -> {
//            Event event = eventViewModel.getEvent().getValue();
//            if (event != null && event.getWaitlist().contains(mockUser)) {
//                event.getWaitlist().remove(mockUser);
//                firebaseHelper.cancelEvent(event);
//                Toast.makeText(getContext(), "You have left the waitlist!", Toast.LENGTH_SHORT).show();
//                updateButtonText(button);
//            }
//            dialog.dismiss();
//        });
//
//        cancelButton.setOnClickListener(v -> dialog.dismiss());
//
//        dialog.show();
//    }
//
//    // Updates the text on the join/leave button accordingly
//    private void updateButtonText(Button button) {
//        Event event = eventViewModel.getEvent().getValue();
//        if (event != null && mockUser != null) {
//            if (event.getWaitlist().contains(mockUser)) {
//                button.setText("Leave Waitlist");
//            } else {
//                button.setText("Join Waitlist");
//            }
//        }
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}