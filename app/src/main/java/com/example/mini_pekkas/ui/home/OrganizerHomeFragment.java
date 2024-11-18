package com.example.mini_pekkas.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.EventArrayAdapter;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentOrganizerHomeBinding;
import com.example.mini_pekkas.OrganizerEventsListViewModel;
import com.example.mini_pekkas.OrganizerEventsListViewModelFactory;

import java.util.ArrayList;

/**
 * Fragment that displays the home screen for an organizer, showing information about an event.
 */
public class OrganizerHomeFragment extends Fragment {

    private FragmentOrganizerHomeBinding binding;
    private LinearLayout EventsContainer;
    private OrganizerHomeViewModel organizerHomeViewModel;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    /**
     * Called to initialize the fragment's user interface.
     *
     * @param inflater           LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI is attached to.
     * @param savedInstanceState Bundle containing the fragment's previously saved state.
     * @return The root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        organizerHomeViewModel = new ViewModelProvider(this, new OrganizerHomeViewModelFactory(getActivity()))
//                .get(OrganizerHomeViewModel.class);
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        binding = FragmentOrganizerHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EventsContainer = binding.mainContainer;

        //create observer for the event list
        organizerEventsListViewModel.getEventList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {


                if (events != null) {

                    // Update UI with the new event list
                    updateEventListHomepageUI(events);
                } else {
                    Log.d("OrganizerHomeFragment", "No events available");
                    // Handle no events case (e.g., show a message or placeholder)
                }
            }
        });
        //add the current events that are stored in the db
        //organizerEventsListViewModel.getEventsFromDb();
        //Observe the selected event LiveData from the ViewModel
//        organizerHomeViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), new Observer<Event>() {
//            /**
//             * Called when the selected event changes.
//             *
//             * @param event The updated event.
//             */
//            @Override
//            public void onChanged(Event event) {
//                if (event != null) {
//                    Log.d("OrganizerHomeFragment", "Event updated");
//                    //displayEventData(event); // Display the event
//                } else {
//                    Log.d("OrganizerHomeFragment", "No event available");
//                    // Handle no event case (e.g., show a message or placeholder)
//                }
//            }
//        });

        return root;
    }
    //TODO calculate the time until the start of the event

    /**
     * this function updates the UI with the given Livedata eventList
     *
     * @param events
     */
    private void updateEventListHomepageUI(ArrayList<Event> events){
        //remove all events
        EventsContainer.removeAllViews();

        // Add views for each event
        for (Event event : events) {
            View eventView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_organizer_event, null, false);
            CardView NewMockEventCardView = eventView.findViewById(R.id.EventCardView);
            TextView EventTitleView = NewMockEventCardView.findViewById(R.id.EventName);
            TextView EventSubtitleView = NewMockEventCardView.findViewById(R.id.EventSubtitle);
            TextView EventDescriptionView = NewMockEventCardView.findViewById(R.id.EventDescription);
            TextView TimeUntilEventView = NewMockEventCardView.findViewById(R.id.TimeUntilEvent);

            EventTitleView.setText(event.getName());
            EventSubtitleView.setText("hosted by " + event.getEventHost().getName());
            EventDescriptionView.setText(event.getDescription());
            TimeUntilEventView.setText("10 days until event");
            //set tag for identification later
            eventView.setTag(event.getId());

            EventsContainer.addView(eventView);
        }
    }

    /**
     * Displays the details of the given event in the fragment's layout.
     *
     * @param event The event to display.
     */
//    private void displayEventData(Event event) {
//        Log.d("OrganizerHomeFragment", "Displaying event: " + event.getName());
//
//        // Access and update the TextViews in the XML layout
//        CardView EventView = binding.ExampleEvent1;
//        String description = binding.EventDescription.getText().toString();
//        TextView eventDescriptionView =  EventTitle;
//        TextView daysCountView = binding.dayUserCountView;
//        Button editButton = binding.homeUserVIewEvent;
//
//        // Set text content based on the retrieved Event data
//        eventNameView.setText(event.getName());
//        eventDescriptionView.setText(event.getDescription());
//
//        // Calculate and display the number of days until the event starts
//        int daysUntilEvent = calculateDaysUntilEvent(event.getStartDate());
//        daysCountView.setText("Starts in " + daysUntilEvent + " Days");
//
//        // Set a click listener for the edit button
//        editButton.setOnClickListener(view -> {
//            Log.d("OrganizerHomeFragment", "Edit event clicked for: " + event.getName());
//            // Implement navigation to event editing fragment or activity if needed
//        });
//    }

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
