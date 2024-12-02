package com.example.mini_pekkas.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * The FBNotifications class extends FirebaseMessagingService and is responsible for handling Firebase Cloud Messaging (FCM) notifications.
 * The service is initialized and destroyed when needed, and does not need to be called anywhere else
 */
public class FBNotifications extends FirebaseMessagingService implements SendNotificationInterface{

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NotificationManager notificationManager;    // Member variable to store the NotificationManager

    /**
     * Called when the service is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("FirebaseMessagingService", "FirebaseMessagingService Service created");


    }

    /**
     *
     * @param remoteMessage Remote message that has been received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be done.
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), this, notificationManager);
        }
    }
}