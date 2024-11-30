package com.example.mini_pekkas.ui.event.user;

import static org.junit.Assert.assertEquals;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.example.mini_pekkas.databinding.FragmentEventBinding;
import com.example.mini_pekkas.databinding.FragmentEventLeaveWaitBinding;
import com.example.mini_pekkas.ui.home.HomeEventsListViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * Fragment representing an event's details and waitlist functionality.
 * Allows users to join or leave the event's waitlist.
 */
public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private SharedEventViewModel sharedEventViewModel;
    private EventViewModel eventViewModel;
    private HomeEventsListViewModel homeEventsListViewModel;
    private Event event;
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
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        firebaseHelper = new Firebase(requireContext());
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        sharedEventViewModel = new ViewModelProvider(requireActivity()).get(SharedEventViewModel.class);
        homeEventsListViewModel = new ViewModelProvider(requireActivity()).get(HomeEventsListViewModel.class);
        sharedEventViewModel.getNavigationSource().observe(getViewLifecycleOwner(), source -> {
            if ("HOME".equals(source)) {
                Event homeEvent = homeEventsListViewModel.getSelectedEvent().getValue();
                if (homeEvent != null) {
                    event = homeEvent;

                    fetchEventStatus();
                } else {
                    Toast.makeText(requireContext(), "Home event not found", Toast.LENGTH_SHORT).show();
                }
            } else if ("QR_CODE".equals(source)) {
                Event qrCodeEvent = sharedEventViewModel.getEventDetails().getValue();
                if (qrCodeEvent != null) {
                    event = qrCodeEvent;
                    fetchEventStatus();
                } else {
                    Toast.makeText(requireContext(), "QR code event not found", Toast.LENGTH_SHORT).show();
                }
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

    private void fetchEventStatus() {
        firebaseHelper.getStatusInEvent(event, new Firebase.DataRetrievalListener() {
            @Override
            public void onRetrievalCompleted(String status) {
                Log.d("FirebaseHelper", "Status retrieved: " + status);
                if (Objects.equals(status, "waitlisted")){
                    displayEventDetails(event);
                } else if (Objects.equals(status, "pending")){
                    NavController navController = NavHostFragment.findNavController(EventFragment.this);
                    navController.navigate(R.id.action_navigation_home_to_navigation_event3);
                }
            }
        });
    }


    private void displayEventDetails(Event event) {
        if (event == null) {
            Toast.makeText(requireContext(), "Event details are not available", Toast.LENGTH_SHORT).show();
            return;
        }
        String eventName = event.getName() != null ? event.getName() : "Unknown Event";
        String organizerName = event.getEventHost() != null && event.getEventHost().getName() != null
                ? event.getEventHost().getName()
                : "Unknown Organizer";

        binding.eventNameView.setText(eventName);
        binding.organizerNameView.setText(organizerName);
        binding.eventDescriptionView.setText(event.getDescription() != null ? event.getDescription() : "No Description");
        binding.locationView.setText(event.getFacility() != null ? event.getFacility() : "No Facility");
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