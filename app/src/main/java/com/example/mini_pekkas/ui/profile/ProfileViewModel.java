package com.example.mini_pekkas.ui.profile;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.User;

/**
 * ViewModel class for managing user profile data in the UI, including name, email,
 * phone number, and other user information.
 */
public class ProfileViewModel extends ViewModel {

    // Data for the full name, userId, email, phone number, and organizer status
    private final MutableLiveData<String> firstName;
    private final MutableLiveData<String> lastName;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<String> organizerLocation;
    private final MutableLiveData<String> pfpText;
    private final MutableLiveData<String> profilePictureUrl;
    private final MutableLiveData<String> userID;
    private final MutableLiveData<String> facilityPictureUrl;
    private final MutableLiveData<String> facilityDescription;
    private final Firebase firebaseHelper;
    /**
     * Initializes a new ProfileViewModel instance with default profile values and
     * Firebase integration for data fetching and updating.
     *
     * @param context The context from which this ViewModel is created.
     */
    public ProfileViewModel(Context context) {
        organizerLocation = new MutableLiveData<>();;
        // Initialize LiveData objects with default values
        firstName = new MutableLiveData<>();
        lastName = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        pfpText = new MutableLiveData<>();
        profilePictureUrl = new MutableLiveData<>();
        userID = new MutableLiveData<>();
        facilityPictureUrl = new MutableLiveData<>();
        facilityDescription = new MutableLiveData<>();

        firebaseHelper = new Firebase(context);

        // Set default values for these fields
        pfpText.setValue(" ");
        firstName.setValue("Full 45678");
        lastName.setValue("Name123");
        email.setValue("example@example.com");
        phoneNumber.setValue("123-456-7890");
        profilePictureUrl.setValue("");
        userID.setValue("");
        facilityPictureUrl.setValue("");
        facilityDescription.setValue("...");

        loadUserProfile();
    }
    /**
     * Fetches the user profile data from Firebase and sets the LiveData fields
     * with the retrieved user information.
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
                    profilePictureUrl.setValue(user.getProfilePhotoUrl());
                    userID.setValue(user.getUserID());
                    facilityPictureUrl.setValue(user.getFacilityPhotoUrl());
                    facilityDescription.setValue(user.getFacilityDesc());
                }
            }
        });
    }
    /**
     * Updates the current user profile data in Firebase with the modified values from the LiveData fields.
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

    // getters
    /**
     * @return LiveData object for the user's first name.
     */
    public LiveData<String> getFirstName() {
        return firstName;
    }

    /**
     * @return LiveData object for the user's last name.
     */
    public LiveData<String> getLastName() {
        return lastName;
    }

    /**
     * @return LiveData object for the user's email address.
     */
    public LiveData<String> getEmail() {
        return email;
    }

    /**
     * @return LiveData object for the user's phone number.
     */
    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return LiveData object for the profile picture text (initials or placeholder).
     */
    public LiveData<String> getPfpText() {
        return pfpText;
    }

    /**
     * Sets the user's first name.
     *
     * @param name The first name to set.
     */
    public void setFirstName(String name) {
        firstName.setValue(name);
    }

    /**
     * Sets the user's last name.
     *
     * @param id The last name to set.
     */
    public void setLastName(String id) {
        lastName.setValue(id);
    }

    /**
     * Sets the user's email address.
     *
     * @param userEmail The email address to set.
     */
    public void setEmail(String userEmail) {
        email.setValue(userEmail);
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone The phone number to set.
     */
    public void setPhoneNumber(String phone) {
        phoneNumber.setValue(phone);
    }

    /**
     * Sets the profile picture text, typically an initial or placeholder character.
     *
     * @param text The profile picture text to set.
     */
    public void setPfpText(String text) {
        pfpText.setValue(text);
    }

    /**
     * Gets the LiveData for the profile picture URL.
     *
     * @return LiveData containing the profile picture URL.
     */
    public LiveData<String> getProfilePictureUrl() {
        return profilePictureUrl;
    }

    /**
     * Sets the profile picture URL of the user.
     *
     * @param url The new profile picture URL to set.
     */
    public void setProfilePictureUrl(String url) {
        profilePictureUrl.setValue(url);
    }

}

