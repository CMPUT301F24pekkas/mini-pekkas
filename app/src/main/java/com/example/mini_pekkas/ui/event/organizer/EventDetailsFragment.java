package com.example.mini_pekkas.ui.event.organizer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEventOrgBinding;

/**
 * Fragment for displaying the event details and enabling the editing of an event.
 * It shows event description, name, organizer, location, and the event poster.
 * The user can also navigate to the Edit Event fragment to modify the event.
 */
public class EventDetailsFragment extends Fragment {

    private FragmentEventOrgBinding binding;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;


    /**
     * Inflates the layout, binds the views, and sets up the event details in the fragment.
     * Also sets up the navigation to edit event.
     *
     * @param inflater           the LayoutInflater object to inflate the fragment layout
     * @param container          the ViewGroup container to attach the fragment to
     * @param savedInstanceState the previous state of the fragment, if available
     * @return the root view of the fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventOrgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //get selected event from viewmodel
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        //set elements in fragment to selected event
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
        assert event != null;
        binding.eventDescriptionView.setText(event.getDescription());
        binding.eventDetailsView.setText("Details Placeholder");
        binding.eventNameView.setText(event.getName());
        binding.organizerNameView.setText(event.getEventHost().getName());
        if(event.isGeo()){
            binding.locationView.setText("location placeholder");
        }
        else{
            binding.locationView.setText("no location");
        }
        //set poster picture
        String PosterUrlString = event.getPosterPhotoUrl();

        if(PosterUrlString != null){
            //check if permissions are granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Permission not granted");
                // Request permission
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.INTERNET},
                        100);
            } else {
                // Permission already granted, proceed with loading image
                Log.d("Permission", "Permission granted");
                Glide.with(this).load(PosterUrlString).into(binding.eventImageView);
            }
        }
        else{
            Log.d("Permission", "URL is NULL");
        }



        //set organizer profile picture
        String organizerProfileUrl = event.getEventHost().getProfilePhotoUrl();
        Glide.with(this).load(organizerProfileUrl).into(binding.profilePictureImage);
        //make editEvent button navigate to addEvent fragment with values changed
        Button editButton = binding.editEventButton;
        editButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when the "edit event" button is clicked.
             * Navigates to the "edit event" fragment to modify event details.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                //navigate to addEvent fragment with values changed
                NavController navController = NavHostFragment.findNavController(EventDetailsFragment.this);
                navController.navigate(R.id.action_event_details_to_edit_event);

            }
        });
        ImageButton editPosterButton = binding.editEventPictureButton;
        editPosterButton.setOnClickListener(v -> openImageChooser());
        return root;
    }
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.eventImageView.setImageURI(imageUri);  // Show selected image in ImageView
        }
    }

}