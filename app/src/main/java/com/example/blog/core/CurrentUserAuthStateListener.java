package com.example.blog.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CurrentUserAuthStateListener implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<FirebaseUser> currentUser;

    public CurrentUserAuthStateListener(MutableLiveData<FirebaseUser> currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            currentUser.setValue(firebaseAuth.getCurrentUser());
        }
    }
}
