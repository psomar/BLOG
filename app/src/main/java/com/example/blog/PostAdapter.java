package com.example.blog;

import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();

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
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewArticle.setText(post.getArticle());
        holder.textViewAuthor.setText(post.getUser().getNickname());
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        holder.textViewDataRelease.setText(dateText);
        holder.textViewLike.setText(String.valueOf(post.getLike()));
        holder.textViewDislike.setText(String.valueOf(post.getDislike()));
        Glide.with(holder.itemView.getContext())
                .load(post.getUrlAuthorPhoto()) // post.getImageAuthor
                .into(holder.imageViewPhotoAuthor);
        holder.imageViewComment.setImageAlpha(android.R.drawable.stat_notify_chat);
        holder.imageViewSharePost.setImageAlpha(android.R.drawable.ic_menu_share);
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
        private final TextView textViewLike;
        private final TextView textViewDislike;
        private final TextView textViewDataRelease;
        private final ImageView imageViewPoster;
        private final ImageView imageViewStar;
        private final ImageView imageViewSharePost;
        private final ImageView imageViewComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewArticle = itemView.findViewById(R.id.textViewArticle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            imageViewPhotoAuthor = itemView.findViewById(R.id.imageViewPhotoAuthor);
            textViewLike = itemView.findViewById(R.id.textViewLike);
            textViewDislike = itemView.findViewById(R.id.textViewDislike);
            textViewDataRelease = itemView.findViewById(R.id.textViewDataRelease);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            imageViewStar = itemView.findViewById(R.id.imageViewStar);
            imageViewSharePost = itemView.findViewById(R.id.imageViewSharePost);
            imageViewComment = itemView.findViewById(R.id.imageViewComment);
        }
    }
}
