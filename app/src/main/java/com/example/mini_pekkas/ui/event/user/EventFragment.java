package com.example.mini_pekkas.ui.event.user;

import static org.junit.Assert.assertEquals;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentEventBinding;
import com.example.mini_pekkas.databinding.FragmentEventLeaveWaitBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Fragment representing an event's details and waitlist functionality.
 * Allows users to join or leave the event's waitlist.
 */
public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private User mockUser;
    private EventViewModel eventViewModel;
    private Firebase firebaseHelper;

    /**
     * Inflates the fragment's view, sets up the necessary ViewModels and UI elements,
     * and binds actions to the UI components, such as joining or leaving the waitlist.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment
     * @param container          The parent view to which the fragment's UI is attached
     * @param savedInstanceState Previously saved state information for the fragment
     * @return The root view of the fragment's layout
     */
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
                binding.locationView.setText(event.getFacility());
            }
        });

        Button joinWaitlistButton = binding.leaveWaitButton;
        joinWaitlistButton.setOnClickListener(v -> {
            FragmentEventLeaveWaitBinding joinWaitBinding = FragmentEventLeaveWaitBinding.inflate(LayoutInflater.from(getContext()));

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(joinWaitBinding.getRoot());
            AlertDialog dialog = builder.create();
            dialog.show();

            Button joinButton = joinWaitBinding.leaveWaitConfirmButton;
            Button cancelButton = joinWaitBinding.cancelWaitButton;

            joinButton.setOnClickListener(view -> {
                Event event = eventViewModel.getEvent().getValue();
                if (event != null) {
                    String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    leaveWaitList(event, deviceId);
                    Toast.makeText(requireContext(), "Leaving waitlist...", Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_navigation_event_to_navigation_home);
                } else {
                    Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());
        });

        return root;


    }

    /**
     * Fetches event data from Firebase using the provided QR code data and updates the event details
     * in the shared view model and the local event view model.
     *
     * @param qrCodeData The data retrieved from scanning the event's QR code
     * @param sharedViewModel The shared view model used to update the event details
     */
    private void fetchEventFromFirebase(String qrCodeData, SharedEventViewModel sharedViewModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("QrCode", qrCodeData)
                .get()
                .addOnSuccessListener(task -> {
                    if (task.getDocuments().isEmpty()) {
                        Toast.makeText(getContext(), "No Event Found", Toast.LENGTH_SHORT).show();
                    } else {
                        DocumentSnapshot document = task.getDocuments().get(0);
                        Event event = new Event(document.getData());
                        sharedViewModel.setEventDetails(event);
                        eventViewModel.setEvent(event); // Update the ViewModel for local use
                        Toast.makeText(getContext(), "Event Found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventFragment", "Error fetching event: " + e.getMessage());
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Removes the user from the event's waitlist based on the event ID and the device ID.
     *
     * @param event The event object from which the user should be removed
     * @param deviceId The device ID of the user to be removed from the waitlist
     */
    public void leaveWaitList(Event event, String deviceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(event.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the current waitlist
                        List<Map<String, Object>> waitlist = (List<Map<String, Object>>) documentSnapshot.get("waitlist");
                        if (waitlist != null) {
                            List<Map<String, Object>> updatedWaitlist = new ArrayList<>();
                            for (Map<String, Object> entry : waitlist) {
                                if (!deviceId.equals(entry.get("deviceId"))) {
                                    updatedWaitlist.add(entry);
                                }
                            }

                            db.collection("events").document(event.getId())
                                    .update("waitlist", updatedWaitlist)
                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Device removed from waitlist"))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to remove device from waitlist", e));
                        } else {
                            Log.w("Firebase", "Waitlist is empty or not found");
                        }
                    } else {
                        Log.e("Firebase", "Event document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to fetch event document", e));
    }


    /**
     * Displays a dialog for the user to confirm their decision to leave the waitlist.
     *
     * @param button The button that triggers the dialog when clicked
     */
    private void showLeaveWaitlistDialog(Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_event_leave_wait, null);
        builder.setView(dialogView);

        TextView messageView = dialogView.findViewById(R.id.textView33);
        Button leaveButton = dialogView.findViewById(R.id.leaveWaitButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelWaitButton);

        AlertDialog dialog = builder.create();

        leaveButton.setOnClickListener(view -> {
            Event event = eventViewModel.getEvent().getValue();
            if (event != null && event.getWaitlist().contains(mockUser)) {
                event.getWaitlist().remove(mockUser);
                firebaseHelper.cancelEvent(event);
                Toast.makeText(getContext(), "You have left the waitlist!", Toast.LENGTH_SHORT).show();
                updateButtonText(button);
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Updates the text on the join/leave button accordingly
    /**
     * Updates the text on the join/leave button based on the user's waitlist status.
     *
     * @param button The button whose text needs to be updated
     */
    private void updateButtonText(Button button) {
        Event event = eventViewModel.getEvent().getValue();
        if (event != null && mockUser != null) {
            if (event.getWaitlist().contains(mockUser)) {
                button.setText("Leave Waitlist");
            } else {
                button.setText("Join Waitlist");
            }
        }
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