package com.example.mini_pekkas.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.User;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;


public class ProfileViewModel extends ViewModel {

    // Data for the full name, userId, email, phone number, and organizer status
    private final MutableLiveData<String> firstName;
    private final MutableLiveData<String> lastName;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<String> organizerLocation;
    private final MutableLiveData<String> pfpText;
    private final Firebase firebaseHelper;

    public ProfileViewModel(Context context) {
        organizerLocation = new MutableLiveData<>();;
        // Initialize LiveData objects with default values
        firstName = new MutableLiveData<>();
        lastName = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        pfpText = new MutableLiveData<>();
        firebaseHelper = new Firebase(context);

        // Set default values for these fields
        pfpText.setValue(" ");
        firstName.setValue("Full 45678");
        lastName.setValue("Name123");
        email.setValue("example@example.com");
        phoneNumber.setValue("123-456-7890");
        loadUserProfile();
    }
    public void updateProfileInFirebase() {
        User updatedUser = new User(
                firstName.getValue(),
                lastName.getValue(),
                email.getValue(),
                phoneNumber.getValue(),
                organizerLocation.getValue()
        );

        firebaseHelper.updateThisUser(updatedUser);
    }

    // getters
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


    public LiveData<String> getPfpText() {
        return pfpText;
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

    public void setPfpText(String text) {
        pfpText.setValue(text);
    }

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
                }
            }
        });
    }

    public void saveUserProfile() {
        firebaseHelper.fetchUserDocument(new Firebase.InitializationListener() {
            @Override
            public void onInitialized() {
                if (firebaseHelper.getThisUser() != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", firstName);
                    map.put("lastName", lastName);
                    map.put("email", email);
                    map.put("phone",phoneNumber);
                }
            }
        });
    }
}
