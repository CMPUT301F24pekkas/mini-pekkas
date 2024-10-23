package com.example.mini_pekkas;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class accesses the firestore and contains functions to return important information
 */
public class Firebase {
    private final FirebaseFirestore db;
    private final String android_id;
    //private CollectionReference adminList;

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
    }

    /**
     * returns the FirebaseFirestore instance
     * @return a variable linked to the firebase
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * returns the device id as a String
     * @return the device id
     */
    public String getAndroid_id() {
        return android_id;
    }

    /**
     * Queries and returns the data from document.get()
     * @param collection the name of the collection
     * @param document the name of the document
     * @return a DocumentSnapshot, identical to collection.document.get()
     */
    public DocumentSnapshot getDocument(String collection, String document) {
        final DocumentSnapshot[] doc = new DocumentSnapshot[1];

        DocumentReference docRef = db.collection(collection).document(document);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        doc[0] = (DocumentSnapshot) document.getData();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return doc[0];
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
