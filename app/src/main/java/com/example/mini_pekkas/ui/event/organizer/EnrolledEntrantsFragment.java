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

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.UserInEventArrayAdapter;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.UserArrayAdapter;
import com.example.mini_pekkas.databinding.FragmentEnrolledBinding;

import java.util.ArrayList;

/**
 * Fragment for displaying enrolled entrants in an event.
 */
public class EnrolledEntrantsFragment extends Fragment {
    private FragmentEnrolledBinding binding;
    private Firebase firebaseHelper;
    private UserInEventArrayAdapter enrolledArrayAdapter;
    private OrganizerEventsListViewModel organizerEventsListViewModel;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentEnrolledBinding.inflate(inflater, container, false);
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        Event currentEvent = organizerEventsListViewModel.getSelectedEvent().getValue();
        View root = binding.getRoot();

        //make ArrayAdapter

        enrolledArrayAdapter = new UserInEventArrayAdapter(requireActivity(),requireContext(), new ArrayList<>());
        ListView enrolledListView = binding.enrolledListView;
        enrolledListView.setAdapter(enrolledArrayAdapter);

        firebaseHelper = new Firebase(requireContext());


        assert currentEvent != null;
        loadEnrolledUsers(currentEvent.getId());
        return root;
    }

    /**
     * Loads enrolled users for an event.
     * @param eventId ID of Event
     */
    private void loadEnrolledUsers(String eventId) {
        Firebase.UserListRetrievalListener enrolledUsersListener = new Firebase.UserListRetrievalListener() {
            @Override
            public void onUserListRetrievalCompleted(ArrayList<User> users) {
                Log.d("user", "Chosen users retrieved: " + users.size());
                enrolledArrayAdapter.addUsers(users);
            }

            @Override
            public void onError(Exception e) {
                Log.d("user", "Error fetching chosen users: " + e.getMessage());
            }
        };

        firebaseHelper.getEnrolledUsers(eventId, enrolledUsersListener);
    }
}
