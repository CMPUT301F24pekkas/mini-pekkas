package com.example.mini_pekkas.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Facility;
import com.example.mini_pekkas.FacilityArrayAdapter;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseFacilitiesBinding;

import java.util.ArrayList;

/**
 * Fragment for browsing and managing facilities in the Admin interface.
 * This fragment includes functionality for:
 * - Displaying a list of facilities
 * - Searching facilities using a search bar
 */
public class AdminBrowseFacilities extends Fragment {

    private FragmentAdminBrowseFacilitiesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private ListView listView;
    private ArrayList<Facility> facilities;

    /**
     * Called to initialize the fragment's view hierarchy.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent view group that this fragment's view will be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     * @return The root view of the fragment.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseFacilitiesBinding.inflate(inflater, container, false);
        firebaseHelper = new Firebase(requireContext());

        // Set the listView adapter
        listView = binding.adminFacilityListView;
        facilities = new ArrayList<Facility>();
        FacilityArrayAdapter facilityArrayAdapter = new FacilityArrayAdapter(requireContext(), facilities);
        listView.setAdapter(facilityArrayAdapter);

        listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {

            Facility selectedFacility = facilities.get(position);


            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Facility")
                    .setMessage("Are you sure you want to delete this facility?")
                    .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                        // Call deleteUser on confirmation
                        firebaseHelper.deleteByFacility(selectedFacility, new Firebase.InitializationListener() {
                            @Override
                            public void onInitialized() {
                                // Remove the user from the list and notify the adapter
                                facilities.remove(selectedFacility);
                                facilityArrayAdapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Exception e) {
                                // Handle errors
                                Toast.makeText(requireContext(), "Failed to delete facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });


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

    /**
     * Cleans up resources and prevents memory leaks by nullifying the binding.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}