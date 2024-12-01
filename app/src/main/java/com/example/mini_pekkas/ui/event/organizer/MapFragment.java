package com.example.mini_pekkas.ui.event.organizer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private Firebase firebaseHelper;
    private String eventID;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    // Constructor (empty, required for fragment instantiation)
    public MapFragment() {}

    // Method to create a new instance of MapFragment with the eventID as argument
    public static MapFragment newInstance(String eventID) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString("eventID", eventID);  // Put eventID as an argument
        fragment.setArguments(args);  // Set the arguments to the fragment
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Get eventID from arguments
        if (getArguments() != null) {
            eventID = getArguments().getString("eventID");
        }

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize the Firebase helper to fetch user locations
        firebaseHelper = new Firebase(requireContext());

        // Fetch and display locations
        loadUserLocations();

        // Request location updates
        requestLocationUpdates();

        return view;
    }

    // Fetch user locations from Firebase (This is similar to the Google Maps example)
    private void loadUserLocations() {
        firebaseHelper.getUserLocations(eventID, "waitlisted", new Firebase.GeoPointListRetrievalListener() {
            @Override
            public void onGeoPointListRetrievalCompleted(ArrayList<GeoPoint> locations) {
                for (GeoPoint geoPoint : locations) {
                    // Log the latitude and longitude separately
                    double latitude = geoPoint.getLatitude();
                    double longitude = geoPoint.getLongitude();
                    Log.d("UserLocation", "User location: Latitude = " + latitude + ", Longitude = " + longitude);

                    // Optionally, add code to display the location (e.g., on a map or UI element)
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("UserLocation", "Error loading locations: " + e.getMessage());
            }
        });
    }

    // Request location updates
    private void requestLocationUpdates() {
        // Check if the app has permission to access location
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        // Create LocationRequest object
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Update location every 10 seconds
        locationRequest.setFastestInterval(5000); // Fastest interval for location updates

        // LocationCallback to receive updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations() != null) {
                    for (android.location.Location location : locationResult.getLocations()) {
                        // Use location data here (latitude and longitude)
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("CurrentLocation", "Current location: Latitude = " + latitude + ", Longitude = " + longitude);
                        // You can update the UI to show the current location or add markers here
                    }
                }
            }
        };

        // Start location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    // Stop location updates when fragment is paused or destroyed
    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Handle permissions result (if permission was requested for location access)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates(); // Permission granted, start location updates
            } else {
                Toast.makeText(requireContext(), "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
