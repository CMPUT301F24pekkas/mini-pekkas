package com.example.mini_pekkas;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.example.mini_pekkas.notification.Notifications;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used to interface with Firebase and should be used for all Firebase operations.
 * Any functions that get and request data needs a user defined listener. This is a function that's called after an operation is completed.
 * Every listener will have a on success and an optional on error listener (if not overwritten, the default error handling is to print the error in the log)
 * @author ryan
 * @version 1.17.3 didAcceptEvent now takes in multiple notfications
 */
public class Firebase {
    private final String deviceID;
    private final CollectionReference userCollection;
    private final CollectionReference eventCollection;
    private final CollectionReference userEventsCollection;
    private final CollectionReference userNotificationsCollection;
    private final CollectionReference notificationCollection;
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
        notificationCollection = db.collection("notifications");
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
     * Interface for functions that retrieve an array of facilities
     */
    public interface FacilityListRetrievalListener {
        void onFacilityListRetrievalCompleted(ArrayList<Facility> facilities);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting facilities: ", e);
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
     * Interface for functions that return list of locations in GeoPoint format
     */
    public interface GeoPointListRetrievalListener {
        void onGeoPointListRetrievalCompleted(ArrayList<GeoPoint> locations);
        default void onError(Exception e) {Log.e(TAG, "Error getting locations: ", e);}
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
        user.setUserID(deviceID);
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
        //        return userDocument.toObject(User.class);
    }

    /**
     * Updates the user document with the new user object
     * @param user a user object with new data to be updated
     * @param listener Optional InitializationListener listener that is called after the user is updated
     */
    public void updateThisUser(User user, InitializationListener listener) {
        userCollection.whereEqualTo("userID", user.getUserID()).get()
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
        try {
            deleteProfilePicture(user);
            deleteAllNotification(user);
        } catch (Exception e) {
            listener.onError(e);
        }
        // Delete all documents from the user-events collection
        userEventsCollection.whereEqualTo("userID", deviceID).get()
                .addOnSuccessListener(task -> {
                    // Use an array list to store the documents to be deleted.
                    if (!task.isEmpty()) {
                        // Delete all documents that match the query
                        for (DocumentSnapshot document : task.getDocuments()) {
                            document.getReference().delete()
                                    .addOnFailureListener(listener::onError);
                        }
                    }
                }).addOnFailureListener(listener::onError);

        // After deleting event entries, delete the user itself
        userCollection.whereEqualTo("userID", user.getUserID()).get()
                .addOnSuccessListener(userTask -> {
                    if (userTask.size() != 1) {
                        listener.onError(new Exception("User not found"));
                        return;
                    }
                    // Delete the user document
                    userTask.getDocuments().get(0).getReference().delete()
                            .addOnSuccessListener(bVoid -> {
                                userDocument = null;        // Clear the user document
                                listener.onInitialized();
                            })
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #deleteUser(User, InitializationListener)} with no listener
     */
    public void deleteUser(User user) {
        deleteUser(user, () -> {});
    }
    /**
     * Deletes this user, essentially starting with a clean slate
     * also deletes related documents in user-events collection
     * @param listener Optional InitializationListener listener that is called after the user is deleted
     */
    public void deleteThisUser(InitializationListener listener) {
        deleteUser(getThisUser(), listener);
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
                    // Use an array list to store the documents to be deleted.
                    ArrayList<Task> tasks = new ArrayList<>();

                    for (DocumentSnapshot document : task.getDocuments()) {
                        tasks.add(document.getReference().delete());
                    }
                    // Wait for all documents to be deleted first
                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(aVoid -> listener.onInitialized())
                            .addOnFailureListener(listener::onError);
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
                        Event event = documentSnapshot.toObject(Event.class);
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
                    ArrayList<Task> tasks = new ArrayList<>();

                    for (DocumentSnapshot document : task.getDocuments()) {
                        tasks.add(notificationCollection.whereEqualTo("id", document.get("notificationID")).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (!documentSnapshot.isEmpty()) {
                                        // Retrieve the notification
                                        Notifications notif = documentSnapshot.getDocuments().get(0).toObject(Notifications.class);
                                        if (notif != null) notifications.add(notif);
                                    }
                                }));
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(aVoid -> {
                                notifications.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));   // Sort the notifications by newest date
                                listener.onNotificationListRetrievalCompleted(notifications);
                            })
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Add a notification to the notifications collection.
     * This collection is a storage for all notifications.
     * @param notification a notification object to be added
     * @param listener Optional DataRetrievalListener listener that is called after the notification is added
     */
    private void storeNotification(Notifications notification, DataRetrievalListener listener) {
        // Check if the notification already exist
        notificationCollection.whereEqualTo("title", notification.getTitle()).whereEqualTo("description", notification.getDescription())
                .whereEqualTo("date", notification.getTimestamp())  //.whereEqualTo("priority", notification.getPriority()) TODO this fails the check
                .whereEqualTo("fragmentDestination", notification.getFragmentDestination()).get()
                        .addOnSuccessListener(task -> {
                            // This notification already exist
                            if(!task.isEmpty()) {
                                listener.onRetrievalCompleted(Objects.requireNonNull(task.getDocuments().get(0).getId()));
                            } else {
                                // Create a new notification
                                notificationCollection.add(notification.toMap())
                                        .addOnSuccessListener(documentReference -> {
                                            // Retrieve the ID of the document and update the notification object
                                            String id = documentReference.getId();
                                            notification.setID(id);

                                            // Keep the id copy in firestore for querying
                                            documentReference.update("id", id);

                                            // Finally call the listener on completion
                                            listener.onRetrievalCompleted(id);
                                        })
                                        .addOnFailureListener(listener::onError);
                            }
                        }).addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #storeNotification(Notifications, DataRetrievalListener)} with no listener
     */
    private void storeNotification(Notifications notification) {
        storeNotification(notification, id -> {});
    }

    /**
     * Add a notification to the user-notifications collection, effectively sending a notification to a user
     * @param notification a notification object to be added
     * @param listener Optional InitializationListener listener that is called after the notification is added
     */
    private void sendNotification(Notifications notification, String userID, InitializationListener listener) {
        // First store the notification
        storeNotification(notification, id -> {
            // Create the user-notification entry
            HashMap<String, Object> map = new HashMap<>();
            map.put("notificationID", id);
            map.put("userID", userID);
            map.put("read", false);

            // Call the listener on successful add
            userNotificationsCollection.add(map)
                    .addOnSuccessListener(aVoid -> listener.onInitialized())
                    .addOnFailureListener(listener::onError);
        });
    }

    /**
     * Overload of the {@link #sendNotification(Notifications, String, InitializationListener)} with no listener
     */
    private void sendNotification(Notifications notification, String userID) {
        sendNotification(notification, userID, () -> {});
    }

    /**
     * Send a notification to all users in an event with a given status.
     * If status == all, send to all users.
     * @param notification the notification to be sent
     * @param event the event where the notification is to be sent
     * @param status the status to target, if {all}, send to all users {organized}, {pending}, {waitlisted}, {enrolled}
     */
    public void sendEventNotificationByStatus(Notifications notification, Event event, String status, InitializationListener listener) {
        Query query = userEventsCollection.whereEqualTo("eventID", event.getId());
        if (!status.equals("all")) {
            query = query.whereEqualTo("status", status); // Add status filter if not "all"
        }
        query.get()     // Performs the query with the given filter
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        listener.onError(new Exception("No users found"));
                        return;
                    }
                    // Send notification to every user in task
                    for (DocumentSnapshot document : task.getDocuments()) {
                        sendNotification(notification, Objects.requireNonNull(document.get("userID")).toString());
                    }

                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #sendEventNotificationByStatus(Notifications, Event, String, InitializationListener)} with no listener
     */
    public void sendEventNotificationByStatus(Notifications notification, Event event, String status) {
        sendEventNotificationByStatus(notification, event, status, () -> {});
    }

    /**
     * Delete all notifications for the given user
     * This function is used in delete user to remove it's corresponding notifications
     * @param user a user object where the notifications are to be deleted
     * @param listener Optional InitializationListener listener that is called after the notifications are deleted
     */
    private void deleteAllNotification(User user, InitializationListener listener) {
        userNotificationsCollection.whereEqualTo("userID", user.getUserID()).get()
                .addOnSuccessListener(task -> {
                    ArrayList<Task> tasks = new ArrayList<>();

                    // Fetch all notification documents and delete it
                    for (DocumentSnapshot document : task.getDocuments()) {
                        tasks.add(document.getReference().delete());
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(aVoid -> listener.onInitialized())
                            .addOnFailureListener(listener::onError);
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
        map.put("geopoint", null);  // Dont track organizer location

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
     * @param geoPoint Optional location object if the user is located
     * @param listener Optional InitializationListener listener that is called after the event is waitlisted
     */
    public void waitlistEvent(Event event, GeoPoint geoPoint, InitializationListener listener) {
        userEventsCollection
                .whereEqualTo("eventID", event.getId())
                .whereEqualTo("userID", deviceID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Document exists, update the status to "waitlisted"
                        DocumentSnapshot existingDocument = querySnapshot.getDocuments().get(0);
                        userEventsCollection.document(existingDocument.getId())
                                .update("status", "waitlisted")
                                .addOnSuccessListener(aVoid -> {
                                    if (listener != null) {
                                        listener.onInitialized();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (listener != null) {
                                        listener.onError(e);
                                    }
                                });
                    } else {    // Document does not exist, create a new one
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("eventID", event.getId());
                        map.put("userID", deviceID);
                        map.put("status", "waitlisted");
                        map.put("geopoint", geoPoint);

                        userEventsCollection.add(map)
                                .addOnSuccessListener(aVoid -> listener.onInitialized())
                                .addOnFailureListener(listener::onError);
                    }
                });
    }

    /**
     * Overload of the {@link #waitlistEvent(Event, GeoPoint, InitializationListener)} with no location
     */
    public void waitlistEvent(Event event, InitializationListener listener) {
        waitlistEvent(event, null, listener);
    }

    /**
     * Overload of the {@link #waitlistEvent(Event, GeoPoint, InitializationListener)} with no listener
     */
    public void waitlistEvent(Event event, GeoPoint geoPoint) {
        waitlistEvent(event, geoPoint, () -> {});
    }

    /**
     * Overload of the {@link #waitlistEvent(Event, InitializationListener)} with no location and listener
     */
    public void waitlistEvent(Event event) {
        waitlistEvent(event, null, () -> {});
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
     * Leave the waitlist by deleting the user-event entry. Remove all interaction with event
     * @param event an event object to leave from
     * @param listener Optional InitializationListener listener that is called after the event is waitlisted
     */
    public void leaveEvent(Event event, InitializationListener listener) {
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", deviceID).get()
                .addOnSuccessListener(task -> {
                    // Delete the entry
                    task.getDocuments().get(0).getReference().delete()
                            .addOnSuccessListener(aVoid -> listener.onInitialized())
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Overload of the {@link #leaveEvent(Event, InitializationListener)} with no listener
     * @param event an event object to leave from
     */
    public void leaveEvent(Event event) {
        leaveEvent(event, () -> {});
    }

    /**
     * Get the current status of the user in the event.
     * @param event An event object to get the status of.
     * @param listener A DataRetrievalListener listener that returns the status of the user in the event.
     */
    public void getStatusInEvent(Event event, DataRetrievalListener listener) {
        userEventsCollection.whereEqualTo("eventID", event.getId()).whereEqualTo("userID", deviceID).get()
                .addOnSuccessListener(task -> {
                    // Check if there are any documents in the query result
                    if (!task.getDocuments().isEmpty()) {
                        DocumentSnapshot document = task.getDocuments().get(0);
                        String status = document.getString("status");
                        // Call the listener with the retrieved status or "unknown" if the status is null
                        listener.onRetrievalCompleted(status != null ? status : "unknown");
                    } else {
                        // No documents found; call listener with a default "unknown" status
                        listener.onRetrievalCompleted("unknown");
                    }
                })
                .addOnFailureListener(e -> {
                    // Pass the error to the listener
                    listener.onError(e);
                });
    }


    /**
     * Gets an ArrayList of events this user is in, specified by the status
     * This function is called by getXEvents() where X is the status
     * @param status the status of the events to retrieve. [waitlisted, enrolled, cancelled, organized]
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    private void getEventByStatus(String status, EventListRetrievalListener listener) {
        // Create the query
        Query query = userEventsCollection.whereEqualTo("userID", deviceID);
        if (!status.equals("all")) {
            query = query.whereEqualTo("status", status);
        }
        query.get()     // perform the query
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        listener.onEventListRetrievalCompleted(new ArrayList<>());
                        return;
                    }

                    ArrayList<Event> events = new ArrayList<>(); // Get the array of events the user is waitlisted in
                    ArrayList<Task> tasks = new ArrayList<>();

                    for (DocumentSnapshot document : task.getDocuments()) {

                        // Get the event ID to pull from the event collection
                        String eventID = Objects.requireNonNull(document.get("eventID")).toString();

                        // Get the event from the event collection
                        tasks.add(eventCollection.document(eventID).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot == null) {
                                        listener.onError(new Exception("Event does not exist"));
                                    } else {
                                        // Create the new event object and store it in the array
                                        Event event = documentSnapshot.toObject(Event.class);
                                        events.add(event);
                                    }
                                }));
                    }
                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(aVoid -> listener.onEventListRetrievalCompleted(events))
                            .addOnFailureListener(listener::onError);
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
                         Event event = document.toObject(Event.class);
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
     * Get all events the user is waitlisted in
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    public void getPendingEvents(EventListRetrievalListener listener) {
        getEventByStatus("pending", listener);
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

    /**
     * Get all events the user has interacted with
     * @param listener a EventListRetrievalListener that returns an ArrayList of events
     */
    public void getAllEvents(EventListRetrievalListener listener) {
        getEventByStatus("all", listener);
    }

    /**
     * Set the new status of given user in the given event
     * @param status the new status to set
     * @param userID the user ID to update
     * @param eventID the event ID to update
     */
    private void setNewStatus(String status, String userID, String eventID, InitializationListener listener) {
        userEventsCollection.whereEqualTo("eventID", eventID).whereEqualTo("userID", userID).get()
                .addOnSuccessListener(task -> {
                    // This should be a unique entry
                    if (task.size() != 1) {
                        listener.onError(new Exception("Invalid event"));
                        return;
                    }
                    // Update the document and return
                    task.getDocuments().get(0).getReference().update("status", status)
                            .addOnSuccessListener(aVoid -> listener.onInitialized())
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Retrieve a list of all users in an event with a given status
     * @param status the status to retrieve {organized, waitlisted, pending, enrolled, cancelled, all}. set to all to retrieve all users in the event
     * @param eventID the event ID to retrieve
     * @param listener a UserListRetrievalListener that returns an ArrayList of users
     */
    private void getUsersInEventByStatus(String status, String eventID, UserListRetrievalListener listener) {
        Query query = userEventsCollection.whereEqualTo("eventID", eventID);
        if (!status.equals("all")) {
            query = query.whereEqualTo("status", status);
        }
        query.get().addOnSuccessListener(task -> {
            // Check if no documents were found
            if (task.isEmpty()) {
                listener.onUserListRetrievalCompleted(new ArrayList<>());
                return;
            }

            ArrayList<User> users = new ArrayList<>();  // Get the array of events the user is waitlisted in
            ArrayList<Task> tasks = new ArrayList<>();  // Use an array of Tasks to ensure all users are retrieved

            for (DocumentSnapshot document : task.getDocuments()) {

                // Get the event ID to pull from the event collection
                String userID = Objects.requireNonNull(document.get("userID")).toString();
                // Get the event from the event collection
                tasks.add(userCollection.whereEqualTo("userID", userID).get()
                        .addOnSuccessListener(querySnapshot -> {
                            // This should be unique
                            if (querySnapshot.size() != 1) {
                                listener.onError(new Exception("User does not exist"));
                            } else {
                                // Create the new event object and store it in the array
                                User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                                users.add(user);
                            }
                        }));
            }

            Tasks.whenAllSuccess(tasks)     // Await for all task to succeed first
                    .addOnSuccessListener(aVoid -> listener.onUserListRetrievalCompleted(users))
                    .addOnFailureListener(listener::onError);
        }).addOnFailureListener(listener::onError);
    }

    /**
     * Get all organized users for an event (ie the organizer itself). This shouldn't need to be called
     * @param eventID the event ID to query
     * @param listener a UserListRetrievalListener that returns an ArrayList of users
     */
    public void getOrganizedUsers(String eventID, UserListRetrievalListener listener) {
        getUsersInEventByStatus("organized", eventID, listener);
    }

    /**
     * Get all waitlisted users for an event
     * @param eventID the event ID to query
     * @param listener a UserListRetrievalListener that returns an ArrayList of users
     */
    public void getWaitlistedUsers(String eventID, UserListRetrievalListener listener) {
        getUsersInEventByStatus("waitlisted", eventID, listener);
    }

    /**
     * Get all pending users for an event
     * @param eventID the event ID to query
     * @param listener a UserListRetrievalListener that returns an ArrayList of users
     */
    public void getPendingUsers(String eventID, UserListRetrievalListener listener) {
        getUsersInEventByStatus("pending", eventID, listener);
    }

    /**
     * Get all enrolled users for an event
     * @param eventID the event ID to query
     * @param listener a UserListRetrievalListener that returns an ArrayList of users
     */
    public void getEnrolledUsers(String eventID, UserListRetrievalListener listener) {
        getUsersInEventByStatus("enrolled", eventID, listener);
    }

    /**
     * Get all cancelled users for an event
     * @param eventID the event ID to query
     * @param listener a UserListRetrievalListener that returns an ArrayList of users
     */
    public void getCancelledUsers(String eventID, UserListRetrievalListener listener) {
        getUsersInEventByStatus("cancelled", eventID, listener);
    }

    /**
     * Get all users in an event
     * @param eventID the event ID to query
     * @param listener a UserListRetrievalListener that returns an ArrayList of users
     */
    public void getAllUsers(String eventID, UserListRetrievalListener listener) {
        getUsersInEventByStatus("all", eventID, listener);
    }

    /**
     * Retrieve the location of all users in an event. The GeoPoint contains the lat and long of the user
     * @param eventID the event ID to query
     * @param status  the status to filter the query by. Set to {all} to retrieve all user statuses
     *                see {@link #getUsersInEventByStatus(String, String, UserListRetrievalListener)} for possible values
     * @param listener a GeoPointListRetrievalListener that returns an ArrayList of GeoPoints
     */
    public void getUserLocations(String eventID, String status, GeoPointListRetrievalListener listener) {
        Query query = userEventsCollection.whereEqualTo("eventID", eventID);
        if (!status.equals("all")) {
            query = query.whereEqualTo("status", status);
        }
        // Apply filters and run query
        query.get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        listener.onGeoPointListRetrievalCompleted(new ArrayList<>());
                    } else {
                        ArrayList<GeoPoint> locations = new ArrayList<>();
                        for (DocumentSnapshot document : task.getDocuments()) {
                            if (document.get("geopoint") != null) {     // Ensure there is a geopoint present
                                GeoPoint geoPoint = document.getGeoPoint("geopoint");
                                locations.add(geoPoint);
                            }
                        }
                        listener.onGeoPointListRetrievalCompleted(locations);   // Pass the list of locations
                    }
                }).addOnFailureListener(listener::onError);
    }
    /**
     * Call when this user accepts or rejects an event. Updates the user-event collection
     * @param didAccept true if this user accepted (enrolled), false if user rejected (cancelled)
     * @param eventID the event ID to update
     * @param notifications the notifications to send to either the user accepting, or a new user getting enrolled.
     *                     if didAccept is true, notification[0] is an acceptance message sent to the user
     *                     if didAccept is false, notification[0] is an new selected message sent to another user. notification[1] is a rejection message sent to the current user
     * @param listener Optional InitializationListener listener that is called after the event is updated
     */
    public void userAcceptEvent(boolean didAccept, String eventID, ArrayList<Notifications> notifications, InitializationListener listener) {
        setNewStatus(didAccept ? "enrolled" : "cancelled", deviceID, eventID, listener);
        if(didAccept) {
            sendNotification(notifications.get(0), deviceID);   // Send a notification of success
        } else {
            sendNotification(notifications.get(1), deviceID);   // Send a notification of rejection
            redrawUserInEvent(eventID, notifications.get(0), listener);   // Draw new user on rejection. Notify selection
        }
    }

    /**
     * Overload of the {@link #userAcceptEvent(boolean, String, ArrayList, InitializationListener)} with no listener
     */
    public void userAcceptEvent(boolean didAccept, String eventID, ArrayList<Notifications> notifications) {
        userAcceptEvent(didAccept, eventID, notifications, () -> {});
    }

    /*
     *  Functionality for organizer starting and automating the enrollment process
     */

    /**
     * Call when the organizer starts the enrollment process
     * Sample x number of users, set them to pending, send a notification
     * The view event page should now give the accept button (for all events labeled pending)
     * @param event the event to enroll users in
     * @param notifications a length 2 array of notification. not[1] for users selected, not[2] for users not selected (but may be re-drawed eventually)
     * @param listener Optional InitializationListener listener that is called after the event is updated
     */
    public void startEnrollingEvent(Event event, ArrayList<Notifications> notifications, InitializationListener listener) {
        // Create and store a default notification if none is provided
        long user_cap = event.getMaxAttendees();        // The max number of attendees to draw
        // TODO waitlist size should be stored in the event object, I will calculate it in firebase for now
        userEventsCollection
                .whereEqualTo("eventID", event.getId().trim())
                .whereEqualTo("status", "waitlisted").get()
            .addOnSuccessListener(task -> {
                int waitlist_size = task.size();

                Set<Integer> randomIntegers = new HashSet<>();  // Store unique random integers
                try {
                    sleep(0,100);  // 100 nano s delay to prevent race condition
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // If we have more users than the waitlist size, randomly pick x users to enroll

                if (waitlist_size > user_cap && user_cap != -1) {
                    Random random = new Random();
                    while (randomIntegers.size() < user_cap) { // Keep generating until we have 'numIntegers' unique integers
                        randomIntegers.add(random.nextInt(waitlist_size));
                    }
                } else {
                    // If we have less users than the waitlist size, pick all users
                    for (int i = 0; i < waitlist_size; i++) {
                        randomIntegers.add(i);
                    }
                }
                // Now we sample X users (Or all users if we're under capacity
                List<String> selectedUsersID = new ArrayList<>();
                Iterator<Integer> iterator = randomIntegers.iterator();
                for (int i = 0; i < waitlist_size && (i < user_cap || user_cap == -1); i++) {
                    selectedUsersID.add(Objects.requireNonNull(task.getDocuments().get(iterator.next()).get("userID")).toString());
                }

                // Now enroll everyone
                for (String userID : selectedUsersID) {
                    // Send a success notification to each user
                    sendNotification(notifications.get(0), userID);

                    int total_users = selectedUsersID.size();
                    AtomicInteger pending_users = new AtomicInteger();
                    // Update the status to pending
                    setNewStatus("pending", userID, event.getId(), () -> {
                        if (pending_users.incrementAndGet() == total_users) {
                            // Fetch the rest of the users who were not selected
                            getWaitlistedUsers(event.getId(), users -> {
                                for (User user : users) {
                                    sendNotification(notifications.get(1), user.getUserID(), () -> {});     // Send the not selected notification
                                }

                                listener.onInitialized();
                            });
                        }
                    });
                }
            });
    }

    /**
     * Overload of the {@link #startEnrollingEvent(Event, ArrayList, InitializationListener)} with no listener
     */
    public void startEnrollingEvent(Event event, ArrayList<Notifications> notifications) {
        startEnrollingEvent(event, notifications, () -> {});
    }

    /**
     * This function is called whenever a user cancels an event from {@link #userAcceptEvent(boolean, String, ArrayList, InitializationListener)}
     * Redraw a new user from the pool of events (from waitlist into pending)
     */
    private void redrawUserInEvent(String eventID, Notifications notification, InitializationListener listener) {
        userEventsCollection.whereEqualTo("eventID", eventID).whereEqualTo("status", "waitlisted").limit(1).get()
                .addOnSuccessListener(user -> {
                    if (!user.isEmpty()) {
                        String userID = Objects.requireNonNull(user.getDocuments().get(0).get("userID")).toString();
                        // Send a success notification
                        sendNotification(notification, userID);

                        // Set the status to pending
                        setNewStatus("pending", userID, eventID, listener);
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Should be called when the expiration date of the event is reached. Cancel all pending user. Delete and notified all waitlisted users
     * @param eventID the event ID to cancel
     * @param notification the notification to send to cancelled users. Should describe a message of cancellation/no acceptance
     * @param listener Optional InitializationListener listener that is called after the event is updated
     */
    public void eventExpiredCleanup(String eventID, Notifications notification, InitializationListener listener) {
        getPendingUsers(eventID, users -> {
            for (User user : users) {
                sendNotification(notification, user.getUserID());
                setNewStatus("cancelled", user.getUserID(), eventID, () -> {});
            }
        });
        getWaitlistedUsers(eventID, users -> {
            for (User user : users) {
                sendNotification(notification, user.getUserID());
            }
            // delete the event-user entry from the database
            userEventsCollection.whereEqualTo("eventID", eventID).whereEqualTo("userID", deviceID).whereEqualTo("status", "waitlisted").get()
                    .addOnSuccessListener(task -> {
                        for (DocumentSnapshot document : task.getDocuments()) {
                            document.getReference().delete();
                        }
                        listener.onInitialized();
                    }).addOnFailureListener(listener::onError);
        });
    }

    /**
     * Overload of the {@link #eventExpiredCleanup(String, Notifications, InitializationListener)} with no listener
     */
    public void eventExpiredCleanup(String eventID, Notifications notification) {
        eventExpiredCleanup(eventID, notification, () -> {});
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
        posterPictureReference.child(event.getPosterPhotoUrl()).getDownloadUrl()
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
                Event event = document.toObject(Event.class);
                events.add(event);
            }
            listener.onEventListRetrievalCompleted(events);
        });
    }

    /**
     * Searches for users in the database, returns an array of users
     * TODO need to filter out duplicate results
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
            // Create new unique hashset of users
            HashSet<User> users = new HashSet<>();
            for (DocumentSnapshot document : results) {
                User user = document.toObject(User.class);
                users.add(user);
            }
            // Remove duplicate files
            ArrayList<User> userArray = new ArrayList<>(users);
            listener.onUserListRetrievalCompleted(userArray);
        });
    }

    /**
     * TODO need to replace with facility object
     * Searches for facilities in the database, returns an array of facilities (strings)
     * @param query the query to search for
     * @param listener the listener that is called when the search is complete. Returns an ArrayList of facilities
     */
    public void searchForFacilities(String query, FacilityListRetrievalListener listener) {
        // Search by name, lastname, and email
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(userCollection.whereGreaterThanOrEqualTo("facility", query).whereLessThanOrEqualTo("facility", query + "\uf8ff").get());

        waitForQueryCompletion(tasks, (results) -> {
            // Create new array of event objects
            ArrayList<Facility> facilities = new ArrayList<>();
            for (DocumentSnapshot document : results) {
                // If an attribute doesn't exist, set it to null
                String facilityName = (document.get("facility") == null) ? null : document.getString("facility");
                String description = (document.get("facilityDesc") == null) ? null : document.getString("facilityDesc");
                String facilityPhotoUrl = (document.get("facilityPhotoUrl") == null) ? null : document.getString("facilityPhotoUrl");
                Facility facility = new Facility(facilityName, description, facilityPhotoUrl);
                facilities.add(facility);
            }
            listener.onFacilityListRetrievalCompleted(facilities);
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

    /**
     * Deletes all events associated with a facility
     * Delete the facility of the organizer, demoting to a user
     * @param facility the facility to delete events from
     */
    public void deleteByFacility(Facility facility, InitializationListener listener) {
        // Delete all associated event
        eventCollection.whereEqualTo("facility", facility.getName()).get()
                .addOnSuccessListener(tasks -> {
                    if (!tasks.isEmpty()) {
                        for (DocumentSnapshot document : tasks.getDocuments()) {
                            String eventID = (String) document.get("id");
                            // Get and delete the event
                            getEvent(eventID, event -> {
                                deleteEvent(event);
                            });
                        }
                    }
                }).addOnFailureListener(listener::onError);
        // Ban (Delete) the user (one to one) relationship
        userCollection.whereEqualTo("facility", facility.getName()).get()
                .addOnSuccessListener(user -> {
                    if (!user.isEmpty()) {
                        // user.getDocuments().get(0).getReference().set("facility", null);
                        User userObject = user.getDocuments().get(0).toObject(User.class);
                        deleteUser(userObject, listener);
                    }
                }).addOnFailureListener(listener::onError);
    }

    /**
     * Gets all events from the database
     * @param listener the listener that is called when the search is complete. Returns an ArrayList of events
     */
    public void searchAllEvents(EventListRetrievalListener listener) {
        eventCollection.get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        listener.onEventListRetrievalCompleted(new ArrayList<>());
                        return;
                    }
                    ArrayList<Event> events = new ArrayList<>();
                    int totalEvents = task.getDocuments().size();
                    AtomicInteger retrievedEvents = new AtomicInteger();

                    for (DocumentSnapshot document : task.getDocuments()) {
                        try {
                            // Use toObject if Event class is compatible with Firestore mapping
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                events.add(event);
                            } else {
                                Log.e("Firebase", "Event document is null: " + document.getId());
                            }
                        } catch (Exception e) {
                            Log.e("Firebase", "Error parsing event: " + e.getMessage(), e);
                        }

                        // Increment and check completion
                        if (retrievedEvents.incrementAndGet() == totalEvents) {
                            listener.onEventListRetrievalCompleted(events);
                        }
                    }
                })
                .addOnFailureListener(listener::onError);

    }

}


