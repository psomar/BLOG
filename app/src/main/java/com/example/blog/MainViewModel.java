package com.example.blog;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.pojo.Post;
import com.example.blog.pojo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private MutableLiveData<User> PostUsers = new MutableLiveData<>();
    private MutableLiveData<Boolean> postSent = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();


    private FirebaseDatabase database;
    private DatabaseReference postReference;
    private DatabaseReference userReference;
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
        userReference = database.getReference("User");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                PostUsers.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                error.setValue(e.getMessage());
            }
        });
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                posts.setValue(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<Boolean> getPostSent() {
        return postSent;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<FirebaseUser> getUser() {
        return users;
    }

    public LiveData<Boolean> getLogin() {
        return login;
    }

    public MutableLiveData<FirebaseUser> getUsers() {
        return users;
    }
}
