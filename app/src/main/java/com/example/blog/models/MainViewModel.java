package com.example.blog.models;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.core.AuthStateListener;
import com.example.blog.pojo.Post;
import com.example.blog.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> users = new MutableLiveData<>();
    private MutableLiveData<Boolean> login = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private MutableLiveData<User> PostUsers = new MutableLiveData<>();
    private MutableLiveData<Boolean> postSent = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> postId = new MutableLiveData<String>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> postRemove = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> favouriteDelete = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> isFavourites = new MutableLiveData<>();


    private String commentId;

    private boolean isFavouritesRemove = false;
    private FirebaseDatabase database;
    private DatabaseReference postReference;
    private DatabaseReference userReference;
    private FirebaseAuth auth;


    public MainViewModel() {
        AuthStateListener authStateListener = new AuthStateListener(users, login);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authStateListener);
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
        postReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }

                Collections.sort(postList, new Comparator<Post>() {
                    @Override
                    public int compare(Post post1, Post post2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                        try {
                            long time1 = sdf.parse(post1.getTimestamp()).getTime();
                            long time2 = sdf.parse(post2.getTimestamp()).getTime();
                            return Long.compare(time2, time1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                isLoading.setValue(true);
                posts.setValue(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                error.setValue(e.getMessage());
            }
        });
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    commentId = dataSnapshot.getValue(Post.class).getPostId();
                    postId.setValue(commentId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                error.setValue(e.getMessage());
            }
        });
    }

    public void deletePost(Post post) {
        DatabaseReference postRef = database.getReference("Post").child(post.getPostId());
        DatabaseReference userRef = database.getReference("User").child(auth.getCurrentUser().getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favouritePosts = (List<String>) snapshot.child("favouritePosts").getValue();
                if (favouritePosts != null && favouritePosts.contains(post.getPostId())) {
                    isFavouritesRemove = true;
                } else {
                    isFavouritesRemove = false;
                }
                postRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateUserPostCount();
                            if (isFavouritesRemove) {
                                deleteFromFavorites(post);
                            }
                            postRemove.setValue(true);
                        } else {
                            postRemove.setValue(false);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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

    private void updateUserPostCount() {
        String userId = auth.getCurrentUser().getUid();
        userReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentCount = snapshot.child("myPost").getValue(Integer.class);
                        currentCount--;
                        userReference.child(userId).child("myPost").setValue(currentCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
    }

    public LiveData<Boolean> addPostToFavorites(Post post) {
        String userId = auth.getCurrentUser().getUid();
        String postId = post.getPostId();

        userReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> favouritePosts = (List<String>) snapshot.child("favouritePosts").getValue();
                        if (favouritePosts != null && favouritePosts.contains(postId)) {
                            return;
                        }

                        int currentCount = snapshot.child("favouritePost").getValue(Integer.class);
                        currentCount++;
                        userReference.child(userId).child("favouritePost").setValue(currentCount);
                        userReference.child(userId).child("favouritePosts").child(String.valueOf(currentCount)).setValue(postId);
                        isFavourites.setValue(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
        return isFavourites;
    }

    public void deleteFromFavorites(Post post) {
        String userId = auth.getCurrentUser().getUid();

        userReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> favouritePosts = (List<String>) snapshot.child("favouritePosts").getValue();
                        if (favouritePosts != null) {

                            favouritePosts.remove(post.getPostId());
                            userReference.child(userId).child("favouritePosts").setValue(favouritePosts);

                            int currentCount = snapshot.child("favouritePost").getValue(Integer.class);
                            currentCount--;
                            userReference.child(userId).child("favouritePost").setValue(currentCount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
    }

    public LiveData<Boolean> toggleFavorite(Post post) {

        String userId = auth.getCurrentUser().getUid();

        userReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> favouritePosts = (List<String>) snapshot.child("favouritePosts").getValue();
                        if (favouritePosts != null && favouritePosts.contains(post.getPostId())) {
                            isFavourites.setValue(true);
                        } else {
                            isFavourites.setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
        return isFavourites;
    }


    public LiveData<Boolean> getFavouriteDelete() {
        return favouriteDelete;
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

    public LiveData<String> getPostId() {
        return postId;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

}