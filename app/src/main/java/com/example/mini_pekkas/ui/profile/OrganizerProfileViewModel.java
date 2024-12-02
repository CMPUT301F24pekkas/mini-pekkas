package com.example.mini_pekkas.ui.profile;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.User;

/**
 * ViewModel for managing and exposing organizer profile data to the UI.
 * Interacts with Firebase to load and update profile information.
 */
public class OrganizerProfileViewModel extends ViewModel {

    // Data for the full name, userId, email, phone number, organizer status, and organizer location
    private final MutableLiveData<String> firstName;
    private final MutableLiveData<String> lastName;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<String> organizerLocation;
    private final MutableLiveData<String> profilePictureUrl;
    private final MutableLiveData<String> userID;
    private final MutableLiveData<String> facilityPictureUrl;
    private final MutableLiveData<String> facilityDescription;
    private final Firebase firebaseHelper;
    /**
     * Constructor that initializes the ViewModel, sets default profile values,
     * and loads the user's profile from Firebase.
     *
     * @param context Context required for Firebase initialization.
     */
    public OrganizerProfileViewModel(Context context) {
        // Initialize LiveData objects with default values
        firstName = new MutableLiveData<>();
        lastName = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        organizerLocation = new MutableLiveData<>(); // Initialize organizer location
        profilePictureUrl = new MutableLiveData<>();
        userID = new MutableLiveData<>();
        facilityPictureUrl = new MutableLiveData<>();
        facilityDescription = new MutableLiveData<>();
        firebaseHelper = new Firebase(context);

        // Set default values for these fields
        firstName.setValue("Organizer Full Name");
        lastName.setValue("Organizer Last Name");
        email.setValue("organizer@example.com");
        phoneNumber.setValue("123-456-7890");
        organizerLocation.setValue("Organizer Location");
        profilePictureUrl.setValue("");// Default organizer location
        userID.setValue("");
        facilityPictureUrl.setValue("");
        facilityDescription.setValue("...");
        loadUserProfile();
    }
    /**
     * Loads the user profile data from Firebase and updates the LiveData fields.
     * Uses Firebase to fetch the user's document and populate the profile fields.
     */
    private void loadUserProfile() {
        firebaseHelper.fetchUserDocument(new Firebase.InitializationListener() {
            @Override
            public void onInitialized() {
                if (firebaseHelper.getThisUser() != null) {
                    User user = firebaseHelper.getThisUser();
                    firstName.setValue(user.getName());
                    lastName.setValue(user.getLastname());
                    email.setValue(user.getEmail());
                    phoneNumber.setValue(user.getPhone());
                    organizerLocation.setValue((user.getFacility()));
                    profilePictureUrl.setValue(user.getProfilePhotoUrl());
                    userID.setValue(user.getUserID());
                    facilityPictureUrl.setValue(user.getFacilityPhotoUrl());
                    facilityDescription.setValue(user.getFacilityDesc());
                }
            }

        });
    }
    /**
     * Updates the user profile in Firebase using the current values in LiveData fields.
     */
    public void updateProfileInFirebase() {
        User updatedUser = new User(
                firstName.getValue(),
                lastName.getValue(),
                email.getValue(),
                phoneNumber.getValue(),
                organizerLocation.getValue(),
                profilePictureUrl.getValue(),
                userID.getValue(),
                facilityPictureUrl.getValue(),
                facilityDescription.getValue()
        );

        firebaseHelper.updateThisUser(updatedUser);
    }


    /**
     * Gets the LiveData for the organizer's first name.
     *
     * @return LiveData containing the first name.
     */
    public LiveData<String> getFirstName() {
        return firstName;
    }

    /**
     * Gets the LiveData for the organizer's last name.
     *
     * @return LiveData containing the last name.
     */
    public LiveData<String> getLastName() {
        return lastName;
    }

    /**
     * Gets the LiveData for the organizer's email.
     *
     * @return LiveData containing the email.
     */
    public LiveData<String> getEmail() {
        return email;
    }

    /**
     * Gets the LiveData for the organizer's phone number.
     *
     * @return LiveData containing the phone number.
     */
    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the LiveData for the organizer's location.
     *
     * @return LiveData containing the organizer location.
     */
    public LiveData<String> getOrganizerLocation() {
        return organizerLocation;
    }

    // Setters

    /**
     * Sets the first name of the organizer.
     *
     * @param name The new first name to set.
     */
    public void setFirstName(String name) {
        firstName.setValue(name);
    }

    /**
     * Sets the last name of the organizer.
     *
     * @param id The new last name to set.
     */
    public void setLastName(String id) {
        lastName.setValue(id);
    }

    /**
     * Sets the email of the organizer.
     *
     * @param userEmail The new email to set.
     */
    public void setEmail(String userEmail) {
        email.setValue(userEmail);
    }

    /**
     * Sets the phone number of the organizer.
     *
     * @param phone The new phone number to set.
     */
    public void setPhoneNumber(String phone) {
        phoneNumber.setValue(phone);
    }

    /**
     * Sets the location of the organizer.
     *
     * @param location The new location to set.
     */
    public void setOrganizerLocation(String location) {
        organizerLocation.setValue(location);
    }

    /**
     * Gets the LiveData for the organizer's profile picture URL.
     *
     * @return LiveData containing the profile picture URL.
     */
    public LiveData<String> getProfilePictureUrl() {
        return profilePictureUrl;
    }

    /**
     * Sets the profile picture URL of the organizer.
     *
     * @param url The new profile picture URL to set.
     */
    public void setProfilePictureUrl(String url) {
        profilePictureUrl.setValue(url);
    }

    /**
     * Gets the LiveData for the organizer's user ID.
     * @return LiveData for Facility picture
     */
    public LiveData<String> getFacilityPictureUrl() { return facilityPictureUrl; }

    /**
     * Sets the facility picture URL of the organizer.
     * @param url The new facility picture URL to set.
     */
    public void setFacilityPictureUrl(String url) { facilityPictureUrl.setValue(url); }

    /**
     * Gets the LiveData for the facility description.
     * @return LiveData for facility description
     */
    public LiveData<String> getFacilityDescription() { return facilityDescription; }

    /**
     * Sets the facility description.
     * @param description description to set
     */
    public void setFacilityDescription(String description) { facilityDescription.setValue(description); }

}
