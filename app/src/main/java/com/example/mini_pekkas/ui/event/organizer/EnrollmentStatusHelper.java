package com.example.mini_pekkas.ui.event.organizer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class for managing enrollment status.
 */
public class EnrollmentStatusHelper {
    private static final String PREFS_NAME = "EnrollmentStatusPrefs";
    private static final String ENROLLMENT_KEY_PREFIX = "enrollment_started_";

    /**
     * Sets the enrollment status for a specific event.
     * @param context The application context.
     * @param eventId The ID of the event.
     * @param started Whether enrollment has started for the event.
     */
    public static void setEnrollmentStarted(Context context, String eventId, boolean started) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ENROLLMENT_KEY_PREFIX + eventId, started);
        editor.apply();
    }

    /**
     * Checks if enrollment has started for a specific event.
     * @param context The application context.
     * @param eventId The ID of the Event
     * @return boolean whether enrollment has started for the event
     */
    public static boolean isEnrollmentStarted(Context context, String eventId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(ENROLLMENT_KEY_PREFIX + eventId, false);
    }
}

