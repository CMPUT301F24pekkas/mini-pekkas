package com.example.mini_pekkas.ui.admin;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.UserArrayAdapter;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseProfilesBinding;

import java.util.ArrayList;

/**
 * Fragment for browsing and managing user profiles in the Admin interface.
 * Allows admins to search for user profiles in the system
 * and display user profiles in a ListVIew
 */
public class AdminBrowseProfile extends Fragment {

    private FragmentAdminBrowseProfilesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private ListView listView;
    private ArrayList<User> userList;  // Store the original event objects

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
        binding = FragmentAdminBrowseProfilesBinding.inflate(inflater, container, false);
        firebaseHelper = new Firebase(requireContext());

        // Set the listView adapter
        listView = binding.adminProfileListView;
        userList = new ArrayList<User>();
        UserArrayAdapter userArrayAdapter = new UserArrayAdapter(requireContext(), userList);
        listView.setAdapter(userArrayAdapter);    // TODO need to add option to pass in event list fragment instead


        listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {

            User selectedUser = userList.get(position);
            Log.d(TAG, "Selected user: " + selectedUser.getName());
            Log.d(TAG, "Selected user ID: " + selectedUser.getUserID());

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                        // Call deleteUser on confirmation
                        firebaseHelper.deleteUser(selectedUser, new Firebase.InitializationListener() {
                            @Override
                            public void onInitialized() {
                                // Remove the user from the list and notify the adapter
                                userList.remove(selectedUser);
                                userArrayAdapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Exception e) {
                                // Handle errors, Ignore no profile picture error
                                if (e.getMessage() != "No profile picture to delete") {
                                    Toast.makeText(requireContext(), "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });


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

    /**
     * Cleans up resources and prevents memory leaks by nullifying the binding.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}