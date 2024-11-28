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
import com.example.mini_pekkas.OrganizerEventsListViewModel;
import com.example.mini_pekkas.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.UserArrayAdapter;
import com.example.mini_pekkas.databinding.FragmentChosenBinding;
import com.example.mini_pekkas.databinding.FragmentEnrolledBinding;

import java.util.ArrayList;


public class ChosenEntrantsFragment extends Fragment {
    private FragmentChosenBinding binding;
    private Firebase firebaseHelper;
    private UserArrayAdapter ChosenArrayAdapter;
    private OrganizerEventsListViewModel organizerEventsListViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentChosenBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        Event currentEvent = organizerEventsListViewModel.getSelectedEvent().getValue();
        //make ArrayAdapter
        ChosenArrayAdapter = new UserArrayAdapter(requireContext(), new ArrayList<>());
        ListView enrolledListView = binding.chosenListView;
        enrolledListView.setAdapter(ChosenArrayAdapter);

        firebaseHelper = new Firebase(requireContext());

        Firebase.UserListRetrievalListener listener = new Firebase.UserListRetrievalListener() {
            @Override
            public void onUserListRetrievalCompleted(ArrayList<User> users) {
                Log.d("user", "User list retrieval completed" + " size:" + users.size());
                for(User user : users) {
                    ChosenArrayAdapter.addUser(user);
                }
            }
            @Override
            public void onError(Exception e) {
                Log.d("user", "Error occurred: " + e.getMessage());
            }

        };
        assert currentEvent != null;
        firebaseHelper.getPendingUsers(currentEvent.getId(), listener);
        firebaseHelper.getEnrolledUsers(currentEvent.getId(), listener);
        firebaseHelper.getCancelledUsers(currentEvent.getId(), listener);
        return root;
    }
}
