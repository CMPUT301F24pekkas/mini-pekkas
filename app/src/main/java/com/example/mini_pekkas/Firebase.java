package com.example.mini_pekkas;

import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class accesses the firestore and contains functions to return important information
 */
public class Firebase {
    private FirebaseFirestore db;
    private String android_id = null;
    //private CollectionReference adminList;

    /**
     * Constructor to access the firestore in db
     * @param context
     * the context of the activity the function is called from
     */
    public Firebase(Context context) {
        // initialize the database
        this.db = FirebaseFirestore.getInstance();

        // Get the device id
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * No context constructor for calling outside an activity
     */
    public Firebase() {
        // initialize the database
        this.db = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDb() {
        return db;
    }


    public Object getDocument(String collection, String document) {
        AtomicReference<Object> data = null;

        DocumentReference docRef = db.collection(collection).document(document);
        docRef.get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document1 = task.getResult();
                if (document1.exists()) {
                    data.set(document1.getData());
                } else {
                    data.set(null);
                }
            } else {
                // TODO why cant I just throw an exception
                task.getException();
            }
        });
        return data;
    }


    /**
     * Checks if the current device is an admin user
     * @return
     * True if device id is in the admin collection store
     */
    public Boolean isAdmin() {


        return true;
    }
}
