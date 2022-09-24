package com.example.blog.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blog.pojo.Post;
import com.example.blog.R;
import com.example.blog.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference postsReference = database.getReference("Post");
    private DatabaseReference userReference = database.getReference("User");
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    public String userId;
    public String id;


    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public List<Post> getPosts() {
        return posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,
                parent,
                false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        getReference(holder);
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userId = snapshot.getValue(Post.class).getUserId();
                if (userId != null) {
                    Log.i("TAG1", userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id = snapshot.getValue(User.class).getId();
                if (id != null) {
                    Log.d("TAG2", id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewArticle.setText(post.getArticle());
        holder.imageViewPhotoAuthor.setImageDrawable
                (
                        holder.itemView.getContext().getDrawable(R.drawable.default_avatar)
                );
        Glide.with(holder.itemView.getContext())
                .load(post.getUrlImage())
                .into(holder.imageViewPoster);
        holder.imageViewComment.setImageDrawable
                (
                        holder.itemView.getContext().getDrawable(android.R.drawable.sym_action_chat)
                );
        holder.textViewAuthor.setText(post.getNickname());
        holder.textViewDataRelease.setText(post.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTitle;
        private final TextView textViewArticle;
        private final TextView textViewAuthor;
        private final ImageView imageViewPhotoAuthor;
        private final View viewLine;
        private final TextView textViewDataRelease;
        private final ImageView imageViewPoster;
        private final ImageView imageViewComment;

        // Изменить на новые элементы. Добавилось меню.

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewArticle = itemView.findViewById(R.id.textViewArticle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            viewLine = itemView.findViewById(R.id.viewLine);
            imageViewPhotoAuthor = itemView.findViewById(R.id.imageViewPhotoAuthor);
            textViewDataRelease = itemView.findViewById(R.id.textViewDataRelease);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            imageViewComment = itemView.findViewById(R.id.imageViewComment);
        }

    }

    private void getReference(@NonNull PostViewHolder holder) {
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    currentUser.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });


    }
}
