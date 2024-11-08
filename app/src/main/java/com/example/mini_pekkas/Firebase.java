package com.example.mini_pekkas;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used to interface with Firebase and should be used for all Firebase operations.
 * Any functions that get and request data needs a user defined listener. This is a function that's called after an operation is completed.
 * Every listener will have a on success and an optional on error listener (if not overwritten, the default error handling is to print the error in the log)
 * @author ryan
 * @version 1.13 11/12/20224 Fixed alot of logical bugs
 */
public class Firebase {
    private final String deviceID;
    private final CollectionReference userCollection;
    private final CollectionReference eventCollection;
    private final CollectionReference userEventsCollection;
    private final CollectionReference adminCollection;
    private final StorageReference profilePictureReference;
    private final StorageReference posterPictureReference;

    private DocumentSnapshot userDocument;

    /**
     * Firebase constructor. Initializes the database and all collection references
     * @param context
     * the context of the activity the function is called from
     */
    @SuppressLint("HardwareIds") // We just need the device id as a string
    public Firebase(Context context) {
        // Initialize the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize all collection references
        userCollection = db.collection("users");
        eventCollection = db.collection("events");
        userEventsCollection = db.collection("user-events");
        adminCollection = db.collection("admins");

        //Initialize our storage references
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        profilePictureReference = storageReference.child("profile-pictures");
        posterPictureReference = storageReference.child("poster-pictures");

        // Get the device id
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch the user document
        fetchUserDocument(() -> userDocument.getReference().addSnapshotListener((value, error) -> {
            // Update the user document whenever data gets updated
            this.userDocument = value;
            }));
    }

    /**
     * Interface for functions that initializes data and doesn't return anything
     */
    public interface InitializationListener {
        void onInitialized();
        default void onError(Exception e) {Log.e(TAG, "Error initializing data: ", e);}
    }

    /**
     * Interface for functions that retrieve string.
     */
    public interface DataRetrievalListener {
        void onRetrievalCompleted(String result);
        default void onError(Exception e) {
            Log.e(TAG, "Error retrieving data: ", e);
        }
    }

    /**
     * Interface for functions that check a conditional. Returns true if the conditional is met
     */
    public interface CheckListener {
        void onCheckComplete(boolean exist);
        default void onError(Exception e) {
            Log.e(TAG, "Error checking conditional: ", e);
        }
    }

    /**
     * Interface for getUser. Fetches and returns an user object
     */
    public interface UserRetrievalListener {
        void onUserRetrievalCompleted(Event event);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting data: ", e);
        }
    }

    /**
     * Interface for functions that retrieve an array of users
     */
    public interface UserListRetrievalListener {
        void onUserListRetrievalCompleted(ArrayList<Event> events);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting events: ", e);
        }
    }

    /**
     * Interface for getEvent. Fetches and returns an event object
     */
    public interface EventRetrievalListener {
        void onEventRetrievalCompleted(Event event);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting data: ", e);
        }
    }

    /**
     * Interface for functions that retrieve an array of events
     */
    public interface EventListRetrievalListener {
        void onEventListRetrievalCompleted(ArrayList<Event> events);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting events: ", e);
        }
    }

    /**
     * Interface for functions that return an image in Uri format.
     */
    public interface ImageRetrievalListener {
        void onImageRetrievalCompleted(Uri image);
        default void onError(Exception e) {Log.e(TAG, "Error getting image: ", e);}
    }

    /**
     * This function fetches the user document and stores it in UserDocument
     * This function shouldn't need to be called as the constructor will initialize the user document
     * Call checkThisUserExist first if the existence of the user document is not known
     * @param listener An InitializationListener listener that runs on a successful fetch
     */
    public void fetchUserDocument(InitializationListener listener) {
        userCollection.document(this.deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    userDocument = documentSnapshot;
                    listener.onInitialized();
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Checks if the user document exists in the firestore.
     * @param listener A CheckListener listener that is called when the check is complete. Returns true if the document exists
     */
    public void checkThisUserExist(CheckListener listener) {
        if (userDocument != null) {
            listener.onCheckComplete(true);
        } else {
            userCollection.document(deviceID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                            // Check if the document exists
                            boolean exist = documentSnapshot.exists();

                            // Call the listener, return true if the document exists
                            listener.onCheckComplete(exist);
                    })
                    .addOnFailureListener(listener::onError);
        }
    }

    /**
     * Initializes this user into firebase
     * @param user A user object to be initialized
     * @param listener An InitializationListener listener that is called after the user is initialized
     */
    public void InitializeThisUser(User user, InitializationListener listener) {
        userCollection.document(this.deviceID)
                .set(user.toMap())
                .addOnSuccessListener(v -> {
                    // Re fetch the user document. Listener call will now have a valid user document
                    fetchUserDocument(listener);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Gets and returns a User object stored in UserDocument
     * @return the user document as a User object. Throws an error if the user does not exist
     */
    public User getThisUser() {
        return new User(Objects.requireNonNull(userDocument.getData()));
    }

    /**
     * Updates the user document with the new user object
     * @param user a user object with new data to be updated
     * @param listener Optional InitializationListener listener that is called after the user is updated
     */
    public void updateThisUser(User user, InitializationListener listener) {
        userCollection.document(this.deviceID)
                .set(user.toMap())
                .addOnSuccessListener(aVoid -> listener.onInitialized())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #updateThisUser(User, InitializationListener)} with no listener
     */
    public void updateThisUser(User user) {
        updateThisUser(user, () -> {});
    }

    /**
     * Deletes this user, essentially starting with a clean slate
     * also deletes related documents in user-events collection
     * @param listener Optional InitializationListener listener that is called after the user is deleted
     */
    public void deleteThisUser(InitializationListener listener) {
        // Delete the profile picture from user (assuming the deletion works)
        deleteProfilePicture(new User(Objects.requireNonNull(userDocument.getData())));

        userCollection.document(this.deviceID).delete()
                .addOnSuccessListener(aVoid -> {
                    userDocument = null;        // Set user document to null
                })
                .addOnFailureListener(listener::onError);

        // Delete all user document from the user-events collection
        userEventsCollection.whereEqualTo("userID", this.deviceID).get()
                .addOnSuccessListener(task -> {
                    int total_user = task.size();
                    AtomicInteger deleted_user = new AtomicInteger();
                    // Delete all documents that match the query
                    for (DocumentSnapshot document : task.getDocuments()) {
                        document.getReference().delete()
                                .addOnFailureListener(listener::onError);

                        // Wait for all documents to be deleted first
                        if (deleted_user.incrementAndGet() == total_user) {
                            listener.onInitialized();
                        }
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #deleteThisUser(InitializationListener)} with no listener
     */
    public void deleteThisUser() {
        deleteThisUser(() -> {});
    }


    // TODO add event functions

    /**
     * Add an event to the events collection
     * @param event an event object to be added
     * @param listener Optional DataRetrievalListener listener that is called after the event is added
     */
    public void addEvent(Event event, DataRetrievalListener listener) {
        eventCollection.add(event.toMap())
                .addOnSuccessListener(documentReference -> {
                    // Retrieve the ID of the document and update the event object
                    String id = documentReference.getId();
                    event.setId(id);

                    // And update the id field in firestore
                    documentReference.update("id", id);

                    // Set this user as the organizer of the event
                    organizeEvent(event);

                    // Finally call the listener on completion
                    listener.onRetrievalCompleted(id);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #addEvent(Event, DataRetrievalListener)} with no listener
     */
    public void addEvent(Event event) {
        addEvent(event, id -> {});
    }

    /**
     * Delete an event from the events and user-events collection
     * @param event an event object to be deleted
     * @param listener Optional InitializationListener listener that is called after the event is deleted
     */
    public void deleteEvent(Event event, InitializationListener listener) {
        String eventID = event.getId();

        // Delete the banner image
        deletePosterPicture(event);

        // Delete the event document
        eventCollection.document(eventID).delete();

        // Delete all event document from the user-events collection
        userEventsCollection.whereEqualTo("eventID", eventID).get()
                .addOnSuccessListener(task -> {
                    // Counter for the number of documents deleted
                    int total_events = task.size();
                    AtomicInteger deleted_events = new AtomicInteger();

                    for (DocumentSnapshot document : task.getDocuments()) {
                        document.getReference().delete()
                                .addOnFailureListener(listener::onError);

                        // Wait for all events to be deleted first
                        if (deleted_events.incrementAndGet() == total_events) {
                            listener.onInitialized();
                        }
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #deleteEvent(Event, InitializationListener)} with no listener
     */
    public void deleteEvent(Event event) {
        deleteEvent(event, () -> {});
    }

    /**
     * Update an event in the events collection
     * @param event an event object to be updated
     * @param listener Optional InitializationListener listener that is called after the event is updated
     */
    public void updateEvent(Event event, InitializationListener listener) {
        String eventID = event.getId();
        eventCollection.document(eventID).set(event.toMap())
                .addOnSuccessListener(aVoid -> listener.onInitialized())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #updateEvent(Event, InitializationListener)} with no listener
     */
    public void updateEvent(Event event) {
        updateEvent(event, () -> {});
    }

    /**
     * Get an event from the events collection
     * @param eventID the ID of the event to be retrieved
     * @param listener an EventRetrievalListener listener that returns the newly received event object
     */
    public void getEvent(String eventID, EventRetrievalListener listener) {
        eventCollection.document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Throw an error if the document does not exist
                    if (!documentSnapshot.exists()) {
                        listener.onError(new Exception("Event does not exist"));
                    } else {
                        // Else return the event object
                        Event event = new Event(Objects.requireNonNull(documentSnapshot.getData()));
                        listener.onEventRetrievalCompleted(event);
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error getting event", e));
    }

    // TODO enrollment functionality

    /**
     * This function is called whenever an event is created. Sets this user as the organizer
     * @param event an event object that's being organized
     * @param listener Optional InitializationListener listener that is called after the event is organized
     */
    private void organizeEvent(Event event, InitializationListener listener) {
        // Set waitlist to an empty array of user references
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventID", event.getId());
        map.put("userID", this.deviceID);
        map.put("status", "organized");

        userEventsCollection.add(map)
                .addOnSuccessListener(aVoid -> listener.onInitialized())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #organizeEvent(Event, InitializationListener)} with no listener
     */
    private void organizeEvent(Event event) {
        organizeEvent(event, () -> {});
    }

    /**
     * Waitlist this user into the event. Creates a new entry in the user-events collection
     * @param event an event object to be waitlisted into
     * @param listener Optional InitializationListener listener that is called after the event is waitlisted
     */
    public void waitlistEvent(Event event, InitializationListener listener) {
        // Set waitlist to an empty array of user references
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventID", event.getId());
        map.put("userID", this.deviceID);
        map.put("status", "waitlisted");

        userEventsCollection.add(map)
                .addOnSuccessListener(aVoid -> listener.onInitialized())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #waitlistEvent(Event, InitializationListener)} with no listener
     */
    public void waitlistEvent(Event event) {
        waitlistEvent(event, () -> {});
    }

    /**
     * Enroll this user into the event
     * @param event an event object to be enrolled into
     * @param listener Optional InitializationListener listener that is called after the event is enrolled
     */
    public void enrollEvent(Event event, InitializationListener listener) {
        // Find the document that matches the user to event query
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", this.deviceID).get()
                .addOnSuccessListener(task -> {
                    // Get and update the one document that matches the query
                    DocumentSnapshot document = task.getDocuments().get(0);
                    // Update the status
                    document.getReference().update("status", "enrolled")
                            .addOnSuccessListener(aVoid -> listener.onInitialized())
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #enrollEvent(Event, InitializationListener)} with no listener
     */
    public void enrollEvent(Event event) {
        enrollEvent(event, () -> {});
    }

    /**
     * Cancel the event, removing oneself from the waitlist or enrolling
     * @param event an event object to be waitlisted into
     * @param listener Optional InitializationListener listener that is called after the event is waitlisted
     */
    public void cancelEvent(Event event, InitializationListener listener) {
        // Find the document that matches the user to event query
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", this.deviceID).get()
                .addOnSuccessListener(task -> {
                    // Get and update the one document that matches the query
                    DocumentSnapshot document = task.getDocuments().get(0);
                    // Update the status
                    document.getReference().update("status", "cancelled")
                            .addOnSuccessListener(aVoid -> listener.onInitialized())
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #cancelEvent(Event, InitializationListener)} with no listener
     */
    public void cancelEvent(Event event) {
        cancelEvent(event, () -> {});
    }

    /**
     * Get the current status of the user in the event
     * @param event an event object to get the status of
     * @param listener A DataRetrievalListener listener that returns the status of the user in the event
     */
    public void getStatusInEvent(Event event, DataRetrievalListener listener) {
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", this.deviceID).get()
                .addOnSuccessListener(task -> {
                    // Get the one document that matches the query
                    DocumentSnapshot document = task.getDocuments().get(0);
                    // Return the status as a string: waitlisted, enrolled, cancelled
                    listener.onRetrievalCompleted(Objects.requireNonNull(document.get("status")).toString());
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Gets an ArrayList of events this user is in, specified by the status
     * This function is called by getXEvents() where X is the status
     * @param status the status of the events to retrieve. [waitlisted, enrolled, cancelled, organized]
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    private void getEventByStatus(String status, EventListRetrievalListener listener) {
        userEventsCollection.whereEqualTo("userID", this.deviceID).whereEqualTo("status", status).get()
                .addOnSuccessListener(task -> {
                    ArrayList<Event> events = new ArrayList<>(); // Get the array of events the user is waitlisted in
                    int totalEvents = task.getDocuments().size(); // Total events to retrieve
                    AtomicInteger retrievedEvents = new AtomicInteger(); // Counter for retrieved events

                    for (DocumentSnapshot document : task.getDocuments()) {
                        // Get the event ID to pull from the event collection
                        String eventID = Objects.requireNonNull(document.get("eventID")).toString();
                        // Get the event from the event collection
                        eventCollection.document(eventID).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    // Create the new event object and store it in the array
                                    Event event = new Event(Objects.requireNonNull(document.getData()));
                                    events.add(event);

                                    // Increment and check if we have retrieved all events
                                    if (retrievedEvents.getAndIncrement() == totalEvents){
                                        listener.onEventListRetrievalCompleted(events);
                                    }
                                })
                                .addOnFailureListener(listener::onError);
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Retrieves an event using its QR code.
     * @param qrCode The Base64 encoded QR code string to match.
     * @param listener An EventRetrievalListener that returns the Event object if found.
     */
     public void getEventByQRCode(String qrCode, EventRetrievalListener listener) {
         eventCollection.whereEqualTo("QrCode", qrCode).get()
                 .addOnSuccessListener(task -> {
                     // Check if any documents were found
                     if (task.getDocuments().isEmpty()) {
                         // No event found, trigger callback with null
                         Log.d("Camera Yay", "Event NOTHING");
                         listener.onEventRetrievalCompleted(null);
                         return;
                     } else {
                         // Fetch the event details from the document
                         DocumentSnapshot document = task.getDocuments().get(0);
                         Event event = new Event(Objects.requireNonNull(document.getData()));
                         Log.d("Camera Yay", "Event succesfully Retrieved");
                         listener.onEventRetrievalCompleted(event);
                     }
                 })
                 .addOnFailureListener(listener::onError);
     }

    /**
     * Get all events the user is waitlisted in
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    public void getOrganizedEvents(EventListRetrievalListener listener) {
        getEventByStatus("organized", listener);
    }

    /**
     * Get all events the user is waitlisted in
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    public void getWaitlistedEvents(EventListRetrievalListener listener) {
        getEventByStatus("waitlisted", listener);
    }

    /**
     * Get all events the user is enrolled in
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    public void getEnrolledEvents(EventListRetrievalListener listener) {
        getEventByStatus("enrolled", listener);
    }

    /**
     * Get all events the user has cancelled
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    public void getCancelledEvents(EventListRetrievalListener listener) {
        getEventByStatus("cancelled", listener);
    }
    // TODO add Image storage functions. Allows for the storing and retrieving of various media files

    /**
     * A private function that stores the profile picture in the storage.
     * This function is called from uploadProfilePicture and should not be invoked directly
     * @param user the user object to store the profile picture in
     * @param image the image to be stored in Uri format
     * @param listener Optional listener that is called when the image is stored. Returns the image url as a string
     */
    private void uploadProfilePicture(User user, Uri image, DataRetrievalListener listener) {
        profilePictureReference.putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    String imagePath = Objects.requireNonNull(taskSnapshot.getMetadata()).getPath(); // Get the path of the uploaded image
                    user.setProfilePhotoUrl(imagePath); // Set the profile photo field in the user object
                    listener.onRetrievalCompleted(imagePath);   // Pass the image reference to the listener

                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #uploadProfilePicture(User, Uri, DataRetrievalListener)} with no listener
     */
    private void uploadProfilePicture(User user, Uri image) {
        uploadProfilePicture(user, image, id -> {});
    }

    /**
     * Stores the profile picture in the storage. Listener is required
     * This function automatically handles the updating of the profile picture in the user object
     * @param user the user object to store the profile picture in
     * @param image the image to be stored in Uri format
     * @param listener Optional DataRetrievalListener listener that is called when the image is stored. Returns the image url as a string
     */
    public void setProfilePicture(User user, Uri image, DataRetrievalListener listener) {
        // Fetch the current profile picture path
        String currentImagePath = user.getProfilePhotoUrl();

        // Delete the old profile picture (if it exists)
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            // First delete the old pfp deleting old profile picture
            // Then upload new profile pic on success
            deleteProfilePicture(user, () -> uploadProfilePicture(user, image, listener));
        } else {
            // No old profile picture to delete, upload the new one directly
            uploadProfilePicture(user, image, listener);
        }
    }

    /**
     * Overload of the {@link #setProfilePicture(User, Uri, DataRetrievalListener)} with no listener
     */
    public void setProfilePicture(User user, Uri image) {
        setProfilePicture(user, image, id -> {});
    }

    /**
     * Gets the profile picture from the storage.
     * @param user the user object to get the profile picture from
     * @param listener the listener that is called when the image is retrieved. Returns the image as a Uri
     */
    public void getProfilePicture(User user, ImageRetrievalListener listener) {
        profilePictureReference.child(user.getProfilePhotoUrl()).getDownloadUrl()
                .addOnSuccessListener(listener::onImageRetrievalCompleted)
                .addOnFailureListener(listener::onError);
    }

    /**
     * Deletes the profile picture from the storage.
     * Throws an error if the profile picture does not exist
     * @param user the user object to delete the profile picture from
     * @param listener Optional InitializationListener listener that is called when the image is deleted
     */
    public void deleteProfilePicture(User user, InitializationListener listener) {
        String profilePhotoUrl = user.getProfilePhotoUrl();
        if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
            profilePictureReference.child(user.getProfilePhotoUrl()).delete()
                    .addOnSuccessListener(aVoid -> listener.onInitialized())
                    .addOnFailureListener(listener::onError);
        } else {
            // Throw an exception if the profile picture does not exist
            listener.onError(new Exception("No profile picture to delete"));
        }
    }

    /**
     * Overload of the {@link #deleteProfilePicture(User, InitializationListener)} with no listener
     */
    public void deleteProfilePicture(User user) {
        deleteProfilePicture(user, () -> {});
    }

    /**
     * A private function that stores the banner picture in the storage.
     * @param event the event object to store the banner picture in
     * @param image the image to be stored in Uri format
     * @param listener Optional listener that is called when the image is stored. Returns the image url as a string
     */
    private void setPosterPicture(Event event, Uri image, DataRetrievalListener listener) {
        posterPictureReference.putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    String imagePath = Objects.requireNonNull(taskSnapshot.getMetadata()).getPath(); // Get the path of the uploaded image
                    event.setPosterPhotoUrl(imagePath); // Set the profile photo field in the user object
                    listener.onRetrievalCompleted(imagePath);   // Pass the image reference to the listener

                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #setPosterPicture(Event, Uri, DataRetrievalListener)} with no listener
     */
    private void setPosterPicture(Event event, Uri image) {
        setPosterPicture(event, image, id -> {});
    }

    /**
     * Stores the poster picture in the storage. Listener is required
     * This function automatically handles the updating of the poster picture in the event object
     * @param event the event object to store the poster picture in
     * @param image the image to be stored in Uri format
     * @param listener the listener that is called when the image is stored. Returns the image url as a string
     */
    public void uploadPosterPicture(Event event, Uri image, DataRetrievalListener listener) {
        // Fetch the current profile picture path
        String currentImagePath = event.getPosterPhotoUrl();

        // Delete the old profile picture (if it exists)
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            // Upload the new image in it's place
            deletePosterPicture(event, () -> uploadPosterPicture(event, image, listener));
        } else {
            // No old poster picture to delete, upload the new one directly
            uploadPosterPicture(event, image, listener);
        }
    }

    /**
     * Overload of the {@link #uploadPosterPicture(Event, Uri, DataRetrievalListener)} with no listener
     */
    public void uploadPosterPicture(Event event, Uri image) {
        uploadPosterPicture(event, image, id -> {});
    }

    /**
     * Gets the poster picture from the storage.
     * @param event the event object to get the profile picture from
     * @param listener the listener that is called when the image is retrieved. Returns the image as a Uri
     */
    public void getPosterPicture(Event event, ImageRetrievalListener listener) {
        profilePictureReference.child(event.getPosterPhotoUrl()).getDownloadUrl()
                .addOnSuccessListener(listener::onImageRetrievalCompleted)
                .addOnFailureListener(listener::onError);
    }

    /**
     * Deletes the poster picture from the storage.
     * Throws an exception if there is no poster picture
     * @param event the event object to delete the poster picture from
     * @param listener Optional InitializationListener listener that is called when the image is deleted
     */
    public void deletePosterPicture(Event event, InitializationListener listener) {
        // Get the photo Url and delete it
        String photoUrl = event.getPosterPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            posterPictureReference.child(photoUrl).delete()
                .addOnSuccessListener(aVoid -> listener.onInitialized())
                .addOnFailureListener(listener::onError);
        } else {
            // Throw an exception
            listener.onError(new Exception("No poster picture to delete"));
        }
    }

    /**
     * Overload of the {@link #deletePosterPicture(Event, InitializationListener)} with no listener
     */
    public void deletePosterPicture(Event event) {
        deletePosterPicture(event, () -> {});
    }
    // TODO add admin functions. Allows for deletion of various types of data
    /**
     * Checks if this user is an admin
     * @param listener the listener that is called when the check is complete. Returns true if the user is an admin
     */
    public void isThisUserAdmin(CheckListener listener){
        adminCollection.whereEqualTo("deviceID", this.deviceID).get()
                .addOnSuccessListener(result -> {
                    // If the result is not empty, the user is an admin
                    boolean exist = !result.isEmpty();
                    listener.onCheckComplete(exist);
                });
    }

    /**
     * Searches for users by checking if they have a parameter that matches the query
     * @param query the query to search for
     * @param listener
     */
    public void serachForUsers(String query, UserListRetrievalListener listener) {
        // TODO
    }
}

