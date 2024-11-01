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
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Get the user document TODO functions could call, getting userDocument==null before this finishes execution
        checkOrCreateUser();

    }

    /**
     * Check if the user document exists. If not, create a new one
     */
    private void checkOrCreateUser() {
        userCollection.document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userDocument = documentSnapshot;
                    } else {
                        addThisUserDocument();
                    }
                });
    }


    /**
     * Create a new user document in the firestore
     */
    private void addThisUserDocument() {
        Map<String, Object> user = new HashMap<>();
        user.put("deviceID", this.deviceID);
        // TODO Add other initial user data as needed
        user.put("email", null);
        user.put("facility", null);
        user.put("realName", null);
        user.put("phone", null);
        user.put("enrolled", null);
        user.put("waitlist", null);
        user.put("notification", null);

        userCollection.document(this.deviceID)
                .set(user)
                .addOnSuccessListener(documentSnapshot -> {
                    // The document now exist, call again to retrieve the document
                    checkOrCreateUser();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding new user document", e));
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
     * Interface to check if the user is an admin. Return value is the boolean if yes or no
     */
    public interface AdminCheckListener {
        void onAdminCheckComplete(boolean isAdmin);
    }


    /**
     * Get the current user associated with the device id
     * @param listener the user defined listener to be called when the document is retrieved
     */
    public void getThisUser(OnDocumentRetrievedListener listener) {
        userCollection
                .whereEqualTo("deviceID", this.deviceID).get() // Filter by device id
                .addOnSuccessListener(documentSnapshots ->{
                    // Retrieve the user document from query of user collection
                    userDocument = documentSnapshots.getDocuments().get(0);

                    // Check
                    if (userDocument == null) {
                        // Document not found. Throw an exception
                        listener.onError(new Exception("No such user document"));
                        return;
                    }

                    listener.onDocumentRetrieved(userDocument);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Get the waitlist of the event associated with a user
     * @param listener the user defined listener to be called when the document is retrieved
     */
    public void getWaitlist(OnDocumentListRetrievedListener listener) {

        userInEventCollection
                .whereEqualTo("user", userDocument.getReference()).get()
                .addOnSuccessListener(documentSnapshots -> {
                    // Retrieve the user document from query of user collection
                    DocumentSnapshot eventDocument = documentSnapshots.getDocuments().get(0);

                    // List of events references from waitlist TODO is this right?
                    List<DocumentReference> eventList = (List<DocumentReference>) eventDocument.get("waitlist");

                    // Make an array of event documentSnapshots
                    List<DocumentSnapshot> eventDocs = new ArrayList<>();
                    for (DocumentReference eventRef : eventList) {
                        eventRef.get()
                                .addOnSuccessListener(eventDocs::add)
                                .addOnFailureListener(listener::onError);
                    }

                    // Then Call the success listener
                    listener.onDocumentsRetrieved(eventDocs);

                })
                .addOnFailureListener(listener::onError);
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

