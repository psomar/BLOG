package com.example.blog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blog.R;
import com.example.blog.pojo.Post;

import java.util.ArrayList;
import java.util.List;

//
//  BLOG.java
//  PostDetailAdapter
//  Created by Petr Somar
//

public class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.PostDetailViewHolder> {

    private List<Post> posts = new ArrayList<>();

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_detail_item,
                parent,
                false);
        return new PostDetailAdapter.PostDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostDetailViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.textViewTitleDetail.setText(post.getTitle());
        holder.textViewArticleDetail.setText(post.getArticle());
        holder.textViewAuthorDetail.setText(post.getNickname());
        holder.textViewDataReleaseDetail.setText(post.getTimestamp());
        Glide.with(holder.itemView.getContext())
                .load(post.getUrlAuthorPhoto())
                .into(holder.imageViewPhotoAuthorDetail);
        Glide.with(holder.itemView.getContext())
                .load(post.getUrlImage())
                .into(holder.imageViewPosterDetail);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class PostDetailViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTitleDetail;
        private final TextView textViewArticleDetail;
        private final TextView textViewAuthorDetail;
        private final ImageView imageViewPhotoAuthorDetail;
        private final TextView textViewDataReleaseDetail;
        private final ImageView imageViewPosterDetail;

        public PostDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitleDetail = itemView.findViewById(R.id.textViewTitleDetail);
            textViewArticleDetail = itemView.findViewById(R.id.textViewArticleDetail);
            textViewAuthorDetail = itemView.findViewById(R.id.textViewAuthorDetail);
            imageViewPhotoAuthorDetail = itemView.findViewById(R.id.imageViewPhotoAuthorDetail);
            textViewDataReleaseDetail = itemView.findViewById(R.id.textViewDataReleaseDetail);
            imageViewPosterDetail = itemView.findViewById(R.id.imageViewPosterDetail);
        }
    }

}
