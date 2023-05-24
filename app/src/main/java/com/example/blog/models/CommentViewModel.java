package com.example.blog.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blog.core.AuthStateListener;
import com.example.blog.pojo.Comment;
import com.example.blog.pojo.Post;
import com.example.blog.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Map;
import java.util.Objects;

public class CommentViewModel extends ViewModel {


    private MutableLiveData<Boolean> commentSent = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<String> nickname = new MutableLiveData<>();
    private MutableLiveData<Boolean> login = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private MutableLiveData<String> idComment = new MutableLiveData<>();
    private MutableLiveData<String> idPost = new MutableLiveData<>();
    private MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> profileImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> commentRemove = new MutableLiveData<>(true);


    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference postReference;
    private DatabaseReference userReference;
    private DatabaseReference commentReference;

    private String nick;
    private final String idUser;
    private String id;

    public CommentViewModel() {
        AuthStateListener authStateListener = new AuthStateListener(currentUser, login);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authStateListener);
        database = FirebaseDatabase.getInstance();
        postReference = database.getReference("Post");
        userReference = database.getReference("User");
        commentReference = database.getReference("Comment");
        auth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        idUser = auth.getUid();


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    nick = dataSnapshot.getValue(User.class).getNickname();
                    id = dataSnapshot.getValue(User.class).getId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String nick = dataSnapshot.getValue(User.class).getNickname();
                    String id = dataSnapshot.getValue(User.class).getId();
                    if (Objects.equals(idUser, id)) {
                        nickname.setValue(nick);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                error.setValue(e.getMessage());
            }
        });
    }

    public void deleteComment(Comment comment, Post post) {
        DatabaseReference ref = database.getReference("Post")
                .child(post.getPostId())
                .child("Comment")
                .child(comment.getIdComment());
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    commentRemove.setValue(false);
                } else {
                    commentRemove.setValue(true);
                }
            }
        });
    }

    public void addComment(Map<String, Object> commentMap, String postId, String commentId) {
        DatabaseReference commentsRef = database.getReference("Post").
                child(postId).
                child("Comment").
                child(commentId);
        commentsRef.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    commentSent.setValue(true);
                } else {
                    commentSent.setValue(false);
                }
            }
        });
    }

    public void plusCommentCount(String postId) {
        postReference
                .child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentCount = snapshot.child("countComment").getValue(Integer.class);
                        currentCount++;
                        postReference.child(postId).child("countComment").setValue(currentCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
    }

    public void minusCommentCount(String postId) {
        postReference
                .child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentCount = snapshot.child("countComment").getValue(Integer.class);
                        currentCount--;
                        postReference.child(postId).child("countComment").setValue(currentCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
    }

    public LiveData<String> getProfileImage() {
        String userId = auth.getCurrentUser().getUid();
        Log.i("userId", userId);
        database.getReference("User")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String image = snapshot.child("profileImage").getValue(String.class);
                            profileImage.setValue(image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        error.setValue(e.getMessage());
                    }
                });
        return profileImage;
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
                }
                Collections.sort(commentList, new Comparator<Comment>() {
                    @Override
                    public int compare(Comment comment1, Comment comment2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                        try {
                            long time1 = sdf.parse(comment1.getTimestamp()).getTime();
                            long time2 = sdf.parse(comment2.getTimestamp()).getTime();
                            return Long.compare(time2, time1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                commentsLiveData.setValue(commentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                error.setValue(e.getMessage());
            }
        });
        return commentsLiveData;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public LiveData<String> getNickname() {
        return nickname;
    }

    public LiveData<Boolean> getLogin() {
        return login;
    }
}
