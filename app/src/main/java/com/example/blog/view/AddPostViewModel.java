package com.example.blog.view;

import android.util.Log;

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
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class AddPostViewModel extends ViewModel {

    private static final String TAG = "AddPostViewModel";

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference postReference;
    private DatabaseReference userReference;

    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private MutableLiveData<User> users = new MutableLiveData<>();
    private MutableLiveData<Boolean> postSent = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<String> nickname = new MutableLiveData<String>();
    private MutableLiveData<String> profileImage = new MutableLiveData<>();


    private String id;

    public AddPostViewModel() {
        database = FirebaseDatabase.getInstance();
        postReference = database.getReference("Post");
        userReference = database.getReference("User");
        auth = FirebaseAuth.getInstance();
        String idUser = auth.getUid();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    currentUser.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                users.setValue(user);
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

        // подругажем ник пользователя, котоырый опубликовал пост

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String nick = dataSnapshot.getValue(User.class).getNickname();
                    id = dataSnapshot.getValue(User.class).getId();
                    if (Objects.equals(idUser, id)) {
                        nickname.setValue(nick);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, error.getMessage());
            }
        });
        database
                .getReference("User")
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String image = snapshot.child("profileImage").getValue(String.class);
                            profileImage.setValue(image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void addPost(Map<String, Object> post, String idPost) {
        postReference
                .child(idPost)
                .setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateUserPostCount();
                        postSent.setValue(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("errorFromAddPost", e.toString());
                    }
                });
    }

    private void updateUserPostCount() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        String userId = auth.getCurrentUser().getUid();
        userReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentCount = snapshot.child("myPost").getValue(Integer.class);
                        currentCount++;
                        userReference.child(userId).child("myPost").setValue(currentCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i("errorFromUpdateUserPost", error.toString());
                    }
                });
    }

    public MutableLiveData<String> getProfileImage() {
        return profileImage;
    }

    public LiveData<String> getNickname() {
        return nickname;
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<User> getUsers() {
        return users;
    }

    public LiveData<Boolean> getPostSent() {
        return postSent;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }
}
