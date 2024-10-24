package com.example.mini_pekkas;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * This class accesses the firestore and contains functions to return important information
 */
public class Firebase {
    private final FirebaseFirestore db;
    private final String android_id;
    private DocumentSnapshot user_document;

    /**
     * Constructor to access the firestore in db
     * @param context
     * the context of the activity the function is called from
     */
    @SuppressLint("HardwareIds") // We just need the device id as a string
    public Firebase(Context context) {
        // initialize the database
        this.db = FirebaseFirestore.getInstance();
        // Get the device id
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // Get the user document, or create a new one if it doesn't exist
        findUserDocumentByDeviceId(android_id);
    }

    /**
     * find the user id and store it in the user_document variable
     * @param deviceId the device id of the user
     */
    public void findUserDocumentByDeviceId(String deviceId) {
        db.collection("users")
                .whereEqualTo("deviceID", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Found the document, store it in the class variable
                            user_document = document;
                            break; // Exit the loop after finding the document
                        }

                        if (user_document == null) {
                            // Document not found, create a new document
                            createNewUserDocument(deviceId);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    /**
     * Create a new user document in the firestore if it doesn't already exist
     * @param deviceId the device id of the user
     */
    private void createNewUserDocument(String deviceId) {
        Map<String, Object> user = new HashMap<>();
        user.put("deviceID", deviceId);
        // TODO Add other initial user data as needed
        user.put("email", null);
        user.put("facility", null);
        user.put("realName", null);
        user.put("phone", null);
        user.put("enrolled", null);
        user.put("waitlist", deviceId);
        user.put("notification", deviceId);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    user_document = documentReference.get().getResult(); // Get the newly created document
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    /**
     * all document request must implement this interface
     * onDocumentRetrieved is called when the document is retrieved successfully. Collect results in the listener
     * onError handles any errors that occur
     */
    public interface OnDocumentRetrievedListener {
        void onDocumentRetrieved(DocumentSnapshot documentSnapshot);
        void onError(Exception e);
    }


    /**
     * Queries and returns the data from document.get()
     * @param collection the name of the collection
     * @param document the name of the document
     */
    public void getDocument(String collection, String document, OnDocumentRetrievedListener listener) {
        DocumentReference docRef = db.collection(collection).document(document);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    listener.onDocumentRetrieved(documentSnapshot);
                } else {
                    listener.onError(new Exception("No such document"));
                }
            } else {
                listener.onError(task.getException());
            }
        });
    }


    /**
     * Checks if the current device is an admin user
     * @return
     * True if device id is in the admin collection store
     */
    public Boolean isAdmin() {
        // TODO
        return true;
    }
}
