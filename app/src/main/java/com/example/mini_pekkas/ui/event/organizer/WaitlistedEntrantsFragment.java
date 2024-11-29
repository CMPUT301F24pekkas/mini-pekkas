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
import com.example.mini_pekkas.databinding.FragmentWaitlistBinding;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;

import java.util.ArrayList;

public class WaitlistedEntrantsFragment extends Fragment {
    private FragmentWaitlistBinding binding;
    private Firebase firebaseHelper;
    private UserArrayAdapter waitlistedArrayAdapter;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentWaitlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        Event currentEvent = organizerEventsListViewModel.getSelectedEvent().getValue();

        //make ArrayAdapter

        waitlistedArrayAdapter = new UserArrayAdapter(requireContext(), new ArrayList<>());
        ListView enrolledListView = binding.waitListView;
        enrolledListView.setAdapter(waitlistedArrayAdapter);

        firebaseHelper = new Firebase(requireContext());

        Firebase.UserListRetrievalListener listener = new Firebase.UserListRetrievalListener() {
            @Override
            public void onUserListRetrievalCompleted(ArrayList<User> users) {
                Log.d("user", "User list retrieval completed" + " size:" + users.size());
                //add users to waitlistedArrayAdapter
                for(User user : users) {
                    waitlistedArrayAdapter.addUser(user);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("user", "Error occurred: " + e.getMessage());
            }

        };
        assert currentEvent != null;
        firebaseHelper.getWaitlistedUsers(currentEvent.getId(), listener);
        return root;
    }
}