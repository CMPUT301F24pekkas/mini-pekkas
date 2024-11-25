package com.example.mini_pekkas;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used to interface with Firebase and should be used for all Firebase operations.
 * Any functions that get and request data needs a user defined listener. This is a function that's called after an operation is completed.
 * Every listener will have a on success and an optional on error listener (if not overwritten, the default error handling is to print the error in the log)
 * @author ryan
 * @version 1.15.3 11/22/2024 null check for get event by status
 */
public class Firebase {
    private final String deviceID;
    private final CollectionReference userCollection;
    private final CollectionReference eventCollection;
    private final CollectionReference userEventsCollection;
    private final CollectionReference userNotificationsCollection;
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
        userNotificationsCollection = db.collection("user-notifications");
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
     * Interface for functions that retrieves a list of strings.
     */
    public interface DataListRetrievalListener {
        void onListRetrievalCompleted(ArrayList<String> result);
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
        void onUserRetrievalCompleted(User users);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting user: ", e);
        }
    }

    /**
     * Interface for functions that retrieve an array of users
     */
    public interface UserListRetrievalListener {
        void onUserListRetrievalCompleted(ArrayList<User> users);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting users: ", e);
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
     * Interface for functions that retrieve the notifications for this use
     */
    public interface NotificationListRetrievalListener {
        void onNotificationListRetrievalCompleted(ArrayList<Notifications> notification);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting notification: ", e);
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
     * Interface for functions that return list of image in Uri format.
     */
    public interface ImageListRetrievalListener {
        void onImageListRetrievalCompleted(ArrayList<Uri> images);
        default void onError(Exception e) {Log.e(TAG, "Error getting images: ", e);}
    }

    /**
     * Interface for admin functions that retrieve an array of document snapshots to be processed later
     */
    public interface QueryRetrievalListener {
        void onQueryRetrievalCompleted(ArrayList<DocumentSnapshot> objects);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting data: ", e);
        }
    }

    /*
     *  Functionality for managing this user (the user of this device)
     */

    /**
     * This function fetches the user document and stores it in UserDocument
     * This function shouldn't need to be called as the constructor will initialize the user document
     * Call checkThisUserExist first if the existence of the user document is not known
     * @param listener An InitializationListener listener that runs on a successful fetch
     */
    public void fetchUserDocument(InitializationListener listener) {
        userCollection.whereEqualTo("userID", deviceID).get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        listener.onError(new Exception("User not found"));
                        return;
                    }

                    // Get the one document and set the new value
                    userDocument = task.getDocuments().get(0);
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
            userCollection.whereEqualTo("userID", deviceID).get()
                    .addOnSuccessListener(task -> {
                            // Check if the document exists
                            boolean exist = !task.isEmpty();

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
        user.setId(deviceID);
        userCollection.add(user.toMap())
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
        userCollection.whereEqualTo("userID", user.getId()).get()
            .addOnSuccessListener(task -> {
                if (task.isEmpty()) {
                    listener.onError(new Exception("User not found"));
                    return;
                }

                // Get the one document and set the new value
                task.getDocuments().get(0).getReference().set(user.toMap())
                    .addOnSuccessListener(aVoid -> listener.onInitialized())
                    .addOnFailureListener(listener::onError);
            })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #updateThisUser(User, InitializationListener)} with no listener
     */
    public void updateThisUser(User user) {
        updateThisUser(user, () -> {});
    }

    /**
     * Deletes a user from the firebase database, uses the user id and the name of its document
     * Deletes User documents, user-events documents, profile pictures, and notifications
     * @param user A user object to be deleted
     * @param listener Optional InitializationListener listener that is called after the user is deleted
     */
    public void deleteUser(User user, InitializationListener listener) {
        deleteProfilePicture(user);
        deleteAllNotification(user);

        // Delete all documents from the user-events collection
        // TODO abstract these operations in one super function
        userEventsCollection.whereEqualTo("userID", deviceID).get()
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
                        if (deleted_user.get() != total_user) {
                            listener.onError(new Exception("Failed to delete all user events"));
                        }
                    }
                })
                .addOnFailureListener(listener::onError);

        userCollection.whereEqualTo("userID", user.getId()).get()
            .addOnSuccessListener(task -> {
                if (task.size() != 1) {
                    listener.onError(new Exception("User not found"));
                    return;
                }

                // Delete the user document
                task.getDocuments().get(0).getReference().delete()
                        .addOnSuccessListener(aVoid -> {
                            userDocument = null;        // Clear the user document
                            listener.onInitialized();
                        })
                        .addOnFailureListener(listener::onError);
            })
            .addOnFailureListener(listener::onError);
    }


    /**
     * Deletes this user, essentially starting with a clean slate
     * also deletes related documents in user-events collection
     * @param listener Optional InitializationListener listener that is called after the user is deleted
     */
    public void deleteThisUser(InitializationListener listener) {
        deleteUser(new User(Objects.requireNonNull(userDocument.getData())), listener);
    }

    /**
     * Overload of the {@link #deleteThisUser(InitializationListener)} with no listener
     */
    public void deleteThisUser() {
        deleteThisUser(() -> {});
    }

    /*
     *  Functionality for managing events
     */

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
        eventCollection.document(eventID).delete()
                .addOnFailureListener(listener::onError);

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
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #getEvent(String eventID, EventRetrievalListener listener)} using the event object itself
     * @param event an event object to be retrieved
     * @param listener an EventRetrievalListener listener that returns the newly received event object
     */
    public void getEvent(Event event, EventRetrievalListener listener) {
        getEvent(event.getId(), listener);
    }

    /*
     *  Functionality for managing notifications of this user
     */

    /**
     * Get all the notifications for this user. Returns an arrayList of notifications sorted by newest date first
     * @param listener a NotificationListRetrievalListener listener that returns an arrayList of notifications
     */
    public void getThisUserNotifications(NotificationListRetrievalListener listener) {
        userNotificationsCollection.whereEqualTo("userID", deviceID).get()
                .addOnSuccessListener(task -> {
                    // Pass an empty array if no notifications exist
                    if (task.isEmpty()) {
                        listener.onNotificationListRetrievalCompleted(new ArrayList<>());
                        return;
                    }

                    ArrayList<Notifications> notifications = new ArrayList<>();
                    int total_notifications = task.size();
                    AtomicInteger notification_count = new AtomicInteger();

                    for (DocumentSnapshot document : task.getDocuments()) {
                        // Remove the user id so Notifications don't store the device id
                        Map<String, Object> map = Objects.requireNonNull(document.getData());
                        map.remove("userID");
                        notifications.add(new Notifications(map));

                        // Wait for all events to be deleted first
                        if (notification_count.incrementAndGet() == total_notifications) {
                            // Sort by newest date first
                            notifications.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                            listener.onNotificationListRetrievalCompleted(notifications);
                        }
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Add a notification to the user-notifications collection
     * @param notification a notification object to be added
     * @param listener Optional InitializationListener listener that is called after the notification is added
     */
    public void addNotification(Notifications notification, InitializationListener listener) {
        // Set the user id and date field in correct format
        HashMap<String, Object> map = notification.toMap();
        map.put("date", notification.getTimestamp());
        map.put("userID", deviceID);

        userNotificationsCollection.add(map)
                .addOnSuccessListener(aVoid -> listener.onInitialized())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #addNotification(Notifications, InitializationListener)} with no listener
     */
    public void addNotification(Notifications notification) {
        addNotification(notification, () -> {});
    }

    /**
     * Delete all notifications for the given user
     * This function is used in delete user to remove it's corresponding notifications
     * @param user a user object where the notifications are to be deleted
     * @param listener Optional InitializationListener listener that is called after the notifications are deleted
     */
    private void deleteAllNotification(User user, InitializationListener listener) {
        userNotificationsCollection.whereEqualTo("userID", user.getId()).get()
                .addOnSuccessListener(task -> {
                    int total_notifications = task.size();
                    AtomicInteger deleted_notifications = new AtomicInteger();

                    // Fetch all notification documents and delete it
                    for (DocumentSnapshot document : task.getDocuments()) {
                        document.getReference().delete()
                                .addOnFailureListener(listener::onError);
                        if (deleted_notifications.incrementAndGet() == total_notifications) {
                            listener.onInitialized();
                        }
                    }
                    if (deleted_notifications.get() != total_notifications) {
                        listener.onError(new Exception("Failed to retrieve all notifications"));
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #deleteAllNotification(User, InitializationListener)} with no listener
     */
    private void deleteAllNotification(User user) {
        deleteAllNotification(user, () -> {});
    }

    /*
     *  Functionality for managing users within events
     */

    /**
     * This function is called whenever an event is created. Sets this user as the organizer
     * @param event an event object that's being organized
     * @param listener Optional InitializationListener listener that is called after the event is organized
     */
    private void organizeEvent(Event event, InitializationListener listener) {
        // Set waitlist to an empty array of user references
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventID", event.getId());
        map.put("userID", deviceID);
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
        map.put("userID", deviceID);
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
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", deviceID).get()
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
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", deviceID).get()
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
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", deviceID).get()
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
        userEventsCollection.whereEqualTo("userID", deviceID).whereEqualTo("status", status).get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        listener.onEventListRetrievalCompleted(new ArrayList<>());
                        return;
                    }

                    ArrayList<Event> events = new ArrayList<>(); // Get the array of events the user is waitlisted in
                    int totalEvents = task.size(); // Total events to retrieve
                    AtomicInteger retrievedEvents = new AtomicInteger(); // Counter for retrieved events

                    for (DocumentSnapshot document : task.getDocuments()) {

                        // Get the event ID to pull from the event collection
                        String eventID = Objects.requireNonNull(document.get("eventID")).toString();

                        // Get the event from the event collection
                        eventCollection.document(eventID).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot == null) {
                                        listener.onError(new Exception("Event does not exist"));
                                        return;
                                    }

                                    // Create the new event object and store it in the array
                                    Event event = new Event(Objects.requireNonNull(documentSnapshot.getData()));
                                    events.add(event);

                                    // Increment and check if we have retrieved all events
                                    if (retrievedEvents.incrementAndGet() == totalEvents){
                                        listener.onEventListRetrievalCompleted(events);
                                    }
                                })
                                .addOnFailureListener(listener::onError);
                    }
                    if (retrievedEvents.get() != totalEvents){
                        listener.onError(new Exception("Not all events were retrieved"));
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Retrieves an event using its QR code.
     * While we should be retrieving by {@link #getEvent(String eventID, EventRetrievalListener listener)}
     * I've re-introduced this function for the current implementation of the QR code scanner
     * @param qrCode The Base64 encoded QR code string to match.
     * @param listener An EventRetrievalListener that returns the Event object if found.
     */

     public void getEventByQRCode(String qrCode, EventRetrievalListener listener) {
         eventCollection.whereEqualTo("QrCode", qrCode).get()
                 .addOnSuccessListener(task -> {
                     // Check if any documents were found
                     if (task.getDocuments().isEmpty()) {
                         // No event found, trigger callback with null
                         listener.onEventRetrievalCompleted(null);
                     } else {
                         // Fetch the event details from the document
                         DocumentSnapshot document = task.getDocuments().get(0);
                         Event event = new Event(Objects.requireNonNull(document.getData()));
                         listener.onEventRetrievalCompleted(event);
                     }
                 })
                 .addOnFailureListener(listener::onError);
     }

    /**
     * Get all events the user has organized
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

    /*
     *  Functionality for managing profile and poster images
     */

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
     * @param profilePhotoUrl the string path of the profile photo Url to delete
     * @param listener Optional InitializationListener listener that is called when the image is deleted
     */
    public void deleteProfilePicture(String profilePhotoUrl, InitializationListener listener) {
        if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
            profilePictureReference.child(profilePhotoUrl).delete()
                    .addOnSuccessListener(aVoid -> listener.onInitialized())
                    .addOnFailureListener(listener::onError);
        } else {
            // Throw an exception if the profile picture does not exist
            listener.onError(new Exception("No profile picture to delete"));
        }
    }

    /**
     * Overload of the {@link #deleteProfilePicture(String profilePhotoUrl, InitializationListener)} with no listener
     */
    public void deleteProfilePicture(String profilePhotoUrl) {
        deleteProfilePicture(profilePhotoUrl, () -> {});
    }

    /**
     * Overload of the {@link #deleteProfilePicture(String profilePhotoUrl, InitializationListener)} Using the user object
     */
    public void deleteProfilePicture(User user, InitializationListener listener) {
        deleteProfilePicture(user.getProfilePhotoUrl(), listener);
    }

    /**
     * Overload of the {@link #deleteProfilePicture(String profilePhotoUrl, InitializationListener)} Using the user object and no listener
     */
    public void deleteProfilePicture(User user) {
        deleteProfilePicture(user.getProfilePhotoUrl(), () -> {});
    }


    /**
     * A private function that stores the banner picture in the storage.
     * @param event the event object to store the banner picture in
     * @param image the image to be stored in Uri format
     * @param listener Optional listener that is called when the image is stored. Returns the image url as a string
     */
    private void uploadPosterPicture(Event event, Uri image, DataRetrievalListener listener) {
        posterPictureReference.putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    String imagePath = Objects.requireNonNull(taskSnapshot.getMetadata()).getPath(); // Get the path of the uploaded image
                    event.setPosterPhotoUrl(imagePath); // Set the profile photo field in the user object
                    listener.onRetrievalCompleted(imagePath);   // Pass the image reference to the listener

                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #uploadPosterPicture(Event, Uri, DataRetrievalListener)} with no listener
     */
    private void uploadPosterPicture(Event event, Uri image) {
        uploadPosterPicture(event, image, id -> {});
    }

    /**
     * Stores the poster picture in the storage. Listener is required
     * This function automatically handles the updating of the poster picture in the event object
     * @param event the event object to store the poster picture in
     * @param image the image to be stored in Uri format
     * @param listener the listener that is called when the image is stored. Returns the image url as a string
     */
    public void setPosterPicture(Event event, Uri image, DataRetrievalListener listener) {
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
    public void setPosterPicture(Event event, Uri image) {
        setPosterPicture(event, image, id -> {});
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
     * @param photoUrl the string path of the poster photo Url to delete
     * @param listener Optional InitializationListener listener that is called when the image is deleted
     */
    public void deletePosterPicture(String photoUrl, InitializationListener listener) {
        // Get the photo Url and delete it
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
     * Overload of the {@link #deletePosterPicture(String, InitializationListener)} with no listener
     */
    public void deletePosterPicture(String photoUrl) {
        deletePosterPicture(photoUrl, () -> {});
    }

    /**
     * Overload of the {@link #deletePosterPicture(String, InitializationListener)} Using the event object
     */
    public void deletePosterPicture(Event event, InitializationListener listener) {
        deletePosterPicture(event.getPosterPhotoUrl(), listener);
    }

    /**
     * Overload of the {@link #deletePosterPicture(String, InitializationListener)} Using the event object and no listener
     */
    public void deletePosterPicture(Event event) {
        deletePosterPicture(event.getPosterPhotoUrl(), () -> {});
    }

    /**
     * Checks if the image is a profile picture.
     * @param imageName the image name to check
     * @param listener the listener that is called when the check is complete. Returns true if the image is a profile picture
     *                 false if poster image,
     *                 This function should never have to return null as either it exist in one or the other.
     */
    public void isProfilePicture(String imageName, CheckListener listener) {
        // Check if the image exists in profilePictureReference
        System.out.println("imageName: " + imageName);


        profilePictureReference.child(imageName).getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onCheckComplete(true)) // Profile picture
                .addOnFailureListener(e -> {
                    if (e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        // Image not found in profilePictureReference, check posterPictureReference
                        posterPictureReference.child(imageName).getDownloadUrl()
                                .addOnSuccessListener(uri -> listener.onCheckComplete(false)) // Poster picture
                                .addOnFailureListener(listener::onError);
                    } else {
                        listener.onError(e); // Error checking profilePictureReference
                    }
                });
    }

    /**
     * Overload of the {@link #isProfilePicture(String, CheckListener)} with the imageUri instead of the image name
     */
    public void isProfilePicture(Uri imageUrl, CheckListener listener) {
        isProfilePicture(imageUrl.getLastPathSegment(), listener);
    }

    /*
     *  Functionality for managing admin functionality
     */

    /**
     * Checks if this user is an admin
     * @param listener the listener that is called when the check is complete. Returns true if the user is an admin
     */
    public void isThisUserAdmin(CheckListener listener){
        adminCollection.whereEqualTo("deviceID", deviceID).get()
                .addOnSuccessListener(result -> {
                    // If the result is not empty, the user is an admin
                    boolean exist = !result.isEmpty();
                    listener.onCheckComplete(exist);
                });
    }

    /**
     * Handles the processing of multiple search tasks and ensures all data is retrieved before calling the listener
     * @param tasks A list of document snapshots to be processed in the calling function
     * @param listener QueryRetrievalListener listener that returns an ArrayList of document snapshots.
     * Document snapshots should be processed in the calling function
     */
    private void waitForQueryCompletion(ArrayList<Task> tasks, QueryRetrievalListener listener) {
        // wait for all tasks to complete
        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener( queries -> {
                    ArrayList<DocumentSnapshot> results = new ArrayList<>();
                    for (Object query : queries) {
                        // Check what the query is and add the object to the results array
                        if (query instanceof QuerySnapshot) {
                            results.addAll(((QuerySnapshot) query).getDocuments());
                        } else if (query instanceof DocumentSnapshot) {
                            results.add(((DocumentSnapshot) query));
                        }
                    }
                    listener.onQueryRetrievalCompleted(results);
                })
                .addOnFailureListener(listener::onError);
    }

    public void searchForEvent(String query, EventListRetrievalListener listener) {
        // Search by event name and description
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventCollection.whereGreaterThanOrEqualTo("name", query).whereLessThanOrEqualTo("name", query + "\uf8ff").get());
        tasks.add(eventCollection.whereGreaterThanOrEqualTo("description", query).whereLessThanOrEqualTo("description", query + "\uf8ff").get());

        waitForQueryCompletion(tasks, (results) -> {
            // Create new array of event objects
            ArrayList<Event> events = new ArrayList<>();
            for (DocumentSnapshot document : results) {
                Event event = new Event(Objects.requireNonNull(document.getData()));
                events.add(event);
            }
            listener.onEventListRetrievalCompleted(events);
        });
    }

    /**
     * Searches for users in the database, returns an array of users
     * @param query the query to search for
     * @param listener the listener that is called when the search is complete. Returns an ArrayList of users
     */
    public void searchForUsers(String query, UserListRetrievalListener listener) {
        // Search by name, lastname, and email
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(userCollection.whereGreaterThanOrEqualTo("name", query).whereLessThanOrEqualTo("name", query + "\uf8ff").get());
        tasks.add(userCollection.whereGreaterThanOrEqualTo("lastname", query).whereLessThanOrEqualTo("lastname", query + "\uf8ff").get());
        tasks.add(userCollection.whereGreaterThanOrEqualTo("email", query).whereLessThanOrEqualTo("email", query + "\uf8ff").get());


        waitForQueryCompletion(tasks, (results) -> {
            // Create new array of event objects
            ArrayList<User> users = new ArrayList<>();
            for (DocumentSnapshot document : results) {
                User user = new User(Objects.requireNonNull(document.getData()));
                users.add(user);
            }
            listener.onUserListRetrievalCompleted(users);
        });
    }

    /**
     * Searches for facilities in the database, returns an array of facilities (strings)
     * TODO Currently doesn't return anything, an issue on the firebase end
     * @param query the query to search for
     * @param listener the listener that is called when the search is complete. Returns an ArrayList of facilities
     */
    public void searchForFacilities(String query, DataListRetrievalListener listener) {
        // Search by name, lastname, and email
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(userCollection.whereGreaterThanOrEqualTo("facility", query).whereLessThanOrEqualTo("facility", query + "\uf8ff").get());

        waitForQueryCompletion(tasks, (results) -> {
            // Create new array of event objects
            ArrayList<String> facilities = new ArrayList<>();
            for (DocumentSnapshot document : results) {
                System.out.println(document.get("facility"));
                facilities.add(Objects.requireNonNull(document.get("facility")).toString());
            }
            listener.onListRetrievalCompleted(facilities);
        });
    }

    /**
     * Gets all images from the profile and poster picture storages
     * @param listener the listener that is called when the search is complete. Returns an ArrayList of images
     */
    public void getAllImages(ImageListRetrievalListener listener) {
        ArrayList<Uri> images = new ArrayList<>();
        List<Task<ListResult>> tasks = new ArrayList<>();

        tasks.add(profilePictureReference.listAll());
        tasks.add(posterPictureReference.listAll());

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(results -> {
                    List<Task<Uri>> downloadUrlTasks = new ArrayList<>();
                    for (Object result : results) {
                        for (StorageReference ref : ((ListResult) result).getItems()) {
                            downloadUrlTasks.add(ref.getDownloadUrl());
                        }
                    }

                    Tasks.whenAllSuccess(downloadUrlTasks)
                            .addOnSuccessListener(urls -> {
                                for (Object url : urls)
                                    images.add((Uri) url);

                                listener.onImageListRetrievalCompleted(images);
                            })
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }
}

