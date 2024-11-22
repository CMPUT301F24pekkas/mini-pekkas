package com.example.mini_pekkas.ui.event.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentEventJoinBinding;
import com.example.mini_pekkas.databinding.FragmentEventJoinWaitBinding;
import com.example.mini_pekkas.ui.home.HomeFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventJoinFragment extends Fragment {

    private FragmentEventJoinBinding binding;
    private User mockUser;
    private EventViewModel eventViewModel;
    private Firebase firebaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        firebaseHelper = new Firebase(requireContext());


        binding = FragmentEventJoinBinding.inflate(inflater, container, false);
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

        Button joinWaitlistButton = binding.joinWaitButton;
        joinWaitlistButton.setOnClickListener(v -> {
            FragmentEventJoinWaitBinding joinWaitBinding = FragmentEventJoinWaitBinding.inflate(LayoutInflater.from(getContext()));

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(joinWaitBinding.getRoot());
            AlertDialog dialog = builder.create();
            dialog.show();

            Button joinButton = joinWaitBinding.joinWaitButton;
            Button cancelButton = joinWaitBinding.cancelWaitButton;

            joinButton.setOnClickListener(view -> {
                Event event = eventViewModel.getEvent().getValue();
                if (event != null) {
                    String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    joinWaitList(event, deviceId);
                    Toast.makeText(requireContext(), "Joining waitlist...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());
        });


        return root;


    }

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

    public void joinWaitList(Event event, String deviceId) {
        if (event == null || deviceId == null) {
            Log.e("Firebase", "Event or Device ID is null");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> deviceEntry = new HashMap<>();
        deviceEntry.put("deviceId", deviceId);

        db.collection("events").document(event.getId())
                .update("waitlist", FieldValue.arrayUnion(deviceEntry))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Device ID added to waitlist");
                    Toast.makeText(requireContext(), "Added to waitlist", Toast.LENGTH_SHORT).show();

                    NavController navController = NavHostFragment.findNavController(EventJoinFragment.this);
                    navController.navigate(R.id.action_navigation_event2_to_navigation_event);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to add device to waitlist", e);
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}