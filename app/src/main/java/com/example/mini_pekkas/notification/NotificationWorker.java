package com.example.mini_pekkas.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * The NotificationWorker class extends Worker and is called once to initialize a snapshot listener on notification
 * Produces a notification when a new notification is received. This class is called by the WorkManager in MainActivity.
 */
public class NotificationWorker extends Worker implements SendNotificationInterface{
    private String deviceID;
    private FirebaseFirestore db;
    private final NotificationManager notificationManager;
    private ListenerRegistration snapshotListenerRegistration;  // The snapshot listener used for fetching notifications

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        db = FirebaseFirestore.getInstance();

        startFirestoreListener();   // Add the listener
    }

    @NonNull
    @Override
    public Result doWork() {
        return Result.retry();  // Reschedule the task
    }

    /**
     * Creates the snapshot listener for notifications. Removes the previous listener if it exists.
     */
    private void startFirestoreListener() {
        if (snapshotListenerRegistration != null) {
            snapshotListenerRegistration.remove();
        }

        snapshotListenerRegistration = db.collection("user-notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if (e != null) {    // Handle error
                            Log.e("FirebaseMessagingService", "Error getting notification documents: ", e);
                            return;
                        }

                        for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            // Check if the document if newly added, not read, and if the document snapshot contains the deviceID (notification belongs to user)
                            if (dc.getType() == DocumentChange.Type.ADDED && Boolean.FALSE.equals(dc.getDocument().getBoolean("read")) && Objects.equals(dc.getDocument().getString("userID"), deviceID)) {
                                db.collection("notifications").whereEqualTo("id", dc.getDocument().getString("notificationID")).get()
                                        .addOnSuccessListener(task -> {
                                            if (!task.isEmpty()) {
                                                Log.d("NotificationWorker", "New notification received, size = " + task.size());
                                                for (DocumentSnapshot document : task.getDocuments()) {
                                                    //  Display new notifications and mark it as read
                                                    Notifications notification = document.toObject(Notifications.class);
                                                    sendNotification(notification.getTitle(), notification.getDescription(), getApplicationContext(), notificationManager);
                                                    dc.getDocument().getReference().update("read", true);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}