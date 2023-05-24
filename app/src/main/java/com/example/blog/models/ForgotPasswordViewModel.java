package com.example.blog.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordViewModel extends ViewModel {

    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> onSuccess = new MutableLiveData<>();

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> inSuccess() {
        return onSuccess;
    }

    private FirebaseAuth auth;

    public ForgotPasswordViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public void resetPassword(String email) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void success) {
                onSuccess.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error.setValue(e.getMessage());
            }
        });
    }
}
