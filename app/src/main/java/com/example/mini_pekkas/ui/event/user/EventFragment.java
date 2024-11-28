package com.example.mini_pekkas.ui.event.user;

import static org.junit.Assert.assertEquals;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.mini_pekkas.Firebase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEventBinding;
import com.example.mini_pekkas.databinding.FragmentEventLeaveWaitBinding;
import com.example.mini_pekkas.ui.home.HomeEventsListViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Fragment representing an event's details and waitlist functionality.
 * Allows users to join or leave the event's waitlist.
 */
public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private SharedEventViewModel sharedEventViewModel;
    private EventViewModel eventViewModel;
    private HomeEventsListViewModel homeEventsListViewModel;

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
        Firebase firebaseHelper = new Firebase(requireContext());
        homeEventsListViewModel = new ViewModelProvider(requireActivity()).get(HomeEventsListViewModel.class);


        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedEventViewModel = new ViewModelProvider(requireActivity()).get(SharedEventViewModel.class);
        sharedEventViewModel.getQrCodeData().observe(getViewLifecycleOwner(), qrCodeData -> {
            if (qrCodeData != null) {
                fetchEventFromFirebase(qrCodeData, sharedEventViewModel);
            }
        });
        eventViewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
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
                Event event = sharedEventViewModel.getEventDetails().getValue();
                if (event != null) {
                    Toast.makeText(requireContext(), "Leaving waitlist...", Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_navigation_event_to_navigation_home);
                    homeEventsListViewModel.deleteEvent(event.getId());
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
                        eventViewModel.setEvent(event);
                    }
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
                .update("waitlist", FieldValue.arrayRemove(deviceId));
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