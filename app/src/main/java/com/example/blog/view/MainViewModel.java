package com.example.blog.view;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.pojo.Comment;
import com.example.blog.pojo.Post;
import com.example.blog.pojo.User;
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
    private MutableLiveData<List<Post>> deletePost = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> postRemove = new MutableLiveData<>(true);


    private String commentId;

    private FirebaseDatabase database;
    private DatabaseReference postReference;
    private DatabaseReference userReference;
    private DatabaseReference commentReference;
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
        commentReference = database.getReference("Comment");
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
                // Сортировка по временной метке в порядке возрастания (от старых к новым)
                Collections.sort(postList, new Comparator<Post>() {
                    @Override
                    public int compare(Post p1, Post p2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                        try {
                            long t1 = sdf.parse(p1.getTimestamp()).getTime();
                            long t2 = sdf.parse(p2.getTimestamp()).getTime();
                            return Long.compare(t1, t2);
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
            public void onCancelled(@NonNull DatabaseError error) {

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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deletePost(Post post) {
        DatabaseReference ref = database.getReference("Post").child(post.getPostId());
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    postRemove.setValue(false);
                } else {
                    postRemove.setValue(true);
                    updateUserPostCount();
                }
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
                        currentCount--;
                        userReference.child(userId).child("myPost").setValue(currentCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i("errorFromUpdateUserPost", error.toString());
                    }
                });
    }

    public void addPostToFavorites() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        String userId = auth.getCurrentUser().getUid();
        userReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentCount = snapshot.child("favouritePost").getValue(Integer.class);
                        currentCount++;
                        userReference.child(userId).child("favouritePost").setValue(currentCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i("errorFromUpdateUserPost", error.toString());
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

    public LiveData<String> getPostId() {
        return postId;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

}
