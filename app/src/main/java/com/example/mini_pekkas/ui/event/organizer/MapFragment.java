package com.example.mini_pekkas.ui.event.organizer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Firebase firebaseHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private String eventID;

    public static MapFragment newInstance(String eventId) {
        return null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Get eventID from arguments
        if (getArguments() != null) {
            eventID = getArguments().getString("eventID");
        }

        // Initialize Firebase helper
        firebaseHelper = new Firebase(requireContext());

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Enable location if permission granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
            fetchAndDisplayUserLocations();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void fetchAndDisplayUserLocations() {
        firebaseHelper.getUserLocations(eventID, "waitlisted", new Firebase.GeoPointListRetrievalListener() {
            @Override
            public void onGeoPointListRetrievalCompleted(ArrayList<GeoPoint> locations) {
                for (GeoPoint geoPoint : locations) {
                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("User Location"));
                }

                // Optionally move the camera to the first location
                if (!locations.isEmpty()) {
                    GeoPoint firstLocation = locations.get(0);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude()), 12));
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle errors here
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                fetchAndDisplayUserLocations();
            }
        }
    }
}
