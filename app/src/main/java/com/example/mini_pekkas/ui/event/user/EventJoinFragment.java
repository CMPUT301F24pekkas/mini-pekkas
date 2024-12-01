package com.example.mini_pekkas.ui.event.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEventJoinBinding;
import com.example.mini_pekkas.databinding.FragmentEventJoinWaitBinding;
import com.example.mini_pekkas.databinding.FragmentEventJoinGeoBinding;
import com.example.mini_pekkas.ui.home.HomeEventsListViewModelFactory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.mini_pekkas.ui.home.HomeEventsListViewModel;

import java.util.Objects;


/**
 * A fragment that handles joining an event's waitlist.
 * The fragment displays event details, and allows the user to join the waitlist by providing a device ID.
 * It fetches event data from Firebase based on the QR code data passed to it and allows the user
 * to confirm joining the waitlist via a dialog.
 */
public class EventJoinFragment extends Fragment {

    private FragmentEventJoinBinding binding;
    private HomeEventsListViewModel homeEventsListViewModel;
    private EventViewModel eventViewModel;
    private SharedEventViewModel sharedEventViewModel;
    private Firebase firebaseHelper;

    /**
     * Initializes the fragment and sets up necessary view models and Firebase helper.
     * Observes QR code data to fetch the event details, and populates the UI with event information.
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
        homeEventsListViewModel = new ViewModelProvider(requireActivity(), new HomeEventsListViewModelFactory(getActivity()))
                .get(HomeEventsListViewModel.class);

        binding = FragmentEventJoinBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedEventViewModel = new ViewModelProvider(requireActivity()).get(SharedEventViewModel.class);
        sharedEventViewModel.getQrCodeData().observe(getViewLifecycleOwner(), qrCodeData -> {
            if (qrCodeData != null) {
                fetchEventFromFirebase(qrCodeData, sharedEventViewModel);
            }
        });
        sharedEventViewModel.getEventDetails().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                firebaseHelper.getStatusInEvent(event, new Firebase.DataRetrievalListener() {
                    @Override
                    public void onRetrievalCompleted(String status) {
                        if (Objects.equals(status, "waitlisted")){
                            NavController navController = NavHostFragment.findNavController(EventJoinFragment.this);
                            navController.navigate(R.id.action_navigation_event2_to_navigation_event);
                        } else if (Objects.equals(status, "pending") || Objects.equals(status, "enrolled")){
                            NavController navController = NavHostFragment.findNavController(EventJoinFragment.this);
                            navController.navigate(R.id.action_navigation_event2_to_navigation_event3);
                        }
                    }
                });
                binding.eventNameView.setText(event.getName());
                binding.organizerNameView.setText(event.getEventHost().getName());
                binding.eventDescriptionView.setText(event.getDescription());
                binding.locationView.setText(event.getFacility());
            }
        });

        Button joinWaitlistButton = binding.joinWaitButton;
        joinWaitlistButton.setOnClickListener(v -> {
            Event event = sharedEventViewModel.getEventDetails().getValue();
            if (event != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("events").document(event.getId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Boolean isGeoEnabled = documentSnapshot.getBoolean("geo");
                                if (isGeoEnabled != null && isGeoEnabled) {
                                    FragmentEventJoinGeoBinding joinGeoBinding = FragmentEventJoinGeoBinding.inflate(LayoutInflater.from(getContext()));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setView(joinGeoBinding.getRoot());
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                    Button joinGeoButton = joinGeoBinding.joinWaitButton;
                                    Button cancelGeoButton = joinGeoBinding.cancelWaitButton;
                                    showJoinDialog(dialog, joinGeoButton, cancelGeoButton);
                                } else {
                                    FragmentEventJoinWaitBinding joinWaitBinding = FragmentEventJoinWaitBinding.inflate(LayoutInflater.from(getContext()));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setView(joinWaitBinding.getRoot());
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                    Button joinWaitButton = joinWaitBinding.joinWaitButton;
                                    Button cancelWaitButton = joinWaitBinding.cancelWaitButton;
                                    showJoinDialog(dialog, joinWaitButton, cancelWaitButton);
                                }
                            } else {
                                Toast.makeText(requireContext(), "Event not found in Firebase", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Error fetching event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
        return root;

    }

    /**
     * Fetches the event details from Firebase using the provided QR code data.
     * Updates the event details in the shared and local view models.
     *
     * @param qrCodeData The QR code data used to fetch the event.
     * @param sharedViewModel The shared view model used for updating the event data.
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
                        Event newEvent = new Event(document.getData());

                        // Prevent redundant updates
                        Event currentEvent = sharedViewModel.getEventDetails().getValue();
                        if (currentEvent != null && currentEvent.getId().equals(newEvent.getId())) {
                            return;
                        }

                        sharedViewModel.setEventDetails(newEvent);
                        eventViewModel.setEvent(newEvent);
                    }
                });
    }


    /**
     * Shows the dialog for joining the waitlist and allows user to either join or cancel
     *
     * @param dialog The AlertDialog with either a simple confirmation or with a geolocation confirmation if needed
     * @param joinButton The button to join the wait list
     * @param cancelButton The button to cancel joining the wait list
     */
    private void showJoinDialog(AlertDialog dialog, Button joinButton, Button cancelButton){
        joinButton.setOnClickListener(view -> {
            Event event = sharedEventViewModel.getEventDetails().getValue();
            if (event != null) {
                firebaseHelper.waitlistEvent(event);
                homeEventsListViewModel.addEvent(event);
                homeEventsListViewModel.setSelectedEvent(event);
                NavController navController = NavHostFragment.findNavController(EventJoinFragment.this);
                navController.navigate(R.id.action_navigation_event2_to_navigation_event);
                Toast.makeText(requireContext(), "Joining waitlist...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());
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