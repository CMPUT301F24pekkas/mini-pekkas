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

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.OrganizerEventsListViewModel;
import com.example.mini_pekkas.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEditEventBinding;



public class EventEditFragment extends Fragment {
    private FragmentEditEventBinding binding;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
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
        //set poster picture
        String PosterUrl = event.getPosterPhotoUrl();
        Glide.with(this).load(PosterUrl).into(binding.addEventPicture);
        //make button navigate to addEvent fragment with values changed
        Button saveButton = binding.saveEventButton;
        saveButton.setOnClickListener(new View.OnClickListener() {
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
