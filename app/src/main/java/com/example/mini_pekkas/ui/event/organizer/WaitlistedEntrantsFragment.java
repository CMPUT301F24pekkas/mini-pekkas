package com.example.mini_pekkas.ui.event.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.UserArrayAdapter;
import com.example.mini_pekkas.UserInEventArrayAdapter;
import com.example.mini_pekkas.databinding.FragmentWaitlistBinding;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;

import java.util.ArrayList;

/**
 * Fragment representing the waitlisted entrants in an event.
 */
public class WaitlistedEntrantsFragment extends Fragment {
    private FragmentWaitlistBinding binding;
    private Firebase firebaseHelper;
    private UserInEventArrayAdapter waitlistedArrayAdapter;
    private OrganizerEventsListViewModel organizerEventsListViewModel;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWaitlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the ViewModel
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);

        // Retrieve the current event
        Event currentEvent = organizerEventsListViewModel.getSelectedEvent().getValue();

        // Set up the ArrayAdapter for the ListView
        waitlistedArrayAdapter = new UserInEventArrayAdapter(requireActivity(),requireContext(), new ArrayList<>());
        ListView enrolledListView = binding.waitListView;
        enrolledListView.setAdapter(waitlistedArrayAdapter);

        // Initialize Firebase helper
        firebaseHelper = new Firebase(requireContext());

        // Set up listener for retrieving the waitlisted users
        Firebase.UserListRetrievalListener listener = new Firebase.UserListRetrievalListener() {
            @Override
            public void onUserListRetrievalCompleted(ArrayList<User> users) {
                Log.d("user", "User list retrieval completed" + " size:" + users.size());
                // Add users to the ArrayAdapter
                waitlistedArrayAdapter.addUsers(users);
            }

            @Override
            public void onError(Exception e) {
                Log.d("user", "Error occurred: " + e.getMessage());
            }
        };

        // Make sure currentEvent is not null before calling Firebase
        assert currentEvent != null;
        firebaseHelper.getWaitlistedUsers(currentEvent.getId(), listener);

        // Button click listener to navigate to the MapFragment
        binding.button.setOnClickListener(v -> {
            // Get the NavController from the view (this handles navigation properly)
            NavController navController = Navigation.findNavController(requireView());

            // Use the newInstance method to create a MapFragment with the eventID as an argument
            String eventId = currentEvent.getId();  // Ensure you have the eventId
            MapFragment mapFragment = MapFragment.newInstance(eventId);

            // Now, navigate to the MapFragment using the NavController
            navController.navigate(R.id.action_waitlistedEntrants_to_mapFragment);

            Bundle bundle = new Bundle();
            bundle.putString("eventID", eventId);
            Navigation.findNavController(v).navigate(R.id.action_waitlistedEntrants_to_mapFragment, bundle);
        });

        return root;
    }
}
