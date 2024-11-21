package com.example.mini_pekkas.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseFacilitiesBinding;

import java.util.ArrayList;

public class AdminBrowseFacilities extends Fragment {

    private FragmentAdminBrowseFacilitiesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private ListView listView;
    private ArrayList<String> facilityNames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseFacilitiesBinding.inflate(inflater, container, false);
        firebaseHelper = new Firebase(requireContext());

        // Set the listView adapter
        listView = binding.adminFacilityListView;
        facilityNames = new ArrayList<String>();
        ArrayAdapter<String> facilityArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, facilityNames);
        listView.setAdapter(facilityArrayAdapter);    // TODO need to add option to pass in event list fragment instead
        // TODO add listView.setOnItemClickListener()


        binding.adminSearchFacilities.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Called when the user hits enter (submits the query).
             * @param query the query text that is to be submitted
             *
             * @return true if the query has been handled by the listener, false otherwise.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseHelper.searchForFacilities(query, facilities -> {
                    // Set the value of eventList to events
                    facilityArrayAdapter.clear();
                    facilityArrayAdapter.addAll(facilities);
                    facilityArrayAdapter.add("Test");

                    // Notify the dataset change to update
                    facilityArrayAdapter.notifyDataSetChanged();
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
        return binding.getRoot(); // Return the root view from binding
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}