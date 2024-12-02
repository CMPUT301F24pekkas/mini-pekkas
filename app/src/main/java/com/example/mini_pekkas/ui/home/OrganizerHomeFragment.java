package com.example.mini_pekkas.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.Event;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentOrganizerHomeBinding;
import com.example.mini_pekkas.ui.event.organizer.EnrollmentStatusHelper;

import java.util.ArrayList;

/**
 * Fragment that displays the home screen for an organizer, showing information about an event.
 */
public class OrganizerHomeFragment extends Fragment {

    private FragmentOrganizerHomeBinding binding;
    private LinearLayout EventsContainer;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    private LiveData<ArrayList<Event>> EventList;
    /**
     * Called to initialize the fragment's user interface.
     *
     * @param inflater           LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI is attached to.
     * @param savedInstanceState Bundle containing the fragment's previously saved state.
     * @return The root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);

        binding = FragmentOrganizerHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EventsContainer = binding.mainContainer;
        Log.d("DEVICEID", organizerEventsListViewModel.getDeviceId());
        //create observer for the event list
        organizerEventsListViewModel.getEventList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                if (events != null) {
                    // Update UI with the new event list
                    updateEventListHomepageUI(events);
                } else {
                    Log.d("OrganizerHomeFragment", "No events available");
                }
            }
        });
        organizerEventsListViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                if (event != null) {
                    // Update UI with the new event list
                    EventList = organizerEventsListViewModel.getEventList();
                    assert EventList.getValue() != null;
                    updateEventListHomepageUI(EventList.getValue());
                } else {
                    Log.d("OrganizerHomeFragment", "No events available");
                }
            }
        });
        //add the current events in db ONCE
        organizerEventsListViewModel.initializeDataIfNeeded();

        return root;
    }


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
            View eventView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_organizer_event, EventsContainer, false);
            CardView NewMockEventCardView = eventView.findViewById(R.id.EventCardView);
            TextView EventTitleView = NewMockEventCardView.findViewById(R.id.EventName);
            TextView EventSubtitleView = NewMockEventCardView.findViewById(R.id.EventSubtitle);
            TextView EventDescriptionView = NewMockEventCardView.findViewById(R.id.EventDescription);
            TextView TimeUntilEventView = NewMockEventCardView.findViewById(R.id.TimeUntilEvent);

            Button EditEvent = NewMockEventCardView.findViewById(R.id.EditEvent);
            EditEvent.setOnClickListener(v -> {
                NavController navController = NavHostFragment.findNavController(OrganizerHomeFragment.this);
                assert event != null;
                organizerEventsListViewModel.setSelectedEvent(event);

                boolean isEnrollmentStarted = EnrollmentStatusHelper.isEnrollmentStarted(requireContext(), event.getId());

                if (isEnrollmentStarted) {
                    navController.navigate(R.id.action_navigation_org_home_to_navigation_org_event_choose_participants);
                } else {
                    navController.navigate(R.id.action_home_to_event_details);
                }
            });



            EventTitleView.setText(event.getName());
            EventSubtitleView.setText("hosted by " + event.getEventHost().getName());
            EventDescriptionView.setText(event.getDescription());
            TimeUntilEventView.setText("Starts on " + event.getStartDate().toString());
            //set tag for identification later
            eventView.setTag(event.getId());

            EventsContainer.addView(eventView);
        }
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
