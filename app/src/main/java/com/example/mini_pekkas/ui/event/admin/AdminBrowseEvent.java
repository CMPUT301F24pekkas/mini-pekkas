package com.example.mini_pekkas.ui.event.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseEventsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminBrowseEvent extends Fragment {

    private FragmentAdminBrowseEventsBinding binding;
    private FirebaseFirestore db;
    private ArrayAdapter<String> adapter;
    private ArrayList<Event> eventList;  // Store the original event objects
    private ArrayList<String> eventNames;  // Store event names for the ListView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseEventsBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventNames = new ArrayList<>();

        //setupListView();
        //loadEvents();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //filterEvents(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filterEvents(newText);
                return false;
            }
        });

        return binding.getRoot();
    }

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
