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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
    private CountDownLatch userDocumentLatch;

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

        // Get the user document
        userDocumentLatch = new CountDownLatch(1);   // Used to await the user document
        checkOrCreateUser(newDocument -> userDocument = newDocument);

        //waitForUserDocument();
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
     * implemented in the constructor to retrieve the user document
     */
    public interface UserInitializationListener {
        void onUserInitialized(DocumentSnapshot userDocument);
    }

    /**
     * Interface to check if the user is an admin. Return value is the boolean if yes or no
     */
    public interface AdminCheckListener {
        void onAdminCheckComplete(boolean isAdmin);
    }

    /**
     * This function waits for the user document to be initialized
     * Uses the CountDownLatch to wait for the user document to be initialized
     */
    private void waitForUserDocument() {
        try {
            userDocumentLatch.await(); // Wait for initialization
        } catch (InterruptedException e) {
            Log.e("Firebase", "Error waiting for user document initialization", e);
        }
    }

    /**
     * Check if the user document exists. If not, create a new one
     * This function is only called in the constructor. Used getThisUser to get the user document
     */
    private void checkOrCreateUser(UserInitializationListener listener) {
        if (userDocument != null) {
            listener.onUserInitialized(userDocument);
        } else {

        userCollection.document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userDocumentLatch.countDown();
                        listener.onUserInitialized(documentSnapshot);
                    } else {
                        addThisUserDocument(listener);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting user document", e));
        }

    }

    /**
     * Create a new user document in the firestore
     */
    private void addThisUserDocument(UserInitializationListener listener) {
        Map<String, Object> user = new HashMap<>();
        user.put("deviceID", this.deviceID);
        // TODO Add other initial user data as needed
        user.put("name", "a name");
        user.put("email", "fake@aemail.com");
        user.put("facility", "");
        user.put("phone", "7809993333");

        List<Event> emptyArray = new ArrayList<>();
        user.put("enrolled", emptyArray);
        user.put("waitlist", emptyArray);

        userCollection.document(this.deviceID)
                .set(user)
                .addOnSuccessListener(success -> {
                    // Re-get the user document again, now that the document was added
                    addThisUserDocument(listener);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding new user document", e));
    }


    /**
     * Get the current user associated with the device id
     * @param listener the user defined listener to be called when the document is retrieved
     */
    public void getThisUser(OnDocumentRetrievedListener listener) {
        // Call checkOrCreateUser first to ensure the userDocument exist
        checkOrCreateUser(listener::onDocumentRetrieved);
    }

    /**
     * Get the waitlist of the event associated with a user
     * @param listener the user defined listener to be called when the document is retrieved
     */
    public void getWaitlist(OnDocumentListRetrievedListener listener) {
        checkOrCreateUser(userDocument -> {
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
//                    if (eventList == null || eventList.isEmpty()) {
//                        // Handle case where waitlist is empty or not found
//                        listener.onDocumentsRetrieved(new ArrayList<>()); // Or handle error
//                        return;
//                    }

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
    public void isAdmin(AdminCheckListener listener) {
        adminCollection
                .whereEqualTo("deviceID", this.deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isAdmin = !task.getResult().isEmpty(); // Check if any documents were found
                        listener.onAdminCheckComplete(isAdmin);
                    } else {
                        // Handle error (e.g., call listener with false or throw an exception)
                        listener.onAdminCheckComplete(false); // Assuming error means not admin
                    }
                });
    }
}

