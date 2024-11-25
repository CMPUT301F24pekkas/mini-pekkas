package com.example.mini_pekkas.ui.event.organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.OrganizerEventsListViewModel;
import com.example.mini_pekkas.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.databinding.FragmentEditEventBinding;
import com.example.mini_pekkas.ui.event.user.EventFragment;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

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


        //set elements in fragment to selected event
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
        assert event != null;
        binding.createEventEditText.setText(event.getName());
        binding.editStartDate.setText(event.getStartDate());
        binding.editEndDate.setText(event.getEndDate());
        binding.editStartTime.setText("10:00");
        binding.editEndTime.setText("14:00");
        binding.editDescription.setText(event.getDescription());
        binding.editDetails.setText("Details Placeholder");

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
                Event UpdatedEvent = UpdateEvent(event);
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
    private Event UpdateEvent(Event event) {
        event.setName(binding.createEventEditText.getText().toString());
        event.setStartDate(binding.editStartDate.getText().toString());
        event.setEndDate(binding.editEndDate.getText().toString());
        //TODO figure out time
//        event.setstartTime("10:00");
//        event.setEndTime("14:00");
        event.setDescription(binding.editDescription.getText().toString());

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