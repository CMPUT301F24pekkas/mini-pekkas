package com.example.mini_pekkas.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.mini_pekkas.MainActivity;
import com.example.mini_pekkas.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * The NotificationForegroundService class is a foreground service that listens for notifications.
 * This service allows the app to run in the background while fetching notification
 */
public class NotificationForegroundService extends Service implements SendNotificationInterface{

    private FirebaseFirestore db;
    private String deviceID;
    private NotificationManager notificationManager;
    private ListenerRegistration snapshotListenerRegistration;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Only start the service if the notification permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            createNotificationChannel();    // For the initial notification listener
            startForeground((int) System.currentTimeMillis(), createNotification()); // Random id for first notification
            startFirestoreListener();
        }
    }

    // ... (other methods for notification channel, notification creation, and Firestore listener) ...

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Restart service if killed by system
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    /**
     * Create a notification channel for the foreground service and the first notification
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Base channel";
            String description = "Basic channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("base_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Create a notification for the foreground service
     * @return the notification
     */
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class); // Or your desired activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "base_channel")
                .setContentTitle("Mini pekkas Notification Listener")
                .setContentText("Running in the background for notifications")
                .setSmallIcon(R.drawable.baseline_notifications_24) // Replace with your icon
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    /**
     * Create the listener for the Firestore database. This listener listens for new notifications and marks them as read.
     */
    public void startFirestoreListener() {
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
