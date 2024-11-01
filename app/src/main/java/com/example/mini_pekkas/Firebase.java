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

        userCollection.document(this.deviceID).set(user);
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
     * Get the current user associated with the device id
     * @param listener the user defined listener to be called when the document is retrieved
     */
    public void getUser(OnDocumentRetrievedListener listener) {
        userCollection
                .whereEqualTo("deviceID", this.deviceID) // Filter by device id
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Found the document, store it in the class variable
                            userDocument = document;

                            // Call the document received listener
                            listener.onDocumentRetrieved(userDocument);
                            break; // Exit the loop after finding the document
                        }
                        if (userDocument == null) {
                            // Document not found, create a new document
                            addThisUserDocument();
                        }
                    } else {
                        // Call the error listener
                        listener.onError(task.getException());
                    }
                });
    }

    /**
     * Get the waitlist of the event associated with a user
     * @param listener the user defined listener to be called when the document is retrieved
     */
    public void getWaitlist(OnDocumentListRetrievedListener listener) {
        // First check if the user document exist
        if (userDocument == null) {
            listener.onError(new Exception("No such document"));
            return;
        }

        // Retrieve the document if it does
        userInEventCollection
                .whereEqualTo("user", userDocument.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QueryDocumentSnapshot eventDocument = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Found the document, store it in the class variable
                            eventDocument = document;
                            break; // Exit the loop after finding the document
                        }
                        
                        if (eventDocument != null) {
                            if (eventDocument.exists()) {
                                // Extract the array of event references
                                List<DocumentReference> eventRefs = (List<DocumentReference>) eventDocument.get("waitlist");
                                List<DocumentSnapshot> waitlistEvents = new ArrayList<>();

                                // Then retrieve the event documents
                                for (DocumentReference eventRef : eventRefs) {
                                    eventRef.get().addOnCompleteListener(eventTask -> {
                                        if (eventTask.isSuccessful()) {
                                            DocumentSnapshot eventDoc = eventTask.getResult();
                                            if (eventDoc.exists()) {
                                                // Add eventDoc to list of result
                                                waitlistEvents.add(eventDoc);
                                            } else {
                                                // Handle case where event document doesn't exist
                                                listener.onError(new Exception("No such document"));
                                            }
                                        } else {
                                            // Handle error getting event document
                                            listener.onError(task.getException());
                                        }
                                    });
                                }
                                // After getting all event documents, call the document get listener
                                listener.onDocumentsRetrieved(waitlistEvents);

                            }
                        } else {
                            // Document not found, throw an exception
                            listener.onError(new Exception("No such document"));

                        }
                    } else {
                        // Call the error listener
                        listener.onError(task.getException());
                    }
                });
    }

    /**
     * Interface to check if the user is an admin. Return value is the boolean if yes or no
     */
    public interface AdminCheckListener {
        void onAdminCheckComplete(boolean isAdmin);
    }
//    this is all newly integrated firebase stuff, keep and leave what you think is good -daniel
    public void getUser(String id, OnDocumentRetrievedListener listener) {
        db.collection("users").document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        listener.onDocumentRetrieved(documentSnapshot);
                    } else {
                        listener.onError(new Exception("No user found with this ID"));
                    }
                })
                .addOnFailureListener(listener::onError);
    }
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

