package com.example.blog.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.core.CurrentUserAuthStateListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class PostEditorViewModel extends ViewModel {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference postReference;

    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> postEdit = new MutableLiveData<>(false);


    public PostEditorViewModel() {
        CurrentUserAuthStateListener currentUserAuthStateListener = new CurrentUserAuthStateListener(currentUser);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(currentUserAuthStateListener);
        database = FirebaseDatabase.getInstance();
        postReference = database.getReference("Post");
    }

    public void editPost(Map<String, Object> post, String postId) {
        postReference
                .child(postId)
                .updateChildren(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        postEdit.setValue(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });
    }


    public MutableLiveData<String> getError() {
        return error;
    }
}
