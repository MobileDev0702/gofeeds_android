package com.stackrage.gofeds;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseIIDServiceDemo";
    public String[] name;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void sendRegistrationToServer(String token) {

    }
}
