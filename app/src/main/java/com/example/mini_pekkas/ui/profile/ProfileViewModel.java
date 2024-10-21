package com.example.mini_pekkas.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    // Data for the full name, userId, email, phone number, and organizer status
    private final MutableLiveData<String> fullName;
    private final MutableLiveData<String> userId;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<Boolean> isOrganizer;

    public ProfileViewModel() {
        // Initialize LiveData objects with default values
        fullName = new MutableLiveData<>();
        userId = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        isOrganizer = new MutableLiveData<>();

        // Set default values for these fields
        fullName.setValue("Full 45678");
        userId.setValue("Name123");
        email.setValue("example@example.com");
        phoneNumber.setValue("123-456-7890");
        isOrganizer.setValue(false);  // Default to non-organizer view
    }

    // getters
    public LiveData<String> getFullName() {
        return fullName;
    }

    public LiveData<String> getUserId() {
        return userId;
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

    // Setters
    public void setFullName(String name) {
        fullName.setValue(name);
    }

    public void setUserId(String id) {
        userId.setValue(id);
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
}
