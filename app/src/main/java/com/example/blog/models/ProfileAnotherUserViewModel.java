package com.example.blog.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.core.AuthStateListener;
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

public class ProfileAnotherUserViewModel extends ViewModel {


    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<List<User>> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> login = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public ProfileAnotherUserViewModel() {
        AuthStateListener authStateListener = new AuthStateListener(currentUser, login);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authStateListener);
        database = FirebaseDatabase.getInstance();
    }

    public LiveData<List<User>> getUsers(String userId) {
        DatabaseReference userRef = database.getReference("User").
                child(userId);
        userRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<User> userList = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            Log.i("userSnapshot", userSnapshot.toString());
                            User user = snapshot.getValue(User.class);
                            userList.add(user);
                        }
                        user.setValue(userList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
        return user;
    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
