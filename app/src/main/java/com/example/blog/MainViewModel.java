package com.example.blog;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> users = new MutableLiveData<>();
    private MutableLiveData<Boolean> login = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();


    private FirebaseDatabase database;
    private DatabaseReference postReference;
    private FirebaseAuth auth;

    public MainViewModel() {
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    users.setValue(firebaseAuth.getCurrentUser());
                    login.setValue(true);
                } else {
                    login.setValue(false);
                }
            }
        });
        database = FirebaseDatabase.getInstance();
        postReference = database.getReference("Post");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    return;
                }
                List<Post> postsFromDb = new ArrayList<>();
                Post post = snapshot.getValue(Post.class);
                if (post == null) {
                    return;
                }
                if (post.equals(getPosts())) {
                    postsFromDb.add(post);
                }
                posts.setValue(postsFromDb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<FirebaseUser> getUser() {
        return users;
    }

    public LiveData<Boolean> getLogin() {
        return login;
    }

    public MutableLiveData<List<Post>> getPosts() {
        return posts;
    }
}
