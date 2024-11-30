package com.example.mini_pekkas.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * The NotificationWorker class extends Worker and is called once to initialize a snapshot listener on notification
 * Produces a notification when a new notification is received. This class is called by the WorkManager in MainActivity.
 */
public class NotificationWorker extends Worker implements SendNotificationInterface{
    private String deviceID;
    private final NotificationManager notificationManager;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Initialize your snapshot listener here
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //MyFirebaseMessagingService();
        db.collection("user-notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        Log.d("FirebaseMessagingService", "onEvent called");
                        if (e != null) {
                            // Handle error
                            Log.e("FirebaseMessagingService", "Error getting notification documents: ", e);
                            return;
                        }

                        Log.d("FirebaseMessagingService", "Snapshot listener called id = " + deviceID);

                        for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            // Check if the document if newly added, not null, and if the document snapshot contains the deviceID
                            if (dc.getType() == DocumentChange.Type.ADDED && Objects.equals(dc.getDocument().getString("userID"), deviceID)) {
                                db.collection("notifications").whereEqualTo("id", dc.getDocument().getString("notificationID")).get()
                                        .addOnSuccessListener(task -> {
                                            if (!task.isEmpty()) {
                                                Notifications notification = task.getDocuments().get(0).toObject(Notifications.class);
                                                Log.d("FirebaseMessagingService", "Notification title: " + notification.getTitle());
                                                sendNotification(notification.getTitle(), notification.getDescription(), getApplicationContext(), notificationManager);
                                            }
                                        });
                            }
                        }
                    }
                });

        return Result.success();
    }
}