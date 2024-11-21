package com.example.mini_pekkas.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.UserArrayAdapter;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseProfilesBinding;

import java.util.ArrayList;

public class AdminBrowseProfile extends Fragment {

    private FragmentAdminBrowseProfilesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private ListView listView;
    private ArrayList<User> userList;  // Store the original event objects

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseProfilesBinding.inflate(inflater, container, false);
        firebaseHelper = new Firebase(requireContext());

        // Set the listView adapter
        listView = binding.adminProfileListView;
        userList = new ArrayList<User>();
        UserArrayAdapter userArrayAdapter = new UserArrayAdapter(requireContext(), userList);
        listView.setAdapter(userArrayAdapter);    // TODO need to add option to pass in event list fragment instead
        // TODO add listView.setOnItemClickListener()

        binding.adminSearchProfiles.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Called when the user hits enter (submits the query).
             * @param query the query text that is to be submitted
             *
             * @return true if the query has been handled by the listener, false otherwise.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseHelper.searchForUsers(query, users -> {
                    // Set the value of eventList to events
                    userArrayAdapter.clear();
                    userArrayAdapter.addAll(users);

                    // Notify the dataset change to update
                    userArrayAdapter.notifyDataSetChanged();
                });
                return true;
            }

            /**
             * Called when the content of the query text field has changed.
             * @param newText the new content of the query text field.
             *
             * @return true if the text has been consumed, false otherwise.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}