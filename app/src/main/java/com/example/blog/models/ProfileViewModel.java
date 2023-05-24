package com.example.blog.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.core.CurrentUserAuthStateListener;
import com.example.blog.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> idUser = new MutableLiveData<>();

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userReference;

    private String userId;
    private String userID;

    public ProfileViewModel() {
        CurrentUserAuthStateListener currentUserAuthStateListener = new CurrentUserAuthStateListener(user);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(currentUserAuthStateListener);
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("User");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    return;
                }
                List<User> usersFromDb = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        return;
                    }
                    if (user.getId().equals(currentUser.getUid())) {
                        usersFromDb.add(user);
                    }
                }
                users.setValue(usersFromDb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                error.setValue(e.getMessage());
            }
        });
    }

    public void changeNick(User user, String newNickName) {
        String userId = user.getId();
        userReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nickname = snapshot.child("nickname").getValue(String.class);
                        userReference.child(userId).child("nickname").setValue(newNickName);

                        DatabaseReference postsReference = FirebaseDatabase.
                                getInstance().
                                getReference().
                                child("Post");
                        postsReference.
                                orderByChild("userId").
                                equalTo(userId).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            String postId = postSnapshot.getKey();
                                            postsReference.child(postId).child("nickname").setValue(newNickName);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError e) {
                                        error.setValue(e.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
    }

    public LiveData<String> getUserId() {
        return idUser;
    }

    public void logOut() {
        auth.signOut();
    }

    public LiveData<String> getError() {
        return error;
    }

}
