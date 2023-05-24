package com.example.blog.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthStateListener implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<FirebaseUser> currentUser;
    private final MutableLiveData<Boolean> login;

    public AuthStateListener(MutableLiveData<FirebaseUser> currentUser, MutableLiveData<Boolean> login) {
        this.currentUser = currentUser;
        this.login = login;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            currentUser.setValue(firebaseAuth.getCurrentUser());
            login.setValue(true);
        } else {
            login.setValue(false);
        }
    }
}
