package com.example.mini_pekkas.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.databinding.FragmentOrganizerHomeBinding;

public class OrganizerHomeFragment extends Fragment {

    private FragmentOrganizerHomeBinding binding;
    private OrganizerHomeViewModel organizerHomeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        organizerHomeViewModel = new ViewModelProvider(this, new OrganizerHomeViewModelFactory(getActivity()))
                .get(OrganizerHomeViewModel.class);

        binding = FragmentOrganizerHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observe the selected event LiveData from the ViewModel
        organizerHomeViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), new Observer<Event>() {  // Updated observer to handle a single event
            @Override
            public void onChanged(Event event) {
                if (event != null) {
                    Log.d("OrganizerHomeFragment", "Event updated");
                    displayEventData(event); // Display the event
                } else {
                    Log.d("OrganizerHomeFragment", "No event available");
                    // Handle no event case (e.g., show a message or placeholder)
                }
            }
        });

        return root;
    }

    private void displayEventData(Event event) {
        Log.d("OrganizerHomeFragment", "Displaying event: " + event.getName());

        // Access and update the TextViews in your XML layout
        TextView eventNameView = binding.homeUserEventView;
        TextView eventDescriptionView = binding.homeUserEventDescriptionView;
        TextView daysCountView = binding.dayUserCountView;
        Button editButton = binding.homeUserVIewEvent;

        // Set text content based on the retrieved Event data
        eventNameView.setText(event.getName());
        eventDescriptionView.setText(event.getDescription());
        int daysUntilEvent = calculateDaysUntilEvent(event.getStartDate());
        daysCountView.setText("Starts in " + daysUntilEvent + " Days");

        // Handle edit button click
        editButton.setOnClickListener(view -> {
            // Example of navigating to an event edit screen (you can implement actual navigation logic)
            Log.d("OrganizerHomeFragment", "Edit event clicked for: " + event.getName());
            // You can add functionality to navigate to an event editing fragment or activity
        });
    }

    private int calculateDaysUntilEvent(String startDate) {
        // Implement logic to calculate days until event start based on `startDate`
        return 12; // Placeholder value; replace with actual calculation
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
