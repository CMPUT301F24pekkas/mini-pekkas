package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;
import com.example.mini_pekkas.databinding.FragmentEventOrgBinding;
import com.example.mini_pekkas.databinding.FragmentChoosePartBinding;


/**
 * Fragment representing the organizer's view for a specific event.
 * Displays event details, allows participant selection, and includes QR code functionality.
 */
public class EventOrgFragment extends Fragment {

    private FragmentEventOrgBinding binding;

    /**
     * Initializes the fragment's view, binding UI elements and setting up ViewModel observers
     * to display event details dynamically.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment
     * @param container          The parent view to which the fragment's UI is attached
     * @param savedInstanceState Previously saved state information for the fragment
     * @return The root view of the fragment's layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventOrgViewModel eventViewModel =
                new ViewModelProvider(this).get(EventOrgViewModel.class);

        binding = FragmentEventOrgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Binds the text views to the view model
        final TextView eventNameView = binding.eventNameView;
        final TextView organizerNameView = binding.organizerNameView;
        final TextView locationView = binding.locationView;
        final TextView eventDescriptionView = binding.eventDescriptionView;
        final TextView eventDetailsView = binding.eventDetailsView;

        // Observes LiveData in the view model to update text views
        eventViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
        eventViewModel.getOrganizerName().observe(getViewLifecycleOwner(), organizerNameView::setText);
        eventViewModel.getLocation().observe(getViewLifecycleOwner(), locationView::setText);
        eventViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
        eventViewModel.getEventDetails().observe(getViewLifecycleOwner(), eventDetailsView::setText);

        // Binds the event image to the view model
        final ImageView eventImage = binding.eventImageView;
        ImageView qrImageView = root.findViewById(R.id.qrImage);
        eventViewModel.getEventImage().observe(getViewLifecycleOwner(), eventImage::setImageResource);

        // Placeholder for the QR code image
        qrImageView.setImageResource(R.drawable.camera);

        // Sets up the choose button to open a participant selection dialog
        ImageButton chooseButton = binding.chooseButton;
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates a dialog for choosing participants when the button is clicked
                FragmentChoosePartBinding choosePartBinding = FragmentChoosePartBinding.inflate(LayoutInflater.from(getContext()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(choosePartBinding.getRoot());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }

    /**
     * Cleans up resources when the fragment's view is destroyed.
     * Sets the binding to null to prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}