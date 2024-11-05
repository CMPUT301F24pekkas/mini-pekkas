package com.example.mini_pekkas;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

/**
 * This class accesses the firestore and contains functions to return important information
 */
public class Firebase {
    private final FirebaseFirestore db;
    private final String deviceID;
    private final CollectionReference userCollection;
    private final CollectionReference eventCollection;
    private final CollectionReference userEventsCollection;
    private final CollectionReference adminCollection;

    private DocumentSnapshot userDocument;

    /**
     * Constructor to access the firestore in db
     * @param context
     * the context of the activity the function is called from
     */
    @SuppressLint("HardwareIds") // We just need the device id as a string
    public Firebase(Context context) {
        // Initialize the database
        this.db = FirebaseFirestore.getInstance();
        // Initialize the collection references
        userCollection = db.collection("users");
        eventCollection = db.collection("events");
        userEventsCollection = db.collection("user-events");
        adminCollection = db.collection("admins");

        // Get the device id
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch the user document
        fetchUserDocument(() -> {Log.d(TAG, "User document fetched");});
    }


    /**
     * all document request must implement this interface if it retrieves a single document
     * onDocumentRetrieved is called when the document is retrieved successfully. Data should be collected here
     * onError handles any errors that occur. Default behaviour logs the error to console
     */
    public interface OnDocumentRetrievedListener {
        void onDocumentRetrieved(DocumentSnapshot documentSnapshot);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting document: ", e);
        }
    }

    /**
     * all document request must implement this interface if it retrieves multiple document
     * onDocumentsRetrieved is called when all documents are retrieved successfully. Data should be collected here
     * onError handles any errors that occur. Default behaviour logs the error to console
     */
    public interface OnDocumentListRetrievedListener {
        void onDocumentsRetrieved(List<DocumentSnapshot> documentSnapshots);
        default void onError(Exception e) {
            Log.e(TAG, "Error getting documents: ", e);
        }
    }


    /**
     * Interface for functions that initialize data. Ensure that the data is initialized successfully before calling the listener
     */
    public interface InitializationListener {
        void onInitialized();
        default void onError(Exception e) {
            Log.e(TAG, "Error initializing data: ", e);
        }
    }

    /**
     * Interface for functions that need to retrieve the document ID. Ensure that the data is initialized successfully before calling the listener
     */
    public interface IDRetrievalListener {
        void onRetrievalCompleted(String id);
        default void onError(Exception e) {
            Log.e(TAG, "Error initializing data: ", e);
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
     * Private function to fetch the user document. Serves as an updater that any getter functions should call
     * @param listener a void listener that runs on a successful fetch
     */
    public void fetchUserDocument(InitializationListener listener) {
        userCollection.document(this.deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    userDocument = documentSnapshot;
                    listener.onInitialized();
                })
                .addOnFailureListener(e ->Log.e(TAG, "Error getting user document", e));
    }

    /**
     * Checks if the user document exists in the firestore. Updates the local copy and calls the listener
     * @param listener a listener that is called when the check is complete. Returns true if the document exists
     */
    public void checkThisUserExist(CheckListener listener) {
        if (userDocument != null) {
            listener.onCheckComplete(true);
        } else {
            userCollection.document(deviceID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                            // Cache userDocument for later use access
                            boolean exist = documentSnapshot.exists();

                            // Set UserDocument to the document if it exists
                            if (exist) {
                                userDocument = documentSnapshot;
                            }

                            // Call the listener, return true if the document exists
                            listener.onCheckComplete(exist);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error checking if user exists", e));
        }
    }

    /**
     * Initializes a new user in Firebase
     * @param user A user object to be initialized
     * @param listener A listener that is called after the user is initialized
     */
    public void InitializeThisUser(User user, InitializationListener listener) {
        userCollection.document(this.deviceID)
                .set(user.toMap())
                .addOnSuccessListener(v -> {
                    // Re fetch the user document
                    fetchUserDocument(listener);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error adding new user document", e));
    }



    /**
     * Gets and returns a User object stored in UserDocument
     * @return the user document as a User object. Or null if the user document doesn't exist
     */
    public User getThisUser() {
        if (userDocument == null) {
            return null;
        } else {
            return new User(userDocument.getData());
        }
    }


    /**
     * Updates the user document with the new user object
     * @param user a user object with new data to be updated
     */
    public void updateThisUser(User user) {
        db.collection("users").document(this.deviceID)
                .set(user.toMap())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User successfully updated"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating user", e));

        // Then update the user document
        fetchUserDocument(() -> {});
    }


    /**
     * Deletes this user, essentially starting with a clean slate
     * @// TODO: 11/2/24   Also need to delete from user-in-event
     */
    public void deleteThisUser() {
        userCollection.document(this.deviceID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User successfully deleted"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting user", e));
        userDocument = null;
    }

    // TODO add event functions

    /**
     * Delete an event from the events collection
     * @param event an event object to be deleted
     */
    public void deleteEvent(Event event) {
        String eventID = event.getId();
        eventCollection.document(eventID).delete();
    }

    /**
     * Update an event in the events collection
     * @param event an event object to be updated
     */
    public void updateEvent(Event event) {
        String eventID = event.getId();
        eventCollection.document(eventID).set(event.toMap());
    }

    /**
     * Add an event to the events collection
     * @param event an event object to be added
     */
    public void addEvent(Event event, IDRetrievalListener listener) {
        eventCollection.add(event.toMap())
                .addOnSuccessListener(documentReference -> {
                    // Retrieve the ID of the document, pass into user retrieval listener
                    String id = documentReference.getId();
                    event.setId(id);

                    listener.onRetrievalCompleted(id);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding event", e));
    }

    /**
     * Waitlist this user into the wait
     * @param event an event object to be waitlisted into
     */
    private void waitlistEvent(Event event) {
        // Set waitlist to an empty array of user references
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventID", event.getId());
        map.put("userID", this.deviceID);
        map.put("status", "waitlist");

        userEventsCollection.add(map);
    }


    // TODO add admin functions. Allows for deletion of any document
}

