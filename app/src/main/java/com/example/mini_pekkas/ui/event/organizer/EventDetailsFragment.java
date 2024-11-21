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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;


import com.bumptech.glide.Glide;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.OrganizerEventsListViewModel;
import com.example.mini_pekkas.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEventOrgBinding;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.ui.home.OrganizerHomeFragment;

import java.net.URL;

public class EventDetailsFragment extends Fragment {

    private FragmentEventOrgBinding binding;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
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
        String PosterUrl = event.getPosterPhotoUrl();
        Glide.with(this).load(PosterUrl).into(binding.eventImageView);
        //set organizer profile picture
        String organizerProfileUrl = event.getEventHost().getProfilePhotoUrl();
        Glide.with(this).load(organizerProfileUrl).into(binding.profilePictureImage);
        //make editEvent button navigate to addEvent fragment with values changed
        Button editButton = binding.editEventButton;
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to addEvent fragment with values changed
                NavController navController = NavHostFragment.findNavController(EventDetailsFragment.this);
                navController.navigate(R.id.action_event_details_to_edit_event);

            }
        });
        return root;
    }

}