package com.example.blog.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.core.CurrentUserAuthStateListener;
import com.example.blog.pojo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationViewModel extends ViewModel {

    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userReference;

    public RegistrationViewModel() {
        CurrentUserAuthStateListener currentUserAuthStateListener = new CurrentUserAuthStateListener(currentUser);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(currentUserAuthStateListener);
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("User");
    }

    public void registration(
            String nickName,
            String email,
            String password,
            String profileImageUri
    ) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser == null) {
                            return;
                        }
                        String userId = firebaseUser.getUid();
                        String profileImagePath = "users/" + userId;
                        StorageReference storageReference = FirebaseStorage.getInstance().
                                getReference();
                        StorageReference profileImageRef = storageReference.child(profileImagePath);
                        profileImageRef.putFile(Uri.parse(profileImageUri))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                User user = new User(
                                                        userId,
                                                        nickName,
                                                        email,
                                                        uri.toString(),
                                                        0,
                                                        0);
                                                userReference.child(userId).setValue(user);
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        error.setValue(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });
    }

    public LiveData<String> getOnError() {
        return error;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

}

