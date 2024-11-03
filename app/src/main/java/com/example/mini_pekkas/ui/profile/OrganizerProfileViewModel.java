package com.example.mini_pekkas.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.User;

import android.content.Context;

public class OrganizerProfileViewModel extends ViewModel {

    // Data for the full name, userId, email, phone number, organizer status, and organizer location
    private final MutableLiveData<String> firstName;
    private final MutableLiveData<String> lastName;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<Boolean> isOrganizer;
    private final MutableLiveData<String> organizerLocation;
    private final Firebase firebaseHelper;

    public OrganizerProfileViewModel(Context context) {
        // Initialize LiveData objects with default values
        firstName = new MutableLiveData<>();
        lastName = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        isOrganizer = new MutableLiveData<>();
        organizerLocation = new MutableLiveData<>(); // Initialize organizer location
        firebaseHelper = new Firebase(context);

        // Set default values for these fields
        firstName.setValue("Organizer Full Name");
        lastName.setValue("Organizer Last Name");
        email.setValue("organizer@example.com");
        phoneNumber.setValue("123-456-7890");
        isOrganizer.setValue(true);  // Default to organizer view
        organizerLocation.setValue("Organizer Location"); // Default organizer location
        loadUserProfile();
    }

    // Getters
    public LiveData<String> getFirstName() {
        return firstName;
    }

    public LiveData<String> getLastName() {
        return lastName;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public LiveData<Boolean> getIsOrganizer() {
        return isOrganizer;
    }

    public LiveData<String> getOrganizerLocation() { // New getter for organizer location
        return organizerLocation;
    }

    // Setters
    public void setFirstName(String name) {
        firstName.setValue(name);
    }

    public void setLastName(String id) {
        lastName.setValue(id);
    }

    public void setEmail(String userEmail) {
        email.setValue(userEmail);
    }

    public void setPhoneNumber(String phone) {
        phoneNumber.setValue(phone);
    }

    public void setIsOrganizer(Boolean isOrganizerStatus) {
        isOrganizer.setValue(isOrganizerStatus);
    }

    public void setOrganizerLocation(String location) { // New setter for organizer location
        organizerLocation.setValue(location);
    }
    private void loadUserProfile() {
        firebaseHelper.fetchUserDocument(new Firebase.InitializationListener() {
            @Override
            public void onInitialized() {
                if (firebaseHelper.getThisUser() != null) {
                    User user = firebaseHelper.getThisUser();
                    firstName.setValue(user.getName());
                    email.setValue(user.getEmail());
                    organizerLocation.setValue((user.getFacility()));
                }
            }
        });
    }
}
