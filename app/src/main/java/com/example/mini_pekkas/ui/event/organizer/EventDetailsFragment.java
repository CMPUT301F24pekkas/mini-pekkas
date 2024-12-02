package com.example.mini_pekkas.ui.event.organizer;

import static com.example.mini_pekkas.QRCodeGenerator.generateQRCode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.User;
import com.example.mini_pekkas.notification.Notifications;
import com.example.mini_pekkas.QRCodeGenerator;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentChoosePartBinding;
import com.example.mini_pekkas.databinding.FragmentEventOrgBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.widget.TextView;

/**
 * Fragment for displaying the event details and enabling the editing of an event.
 * It shows event description, name, organizer, location, and the event poster.
 * The user can also navigate to the Edit Event fragment to modify the event.
 */
public class EventDetailsFragment extends Fragment {

    private FragmentEventOrgBinding binding;
    private OrganizerEventsListViewModel organizerEventsListViewModel;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Firebase firebaseHelper;

    /**
     * Inflates the layout, binds the views, and sets up the event details in the fragment.
     * Also sets up the navigation to edit event.
     *
     * @param inflater           the LayoutInflater object to inflate the fragment layout
     * @param container          the ViewGroup container to attach the fragment to
     * @param savedInstanceState the previous state of the fragment, if available
     * @return the root view of the fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView", "EventDetailsFragment onCreateView called");
        binding = FragmentEventOrgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        firebaseHelper = new Firebase(requireContext());
        // Get selected event from ViewModel
        organizerEventsListViewModel = new ViewModelProvider(requireActivity(), new OrganizerEventsListViewModelFactory(getActivity()))
                .get(OrganizerEventsListViewModel.class);
        // Set elements in fragment to selected event
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
        if (event != null) {
            updateEventDetailsUI(event);
        }
        //observe event data if edited later
        organizerEventsListViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), this::updateEventDetailsUI);
        SetButtonListeners();

        String url = event.getPosterPhotoUrl();
        if (url != null) {
            Glide.with(this).load(url).into(binding.eventImageView);
        } else {
            binding.eventImageView.setImageResource(R.drawable.no_image);
        }

        String qrCode = event.getQrCode();
        Bitmap qrCodeBitmap = generateQRCode(qrCode, 300, 300);
        binding.qrImage.setImageBitmap(qrCodeBitmap);
        TextView waitlistedAmountView = binding.waitlistedAmountView;
        Firebase.UserListRetrievalListener listener = new Firebase.UserListRetrievalListener() {
            @Override
            public void onUserListRetrievalCompleted(ArrayList<User> users) {
                if(users.size() == 1) {
                    waitlistedAmountView.setText(users.size() + " entrant waitlisted");
                }
                else {
                    waitlistedAmountView.setText(users.size() + " entrants waitlisted");
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("user", "Error occurred: " + e.getMessage());
            }
        };

        // Make sure currentEvent is not null before calling Firebase
        firebaseHelper.getWaitlistedUsers(event.getId(), listener);


        return root;
    }

    /**
     * sets the button listeners of the fragment
     */
    private void SetButtonListeners() {
        //editEvent
        Button editButton = binding.editEventButton;
        editButton.setOnClickListener(v -> {
            // Navigate to addEvent fragment with values changed
            NavController navController = NavHostFragment.findNavController(EventDetailsFragment.this);
            navController.navigate(R.id.action_event_details_to_edit_event);
        });
        //edit poster image
        ImageButton editPosterButton = binding.editEventPictureButton;
        editPosterButton.setOnClickListener(v -> openImageChooser());
        //choose participants
        ImageButton chooseButton = binding.chooseButton;
        chooseButton.setOnClickListener(v -> {
            Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
            ArrayList<Notifications> notifications = createDefaultNotifications();
            firebaseHelper.startEnrollingEvent(event, notifications, new Firebase.InitializationListener() {
                    @Override
                    public void onInitialized() {
                        EnrollmentStatusHelper.setEnrollmentStarted(requireContext(), event.getId(), true);
                    }
            });
            View fragmentView = getView();
            assert fragmentView != null;
            NavController navController = Navigation.findNavController(fragmentView);
            navController.navigate(R.id.action_event_details_to_choose_participants);
        });
        ImageButton waitButton = binding.waitButton;
        waitButton.setOnClickListener(v->{
            NavController navController = NavHostFragment.findNavController(EventDetailsFragment.this);
            navController.navigate(R.id.action_event_details_to_waitlisted_entrants);

        });
    }

    /**
     * creates Default Notifications for the event
     * @return an Arraylist of the default Notifications
     */
    private ArrayList<Notifications> createDefaultNotifications(){
        Timestamp serverTimestamp = Timestamp.now();
        ArrayList<Notifications> notifications = new ArrayList<>();
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
        String eventTitle = event.getName();
        String eventId = event.getId();

        Notifications notification1 = new Notifications("Chosen", "you have been chosen to attend " + eventTitle, serverTimestamp, 0, eventId);
        notifications.add(notification1);
        Notifications notification2 = new Notifications("Not Chosen", "unfortunately you have not been chosen to attend " + eventTitle, serverTimestamp, 0, eventId);
        notifications.add(notification2);
        return notifications;

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.eventImageView.setImageURI(imageUri);  // Show selected image in ImageView
            uploadImageToFirebase(imageUri);        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String fileName = "event_images/" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();  // The URL of the uploaded image
                updateEventPosterUrl(imageUrl);
            });
        }).addOnFailureListener(exception -> {
            Log.e("Firebase", "Image upload failed: " + exception.getMessage());
        });
    }

    private void updateEventPosterUrl(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();

        if (event != null) {
            event.setPosterPhotoUrl(imageUrl);  // Set the new poster URL

            String eventId = event.getId();

            // Create a map with the updated data
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("posterPhotoUrl", imageUrl);

            // Update the Firestore document with the new URL
            db.collection("events")
                    .document(eventId)
                    .update(eventData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Event poster updated successfully");
                        // Optionally, reload the updated event details
                        updateEventDetailsUI(event);  // You can reload the event details after update
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error updating event: " + e.getMessage());
                    });
        }
    }


    private void updateEventDetailsUI(Event event) {
        binding.eventDescriptionView.setText(event.getDescription());
        binding.eventDetailsView.setText("Details Placeholder");
        binding.eventNameView.setText(event.getName());
        binding.organizerNameView.setText(event.getEventHost().getName());
        if (event.isGeo()) {
            binding.locationView.setText("Location Placeholder");
        } else {
            binding.locationView.setText("No Location");
        }

        // Set poster picture
        String posterUrlString = event.getPosterPhotoUrl();
        if (posterUrlString != null) {
            // Check if permissions are granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Permission not granted");
                // Request permission
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.INTERNET},
                        100);
            } else {
                // Permission already granted, proceed with loading image
                Log.d("Permission", "Permission granted");
                Glide.with(this).load(posterUrlString).into(binding.eventImageView);
            }
        } else {
            Log.d("Permission", "URL is NULL");
        }

        // Set organizer profile picture
        String organizerProfileUrl = event.getEventHost().getProfilePhotoUrl();
        Glide.with(this).load(organizerProfileUrl).into(binding.profilePictureImage);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
