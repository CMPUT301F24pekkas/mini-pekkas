package com.example.mini_pekkas.ui.event.organizer;

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
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEditEventBinding;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment for editing event details. This fragment allows the Organizer to:
 * - View and modify the event's name, start date, end date, description, and location.
 * - Select a new event poster image.
 * - Save the updated event information and navigate back to the event details.
 */
public class EventEditFragment extends Fragment {
    private FragmentEditEventBinding binding;
    private OrganizerEventsListViewModel organizerEventsListViewModel;

    private StorageReference posterImageRef; //ref to store the image

    /**
     * Inflates the fragment layout and sets up UI elements for editing an event.
     * Binds event data from ViewModel and handles interactions for image selection
     * and saving event updates.
     *
     * @param inflater LayoutInflater to inflate the layout
     * @param container ViewGroup that contains the fragment's UI
     * @param savedInstanceState Bundle containing the fragment's state
     * @return The root view of the inflated layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //get selected event from viewmodel
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        //set elements in fragment to selected event
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
        assert event != null;
        binding.createEventEditText.setText(event.getName());
        binding.editStartDate.setText(dateFormat.format(event.getStartDate()));
        binding.editEndDate.setText(dateFormat.format(event.getEndDate()));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String formattedstartTime = formatter.format(event.getStartDate());
        String formattedendTime = formatter.format(event.getEndDate());
        binding.editStartTime.setText(formattedstartTime);
        binding.editEndTime.setText(formattedendTime);
        binding.editDescription.setText(event.getDescription());

        if(event.isGeo()){
            binding.geoCheckBox.setChecked(true);
            binding.createEventLocationEditText.setText(event.getFacility());
        }
        else{
            binding.createEventLocationEditText.setText("N/A");
        }
        //make button navigate to addEvent fragment with values changed
        Button saveButton = binding.saveEventButton;
        saveButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when the "save" button is clicked.
             * Saves the event details and takes you to the updated even details screen
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                //navigate to addEvent fragment with values changed
                Event UpdatedEvent = null;
                try {
                    UpdatedEvent = UpdateEvent(event);
                    if (UpdatedEvent == null) {
                        // Validation failed; return without proceeding
                        return;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                //update viewmodel with new updated event
                organizerEventsListViewModel.setSelectedEvent(UpdatedEvent);
                NavController navController = NavHostFragment.findNavController(EventEditFragment.this);
                boolean isEnrollmentStarted = EnrollmentStatusHelper.isEnrollmentStarted(requireContext(), UpdatedEvent.getId());

                if (isEnrollmentStarted) {
                    navController.navigate(R.id.action_edit_event_to_choose_participants);
                } else {
                    navController.navigate(R.id.action_edit_event_to_event_details);
                }


            }
        });
        Button deleteButton = binding.deleteButton;
        deleteButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when the "delete" button is clicked.
             * Saves the event details and takes you to the updated even details screen
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                //navigate to addEvent fragment with values changed
                organizerEventsListViewModel.deleteEvent(event.getId());
                //update viewmodel with new updated event

                NavController navController = NavHostFragment.findNavController(EventEditFragment.this);
                navController.navigate(R.id.action_global_navigation_org_home);

            }
        });
        Firebase firebaseHelper = new Firebase(requireContext());
        firebaseHelper.fetchUserDocument(new Firebase.InitializationListener() {
            @Override
            public void onInitialized() {
                if (firebaseHelper.getThisUser() != null) {
                    binding.createEventLocationEditText.setText("Location: " + firebaseHelper.getThisUser().getFacility());
                }
            }
        });
        return root;
    }
    /**
     * Updates the event details
     * @param event Event to update
     */
    private Event UpdateEvent(Event event) throws ParseException {
        String eventTitle = binding.createEventEditText.getText().toString().trim();
        String startDateString = binding.editStartDate.getText().toString().trim();
        String endDateString = binding.editEndDate.getText().toString().trim();
        String startTimeString = binding.editStartTime.getText().toString().trim();
        String endTimeString = binding.editEndTime.getText().toString().trim();
        String eventDescription = binding.editDescription.getText().toString().trim();

        // Validate required inputs
        if (eventTitle.isEmpty()) {
            Toast.makeText(requireContext(), "Event title is required.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (eventDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Event description is required.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (startDateString.isEmpty() || endDateString.isEmpty()) {
            Toast.makeText(requireContext(), "Start and end dates are required.", Toast.LENGTH_SHORT).show();
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date startDate, endDate;
        try {
            startDate = dateFormat.parse(startDateString + " " + startTimeString);
            endDate = dateFormat.parse(endDateString + " " + endTimeString);

            if (startDate.after(endDate)) {
                Toast.makeText(requireContext(), "Start date must be before or equal to the end date.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (ParseException e) {
            Toast.makeText(requireContext(), "Invalid date format Please use yyyy-MM-dd (date) and HH:mm (time).(for 24 hour format)", Toast.LENGTH_SHORT).show();
            return null;
        }

        event.setName(eventTitle);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setDescription(eventDescription);
        boolean checked = binding.geoCheckBox.isChecked();
        event.setGeo(checked);
        //update db
        organizerEventsListViewModel.updateEventInDb(event);
        return event;
    }

    /**
     * Called when the fragment's view is destroyed. Cleans up the binding to prevent memory leaks.
     */
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
