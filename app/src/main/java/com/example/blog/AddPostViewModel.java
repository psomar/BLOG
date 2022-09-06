package com.example.blog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddPostViewModel extends AndroidViewModel {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference postReference;


    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    public LiveData<FirebaseUser> getUser() {
        return currentUser;
    }

    public AddPostViewModel(@NonNull Application application) {
        super(application);
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
        postReference = database.getReference("Post");
    }

    public void addPost (
                    String id,
                    String title,
                    String nickname,
                    String article,
                    double dataRelease,
                    int urlAuthorPhoto,
                    int like,
                    int dislike
            ) {
        database.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = new Post(
                        id,
                        title
                        ,article,
                        dataRelease,
                        like,
                        dislike,
                        urlAuthorPhoto,
                        null
                );
                postReference.push().setValue(post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
