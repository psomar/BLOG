package com.example.blog.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.core.AuthStateListener;
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

import java.util.ArrayList;
import java.util.List;

public class PostDetailViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<List<User>> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> login = new MutableLiveData<>();
    private MutableLiveData<List<Comment>> comments = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public PostDetailViewModel() {
        AuthStateListener authStateListener = new AuthStateListener(currentUser, login);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authStateListener);
        database = FirebaseDatabase.getInstance();
    }

    public LiveData<List<Comment>> getComments(String postId) {
        DatabaseReference commentsRef = database.getReference("Post").
                child(postId).
                child("Comment");
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                    comments.setValue(commentList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return comments;
    }

    public LiveData<List<Post>> getPosts(String postId) {
        DatabaseReference postsRef = database.getReference("Post").
                child(postId);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot postsSnapshot : snapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    postList.add(post);
                    posts.setValue(postList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return posts;
    }

    public MutableLiveData<Boolean> getLogin() {
        return login;
    }
}
