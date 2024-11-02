package com.example.mini_pekkas;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * This class accesses the firestore and contains functions to return important information
 */
public class Firebase {
    private final FirebaseFirestore db;
    private final String deviceID;
    private final CollectionReference userCollection;
    private final CollectionReference eventCollection;
    private final CollectionReference userInEventCollection;
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
        userInEventCollection = db.collection("users-in-events");
        adminCollection = db.collection("admins");

        // Get the device id
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get the user document. If it doesn't exist, set to null
        checkThisUserExist();
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
     * Interface for functions that check a conditional. Returns true if the conditional is met
     */
    public interface CheckListener {
        void onCheckComplete(boolean exist);
        default void onError(Exception e) {
            Log.e(TAG, "Error checking conditional: ", e);
        }
    }


    /**
     * Checks if the user document exists in the firestore. Updates the local copy and calls the listener
     * @param listener a listener that is called when the check is complete. Returns true if the document exists
     */
    private void checkThisUserExist(CheckListener listener) {
        if (userDocument != null) {
            listener.onCheckComplete(true);
        } else {
            userCollection.document(deviceID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                            // Cache userDocument for later use access
                            Boolean exist = documentSnapshot.exists();

                            // Set UserDocument to the document if it exists
                            if (exist) {
                                userDocument = documentSnapshot;
                            }

                            // Call the listener, return true if the document exists
                            listener.onCheckComplete(exist);
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error checking if user exists", e));
        }
    }

    /**
     * Initializes a new user in Firebase
     * @param user A user object to be initialized
     * @param listener A listener that is called after the user is initialized
     */
    private void InitializeThisUser(User user, InitializationListener listener) {
        userCollection.document(this.deviceID)
                .set(user.toMap())
                .addOnSuccessListener(v -> {
                    userDocument = userCollection.document(this.deviceID).get().getResult();
                    listener.onInitialized();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding new user document", e));
    }

    /**
     * Private function to fetch the user document. This servers as an updater that any getter functions can call
     * @param listener a void listener that runs on a successful fetch
     */
    private void fetchUserDocument(InitializationListener listener) {
        if (userDocument == null) {
            userCollection.document(this.deviceID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        userDocument = documentSnapshot;
                        listener.onInitialized();
                    });
        } else {
            // Document already exists, call the listener
            listener.onInitialized();
        }
    }

    /**
     * Gets and returns a User object stored in UserDocument
     * @return the user document as a User object. Or null if the user document doesn't exist
     */
    private User getThisUser() {
        if (userDocument == null) {
            return null;
        } else {
            return new User(Objects.requireNonNull(userDocument.getData()));
        }
    }

    /**
     * Get the waitlist of the event associated with a user
     * @param listener the user defined listener to be called when the document is retrieved
     */
    public void getWaitlist(OnDocumentListRetrievedListener listener) {
        checkThisUserExist(exist -> {
            if (!exist) {
                listener.onDocumentsRetrieved(new ArrayList<>()); // Return an empty array if user doesn't exist
                Log.d(TAG, "User doesn't exist");
            } else {
                userInEventCollection
                        .whereEqualTo("user", userDocument.getReference()).get()
                        .addOnSuccessListener(documentSnapshots -> {
                            if (documentSnapshots.isEmpty()) {
                                listener.onDocumentsRetrieved(new ArrayList<>()); // Return an empty array if no documents found
                                return;
                            }

                            // Else Retrieve the user document from query of user collection
                            DocumentSnapshot eventDocument = documentSnapshots.getDocuments().get(0);

                            // List of events references from waitlist
                            List<DocumentReference> eventList = (List<DocumentReference>) eventDocument.get("waitlist");

                            // If the waitlist is empty or not found, return an empty list
                            if (eventList == null || eventList.isEmpty()) {
                                // Handle case where waitlist is empty or not found
                                listener.onDocumentsRetrieved(new ArrayList<>()); // Or handle error
                                return;
                            }

                            // Counting pending gets. Used to check if we get every element
                            final int[] pendingGets = {eventList.size()}; // Counter for pending gets

                            // Make an array of event documentSnapshots
                            List<DocumentSnapshot> eventDocs = new ArrayList<>();
                            for (DocumentReference eventRef : eventList) {
                                eventRef.get()
                                        .addOnSuccessListener(eventSnapshot -> {
                                            eventDocs.add(eventSnapshot);

                                            // Checks if every document has been retrieved
                                            if (--pendingGets[0] == 0) {
                                                listener.onDocumentsRetrieved(eventDocs);
                                            }
                                        })
                                        .addOnFailureListener(listener::onError);
                            }
                        })
                        .addOnFailureListener(listener::onError);

                    }

            });
    }


//    this is all newly integrated firebase stuff, keep and leave what you think is good -daniel

    public void editUser(User user) {
        db.collection("users").document(this.deviceID)
                .set(user.toMap())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User successfully updated"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating user", e));
    }
    public void addUser(User user) {
        db.collection("users").document(this.deviceID)
                .set(user.toMap())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User successfully added"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user", e));
    }
    public void deleteUser(User user) {
        db.collection("users").document(this.deviceID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User successfully deleted"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting user", e));
    }


    /**
     * Retrieve the admin list
     * @param listener the user defined admin listener which sends true or false depending on if the user is an admin
     */
    public void isAdmin(CheckListener listener) {
        adminCollection
                .whereEqualTo("deviceID", this.deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isAdmin = !task.getResult().isEmpty(); // Check if any documents were found
                        listener.onCheckComplete(isAdmin);
                    } else {
                        // Handle error (e.g., call listener with false or throw an exception)
                        listener.onCheckComplete(false); // Assuming error means not admin
                    }
                });
    }
}

