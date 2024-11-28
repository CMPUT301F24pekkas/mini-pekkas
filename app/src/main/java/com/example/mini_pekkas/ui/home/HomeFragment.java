package com.example.mini_pekkas.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.mini_pekkas.databinding.FragmentHomeBinding;

import java.util.ArrayList;

/**
 * Fragment that serves as the home screen for regular users, displaying basic event details.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private LinearLayout EventsContainer;
    private HomeEventsListViewModel homeEventsListViewModel;
    private LiveData<ArrayList<Event>> EventList;

    /**
     * Initializes the fragment's user interface.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI will attach to.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeEventsListViewModel = new ViewModelProvider(requireActivity(), new HomeEventsListViewModelFactory(getActivity()))
                .get(HomeEventsListViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EventsContainer = binding.mainContainer;
        Log.d("DEVICEID", homeEventsListViewModel.getDeviceId());
        homeEventsListViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                if (event != null) {
                    Toast.makeText(requireContext(), "event list changed", Toast.LENGTH_SHORT).show();
                    EventList = homeEventsListViewModel.getEventList();
                    assert EventList.getValue() != null;
                    updateEventListHomepageUI(EventList.getValue());
                } else {
                    Log.d("HomeFragment", "No events available");
                }
            }
        });
        homeEventsListViewModel.getEventList().observe(getViewLifecycleOwner(), events -> {
            if (events != null && !events.isEmpty()) {
                updateEventListHomepageUI(events);
            } else {
                Log.d("HomeFragment", "No events to display");
            }
        });
        //add the current events in db ONCE
        homeEventsListViewModel.initializeDataIfNeeded();
        return root;
    }
    //TODO calculate the time until the start of the event

    /**
     * this function updates the UI with the given Livedata eventList
     *
     * @param events
     */
    @SuppressLint("SetTextI18n")
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
            EditEvent.setText("VIEW");
            EditEvent.setOnClickListener(v->{
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);
                navController.navigate(R.id.action_navigation_home_to_navigation_event);
                assert event != null;
                homeEventsListViewModel.setSelectedEvent(event);
            });


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
