package com.example.mini_pekkas.ui.event.organizer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.OrganizerEventsListViewModel;
import com.example.mini_pekkas.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentChoosePartBinding;
import com.example.mini_pekkas.databinding.FragmentChosenBinding;
import com.example.mini_pekkas.databinding.FragmentEventOrg2Binding;
import com.example.mini_pekkas.databinding.FragmentEventOrgBinding;

public class EventChooseUsersFragment extends Fragment {
    private FragmentEventOrg2Binding binding;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentEventOrg2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //get selected event from viewmodel
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
        if (event != null) {
            updateEventDetailsUI(event);
        }
        //observe event data
        organizerEventsListViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), this::updateEventDetailsUI);
        //sets the button listeners
        SetButtonListeners();
        //navigate to re-choose participants

        //populate lists

        return root;

    }

    private void updateEventDetailsUI(Event event) {
        binding.eventDescriptionView.setText(event.getDescription());
        binding.eventDetailsView.setText("Details Placeholder");
        binding.eventNameView.setText(event.getName());
        binding.organizerNameView.setText(event.getEventHost().getName());
        if (event.isGeo()) {
            binding.locationView.setText("Location Placeholder");
        } else {
            binding.locationView.setText("No Location");
        }

        // Set poster picture
        String posterUrlString = event.getPosterPhotoUrl();
        if (posterUrlString != null) {
            // Check if permissions are granted
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
                Glide.with(this).load(posterUrlString).into(binding.eventImageView);
            }
        } else {
            Log.d("Permission", "URL is NULL");
        }

        // Set organizer profile picture
        String organizerProfileUrl = event.getEventHost().getProfilePhotoUrl();
        Glide.with(this).load(organizerProfileUrl).into(binding.profilePictureImage);
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
    private void SetButtonListeners() {
        //edit profile button
        ImageButton editPosterButton = binding.editEventPictureButton;
        editPosterButton.setOnClickListener(v -> openImageChooser());

        ImageButton chooseButton = binding.chooseButton;
        chooseButton.setOnClickListener(v -> {

            FragmentChoosePartBinding choosePartBinding = FragmentChoosePartBinding.inflate(
                    LayoutInflater.from(requireContext()));

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(choosePartBinding.getRoot());
            AlertDialog dialog = builder.create();

            View fragmentView = getView();

            choosePartBinding.cancelWaitButton.setOnClickListener(y -> {
                dialog.dismiss();
            });

            choosePartBinding.joinWaitButton.setOnClickListener(z -> {
                if (fragmentView == null) {
                    return;
                }
                NavController navController = Navigation.findNavController(fragmentView);
                navController.navigate(R.id.action_event_details_to_choose_participants);
                dialog.dismiss();
            });

            dialog.show();

            // Verify event data is valid when dialog opens
            Event currentEvent = organizerEventsListViewModel.getSelectedEvent().getValue();
            Log.d("EventDetailsFragment", "Current event when dialog opens: " +
                    (currentEvent != null ? currentEvent.getName() : "null"));
        });
        Button chosenButton = binding.chosenButton;
        chosenButton.setOnClickListener(v -> {

        });
        Button enrolledButton = binding.enrolledButton;
        Button canceledButton = binding.canceledButton;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
