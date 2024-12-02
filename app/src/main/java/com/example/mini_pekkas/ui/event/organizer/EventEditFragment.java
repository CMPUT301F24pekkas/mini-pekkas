package com.example.mini_pekkas.ui.event.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.Event;
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
        binding.editStartTime.setText("10:00");
        binding.editEndTime.setText("14:00");
        binding.editDescription.setText(event.getDescription());
        binding.editDetails.setText(event.getDetails());

        if(event.isGeo()){
            binding.createEventLocationEditText.setText("New York City");
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
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                //update viewmodel with new updated event
                organizerEventsListViewModel.setSelectedEvent(UpdatedEvent);
                NavController navController = NavHostFragment.findNavController(EventEditFragment.this);
                navController.navigate(R.id.navigation_org_event_details);

            }
        });
        Button deleteButton = binding.deleteButton;
        deleteButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when the "save" button is clicked.
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

        return root;
    }
    /**
     * Updates the event details
     * @param event
     */
    private Event UpdateEvent(Event event) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        event.setName(binding.createEventEditText.getText().toString());
        Date startDate = dateFormat.parse(binding.editStartDate.getText().toString());
        Date endDate = dateFormat.parse(binding.editEndDate.getText().toString());

        event.setStartDate(startDate);
        event.setEndDate(endDate);
        //TODO figure out time
//        event.setstartTime("10:00");
//        event.setEndTime("14:00");
        event.setDescription(binding.editDescription.getText().toString());
        event.setDetails(binding.editDetails.getText().toString());

        boolean checked = binding.geoCheckBox.isChecked();

        int maxCapacity = -1;
        if (binding.maxPartCheckBox.isChecked()) {
            maxCapacity = Integer.parseInt(binding.editMaxPart.getText().toString());

        }
        event.setMaxAttendees(maxCapacity);
        event.setGeo(checked);
        //update db
        organizerEventsListViewModel.updateEventInDb(event);
        return event;
    }
//    /**
//     * Uploads a new profile picture to Firebase Storage and updates
//     * the profile with the new image URL.
//     *
//     * @param imageUri The URI of the image to be uploaded.
//     */
//    private void uploadImageToFirebase(Uri imageUri) {
//        StorageReference imageRef = posterImageRef.child(System.currentTimeMillis() + ".jpg");
//
//        imageRef.putFile(imageUri)
//                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String downloadUrl = uri.toString();
//                    profileViewModel.setProfilePictureUrl(downloadUrl);
//                    profileViewModel.updateProfileInFirebase();
//                    Glide.with(this).load(downloadUrl).into(binding.profileImage);
//                }).addOnFailureListener(e ->
//                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show()
//                ));
//    }


    /**
     * Called when the fragment's view is destroyed. Cleans up the binding to prevent memory leaks.
     */
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
