package com.example.mini_pekkas;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.provider.Settings.Secure;

public class User {
    private String device_id;
    public User(Context context){
        this.device_id = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
    }
    public String getDeviceId(){
        return this.device_id;
    }
}
