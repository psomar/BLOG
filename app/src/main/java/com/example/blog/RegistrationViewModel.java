package com.example.blog;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationViewModel extends ViewModel {

    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userReference;

    public RegistrationViewModel() {
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    currentUser.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("User");
    }

    public LiveData<String> getOnError() {
        return error;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public void registration(
            String nickName,
            String email,
            String password
    ) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser == null) {
                            return;
                        }
                        User user = new User(
                                firebaseUser.getUid(),
                                nickName,
                                email,
                                password
                        );
                        userReference.child(user.getId()).setValue(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });
    }
}
