package com.example.mini_pekkas.ui.event.organizer;

import android.content.Context;
import android.content.SharedPreferences;

public class EnrollmentStatusHelper {
    private static final String PREFS_NAME = "EnrollmentStatusPrefs";
    private static final String ENROLLMENT_KEY_PREFIX = "enrollment_started_";

    public static void setEnrollmentStarted(Context context, String eventId, boolean started) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ENROLLMENT_KEY_PREFIX + eventId, started);
        editor.apply();
    }

    public static boolean isEnrollmentStarted(Context context, String eventId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(ENROLLMENT_KEY_PREFIX + eventId, false);
    }
}

