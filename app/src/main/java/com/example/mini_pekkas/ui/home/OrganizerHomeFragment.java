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

/**
 * Fragment that displays the home screen for an organizer, showing information about an event.
 */
public class OrganizerHomeFragment extends Fragment {

    private FragmentOrganizerHomeBinding binding;
    private OrganizerHomeViewModel organizerHomeViewModel;

    /**
     * Called to initialize the fragment's user interface.
     *
     * @param inflater           LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI is attached to.
     * @param savedInstanceState Bundle containing the fragment's previously saved state.
     * @return The root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        organizerHomeViewModel = new ViewModelProvider(this, new OrganizerHomeViewModelFactory(getActivity()))
                .get(OrganizerHomeViewModel.class);

        binding = FragmentOrganizerHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observe the selected event LiveData from the ViewModel
        organizerHomeViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), new Observer<Event>() {
            /**
             * Called when the selected event changes.
             *
             * @param event The updated event.
             */
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

    /**
     * Displays the details of the given event in the fragment's layout.
     *
     * @param event The event to display.
     */
    private void displayEventData(Event event) {
        Log.d("OrganizerHomeFragment", "Displaying event: " + event.getName());

        // Access and update the TextViews in the XML layout
        TextView eventNameView = binding.homeUserEventView;
        TextView eventDescriptionView = binding.homeUserEventDescriptionView;
        TextView daysCountView = binding.dayUserCountView;
        Button editButton = binding.homeUserVIewEvent;

        // Set text content based on the retrieved Event data
        eventNameView.setText(event.getName());
        eventDescriptionView.setText(event.getDescription());

        // Calculate and display the number of days until the event starts
        int daysUntilEvent = calculateDaysUntilEvent(event.getStartDate());
        daysCountView.setText("Starts in " + daysUntilEvent + " Days");

        // Set a click listener for the edit button
        editButton.setOnClickListener(view -> {
            Log.d("OrganizerHomeFragment", "Edit event clicked for: " + event.getName());
            // Implement navigation to event editing fragment or activity if needed
        });
    }

    /**
     * Calculates the number of days until the event starts based on its start date.
     *
     * @param startDate The start date of the event in string format (e.g., "YYYY-MM-DD").
     * @return The number of days until the event starts.
     */
    private int calculateDaysUntilEvent(String startDate) {
        // Implement logic to calculate days until event start based on `startDate`
        // Placeholder value; replace with actual date calculation logic
        return 12;
    }

    /**
     * Cleans up resources and references to avoid memory leaks when the fragment's view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
