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
        listView.setAdapter(eventListAdapter);    // TODO need to add option to pass in event list fragment instead
        // TODO add listView.setOnItemClickListener()
        //listView.setOnItemClickListener((parent, view, position, id) -> navigateToAdminEvent(view));

        //setupListView();
        //loadEvents();

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
                //filterEvents(newText);

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
        getParentFragmentManager().beginTransaction()
                .replace(R.layout.fragment_admin_browse_events, fragment)   // TODO need to finish this
                .addToBackStack(null)
                .commit();
    }

    // TODO I see what you're doing here, but list views should be done in EventArrayAdapter, and all other corresponding object
//    private void setupListView() {
//        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, eventNames);
//        binding.adminEventListView.setAdapter(adapter);
//
//        // Optional: Set click listener for event selection
//        binding.adminEventListView.setOnItemClickListener((parent, view, position, id) -> {
//            Event selectedEvent = eventList.get(position);
//            Toast.makeText(requireContext(), "Selected Event: " + selectedEvent.getName(), Toast.LENGTH_SHORT).show();
//            // You can add navigation to a detailed view here
//        });
//    }

// TODO Assuming this is meant to load an event object and open the admin_event intent on click? This function should open the new intent, passing the data along

//
//    private void loadEvents() {
//        CollectionReference eventsRef = db.collection("events");  // Replace with your collection name
//        eventsRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                eventList.clear();
//                eventNames.clear();
//                for (QueryDocumentSnapshot doc : task.getResult()) {
//                    // Log the facilityGeoLat and facilityGeoLong for debugging
//                    Log.d("Firestore", "facilityGeoLat: " + doc.get("facilityGeoLat"));
//                    Log.d("Firestore", "facilityGeoLong: " + doc.get("facilityGeoLong"));
//
//                    // Safely handle the conversion of facilityGeoLat and facilityGeoLong from String or other types
//                    double facilityGeoLat = 0.0;
//                    double facilityGeoLong = 0.0;
//
//                    Object geoLatObj = doc.get("facilityGeoLat");
//                    if (geoLatObj instanceof Double) {
//                        facilityGeoLat = (Double) geoLatObj;
//                    } else if (geoLatObj instanceof Long) {
//                        facilityGeoLat = ((Long) geoLatObj).doubleValue();
//                    } else if (geoLatObj instanceof String) {
//                        try {
//                            facilityGeoLat = Double.parseDouble((String) geoLatObj);
//                        } catch (NumberFormatException e) {
//                            Log.e("Firestore", "Error parsing facilityGeoLat: " + e.getMessage());
//                        }
//                    }
//
//                    Object geoLongObj = doc.get("facilityGeoLong");
//                    if (geoLongObj instanceof Double) {
//                        facilityGeoLong = (Double) geoLongObj;
//                    } else if (geoLongObj instanceof Long) {
//                        facilityGeoLong = ((Long) geoLongObj).doubleValue();
//                    } else if (geoLongObj instanceof String) {
//                        try {
//                            facilityGeoLong = Double.parseDouble((String) geoLongObj);
//                        } catch (NumberFormatException e) {
//                            Log.e("Firestore", "Error parsing facilityGeoLong: " + e.getMessage());
//                        }
//                    }
//
//                    // Now create the Event object with the correct facilityGeoLat and facilityGeoLong
//                    Event event = doc.toObject(Event.class);
//                    event.setFacilityGeoLat(facilityGeoLat);
//                    event.setFacilityGeoLong(facilityGeoLong);
//
//                    eventList.add(event);
//                    eventNames.add(event.getName());
//                }
//                adapter.notifyDataSetChanged();
//            } else {
//                Toast.makeText(requireContext(), "Failed to load events.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void filterEvents(String query) {
//        ArrayList<String> filteredNames = new ArrayList<>();
//        if (TextUtils.isEmpty(query)) {
//            filteredNames.addAll(eventNames);
//        } else {
//            for (String name : eventNames) {
//                if (name.toLowerCase().contains(query.toLowerCase())) {
//                    filteredNames.add(name);
//                }
//            }
//        }
//        adapter.clear();
//        adapter.addAll(filteredNames);
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
