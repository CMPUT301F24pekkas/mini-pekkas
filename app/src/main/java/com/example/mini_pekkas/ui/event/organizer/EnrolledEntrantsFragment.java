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
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.UserArrayAdapter;
import com.example.mini_pekkas.databinding.FragmentEnrolledBinding;

import java.util.ArrayList;


public class EnrolledEntrantsFragment extends Fragment {
    private FragmentEnrolledBinding binding;
    private Firebase firebaseHelper;
    private UserArrayAdapter enrolledArrayAdapter;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentEnrolledBinding.inflate(inflater, container, false);
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        Event currentEvent = organizerEventsListViewModel.getSelectedEvent().getValue();
        View root = binding.getRoot();

        //make ArrayAdapter

        enrolledArrayAdapter = new UserArrayAdapter(requireContext(), new ArrayList<>());
        ListView enrolledListView = binding.enrolledListView;
        enrolledListView.setAdapter(enrolledArrayAdapter);

        firebaseHelper = new Firebase(requireContext());

        Firebase.UserListRetrievalListener listener = new Firebase.UserListRetrievalListener() {
            @Override
            public void onUserListRetrievalCompleted(ArrayList<User> users) {
                Log.d("user", "User list retrieval completed" + " size:" + users.size());
                enrolledArrayAdapter.addUsers(users);
            }

            @Override
            public void onError(Exception e) {
                Log.d("user", "Error occurred: " + e.getMessage());
            }

        };
        assert currentEvent != null;
        loadEnrolledUsers(currentEvent.getId());
        return root;
    }

    private void loadEnrolledUsers(String eventId) {
        Firebase.UserListRetrievalListener enrolledUsersListener = new Firebase.UserListRetrievalListener() {
            @Override
            public void onUserListRetrievalCompleted(ArrayList<User> users) {
                Log.d("user", "Chosen users retrieved: " + users.size());
                enrolledArrayAdapter.clearUsers();
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
