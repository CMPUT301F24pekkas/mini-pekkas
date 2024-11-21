package com.example.mini_pekkas.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.AdminEventArrayAdapter;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseEventsBinding;
import com.example.mini_pekkas.ui.event.admin.AdminEventFragment;

import java.util.ArrayList;

public class AdminBrowseEvent extends Fragment {

    private FragmentAdminBrowseEventsBinding binding;
    private Firebase firebaseHelper;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<Event> eventList;  // Store the original event objects
    private ArrayList<String> eventNames;  // Store event names for the ListView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseEventsBinding.inflate(inflater, container, false);
        firebaseHelper = new Firebase(requireContext());

        eventNames = new ArrayList<>();     // Not sure this is needed

        // Set the listView adapter
        listView = binding.adminEventListView;
        eventList = new ArrayList<Event>();
        AdminEventArrayAdapter eventListAdapter = new AdminEventArrayAdapter(requireContext(), eventList);
        listView.setAdapter(eventListAdapter);

        // Add delete functionality via long click
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventList.get(position);

            // Confirmation dialog before deletion
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        firebaseHelper.deleteEvent(selectedEvent, new Firebase.InitializationListener() {
                            @Override
                            public void onInitialized() {
                                Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                eventList.remove(position);
                                eventListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(requireContext(), "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        binding.adminSearchEvents.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Called when the user hits enter (submits the query).
             * @param query the query text that is to be submitted
             *
             * @return true if the query has been handled by the listener, false otherwise.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseHelper.searchForEvent(query, events -> {
                    // Set the value of eventList to events
                    eventListAdapter.clear();
                    eventListAdapter.addAll(events);

                    // Notify the dataset change to update
                    eventListAdapter.notifyDataSetChanged();
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
     * On click listener for the adminEventListView.
     * Navigate to an event fragment for the given event
     * @param v the view that was clicked
     */
    private void navigateToAdminEvent(View v) {
        // Get the event that was clicked
        int position = listView.getPositionForView(v);
        Event selectedEvent = eventList.get(position);

        // Create Bundle for event data
        Bundle bundle = new Bundle();
        bundle.putString("eventId", selectedEvent.getId());
        bundle.putString("eventName", selectedEvent.getName());
        // ... add other event data to the bundle ...

        // Create Fragment and set arguments
        AdminEventFragment fragment = new AdminEventFragment();
        fragment.setArguments(bundle);

        // Navigate to Fragment
//        getParentFragmentManager().beginTransaction()
//                .replace(R.layout.fragment_admin_browse_events, fragment)   // TODO need to finish this
//                .addToBackStack(null)
//                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
