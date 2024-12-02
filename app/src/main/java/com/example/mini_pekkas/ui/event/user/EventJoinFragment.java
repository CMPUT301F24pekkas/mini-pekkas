package com.example.mini_pekkas.ui.event.user;

import static com.example.mini_pekkas.QRCodeGenerator.generateQRCode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEventJoinBinding;
import com.example.mini_pekkas.databinding.FragmentEventJoinWaitBinding;
import com.example.mini_pekkas.databinding.FragmentEventJoinGeoBinding;
import com.example.mini_pekkas.ui.home.HomeEventsListViewModelFactory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.mini_pekkas.ui.home.HomeEventsListViewModel;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/**
 * A fragment that handles joining an event's waitlist.
 * The fragment displays event details, and allows the user to join the waitlist by providing a device ID.
 * It fetches event data from Firebase based on the QR code data passed to it and allows the user
 * to confirm joining the waitlist via a dialog.
 */
public class EventJoinFragment extends Fragment {

    private FragmentEventJoinBinding binding;
    private HomeEventsListViewModel homeEventsListViewModel;
    private EventViewModel eventViewModel;
    private SharedEventViewModel sharedEventViewModel;
    private Firebase firebaseHelper;

    /**
     * Initializes the fragment and sets up necessary view models and Firebase helper.
     * Observes QR code data to fetch the event details, and populates the UI with event information.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment
     * @param container          The parent view to which the fragment's UI is attached
     * @param savedInstanceState Previously saved state information for the fragment
     * @return The root view of the fragment's layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        firebaseHelper = new Firebase(requireContext());
        homeEventsListViewModel = new ViewModelProvider(requireActivity(), new HomeEventsListViewModelFactory(getActivity()))
                .get(HomeEventsListViewModel.class);

        binding = FragmentEventJoinBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedEventViewModel = new ViewModelProvider(requireActivity()).get(SharedEventViewModel.class);
        sharedEventViewModel.getQrCodeData().observe(getViewLifecycleOwner(), qrCodeData -> {
            if (qrCodeData != null) {
                fetchEventFromFirebase(qrCodeData, sharedEventViewModel);
            }
        });
        sharedEventViewModel.getEventDetails().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                firebaseHelper.getStatusInEvent(event, new Firebase.DataRetrievalListener() {
                    @Override
                    public void onRetrievalCompleted(String status) {
                        if (Objects.equals(status, "waitlisted")){
                            NavController navController = NavHostFragment.findNavController(EventJoinFragment.this);
                            navController.navigate(R.id.action_navigation_event2_to_navigation_event);
                        } else if (Objects.equals(status, "pending") || Objects.equals(status, "enrolled")){
                            NavController navController = NavHostFragment.findNavController(EventJoinFragment.this);
                            navController.navigate(R.id.action_navigation_event2_to_navigation_event3);
                        }
                    }
                });
                String url = event.getPosterPhotoUrl();
                String qrCode = event.getQrCode();
                Bitmap qrCodeBitmap = generateQRCode(qrCode, 300, 300);

                binding.eventNameView.setText(event.getName());
                binding.organizerNameView.setText(event.getEventHost().getName());
                binding.eventDescriptionView.setText(event.getDescription());
                binding.locationView.setText(event.getFacility());
                if (url != null) {
                    Glide.with(this).load(url).into(binding.eventImageView);
                } else {
                    binding.eventImageView.setImageResource(R.drawable.no_image);
                }
                binding.qrImage.setImageBitmap(qrCodeBitmap);
                binding.priceView.setText(String.format(Locale.getDefault(), "$%.2f", event.getPrice()));
                Date startDate = event.getStartDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = dateFormat.format(startDate);
                binding.startDateView.setText(formattedDate);
            }
        });

        Button joinWaitlistButton = binding.joinWaitButton;
        joinWaitlistButton.setOnClickListener(v -> {
            Event event = sharedEventViewModel.getEventDetails().getValue();
            if (event != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("events").document(event.getId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Boolean isGeoEnabled = documentSnapshot.getBoolean("geo");
                                if (isGeoEnabled != null && isGeoEnabled) {
                                    // Get the users geo location in a GeoPoint
                                    GeoPoint location = getUserLocation(getContext());

                                    FragmentEventJoinGeoBinding joinGeoBinding = FragmentEventJoinGeoBinding.inflate(LayoutInflater.from(getContext()));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setView(joinGeoBinding.getRoot());
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                    Button joinGeoButton = joinGeoBinding.joinWaitButton;
                                    Button cancelGeoButton = joinGeoBinding.cancelWaitButton;
                                    showJoinDialog(dialog, joinGeoButton, cancelGeoButton, location);
                                } else {
                                    FragmentEventJoinWaitBinding joinWaitBinding = FragmentEventJoinWaitBinding.inflate(LayoutInflater.from(getContext()));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setView(joinWaitBinding.getRoot());
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                    Button joinWaitButton = joinWaitBinding.joinWaitButton;
                                    Button cancelWaitButton = joinWaitBinding.cancelWaitButton;
                                    showJoinDialog(dialog, joinWaitButton, cancelWaitButton, null);
                                }
                            } else {
                                Toast.makeText(requireContext(), "Event not found in Firebase", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Error fetching event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
        return root;

    }

    /**
     * Fetches the event details from Firebase using the provided QR code data.
     * Updates the event details in the shared and local view models.
     *
     * @param qrCodeData The QR code data used to fetch the event.
     * @param sharedViewModel The shared view model used for updating the event data.
     */
    private void fetchEventFromFirebase(String qrCodeData, SharedEventViewModel sharedViewModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("QrCode", qrCodeData)
                .get()
                .addOnSuccessListener(task -> {
                    if (task.getDocuments().isEmpty()) {
                        Toast.makeText(getContext(), "No Event Found", Toast.LENGTH_SHORT).show();
                    } else {
                        DocumentSnapshot document = task.getDocuments().get(0);
                        Event newEvent = new Event(document.getData());

                        // Prevent redundant updates
                        Event currentEvent = sharedViewModel.getEventDetails().getValue();
                        if (currentEvent != null && currentEvent.getId().equals(newEvent.getId())) {
                            return;
                        }

                        sharedViewModel.setEventDetails(newEvent);
                        eventViewModel.setEvent(newEvent);
                    }
                });
    }


    /**
     * Shows the dialog for joining the waitlist and allows user to either join or cancel
     *
     * @param dialog The AlertDialog with either a simple confirmation or with a geolocation confirmation if needed
     * @param joinButton The button to join the wait list
     * @param cancelButton The button to cancel joining the wait list
     * @param location The geo location of the user if needed. Null if not used
     */
    private void showJoinDialog(AlertDialog dialog, Button joinButton, Button cancelButton, GeoPoint location){
        joinButton.setOnClickListener(view -> {
            Event event = sharedEventViewModel.getEventDetails().getValue();
            if (event != null) {
                firebaseHelper.countWaitlistedUsers(event.getId(), new Firebase.ResultListener<Integer>() {
                    @Override
                    public void onSuccess(Integer count) {
                        if (count < event.getMaxWaitlist()){
                            if (location == null) {
                                firebaseHelper.waitlistEvent(event);
                            } else {
                                firebaseHelper.waitlistEvent(event, location);
                            }
                            homeEventsListViewModel.addEvent(event);
                            homeEventsListViewModel.setSelectedEvent(event);
                            NavController navController = NavHostFragment.findNavController(EventJoinFragment.this);
                            navController.navigate(R.id.action_navigation_event2_to_navigation_event);
                            Toast.makeText(requireContext(), "Joining waitlist...", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(requireContext(), "Sorry, waitlist is full" + count, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(String errorMessage) {
                        Log.e("WaitlistedCount", "Error: " + errorMessage);
                    }
                });
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());
    }

    /**
     * Gets the device's current location.
     * @param context
     * @return GeoPoint representing the device's location
     */
    public GeoPoint getUserLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            return new GeoPoint(latitude, longitude);
        } else {
            return null;
        }
    }

    /**
     * Called when the fragment's view is destroyed. Cleans up the binding to prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}