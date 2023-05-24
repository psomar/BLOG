package com.example.blog.adapters;

import android.text.TextUtils;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//
//  BLOG.java
//  PostAdapter
//  Created by PETR SOMAR
//


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();

    public String id;

    private onClickComment onClickComment;
    private onClickTitle onClickTitle;
    private onClickUser onClickUser;

    public void setOnClickTitle(PostAdapter.onClickTitle onClickTitle) {
        this.onClickTitle = onClickTitle;
    }

    public void setOnClickUser(PostAdapter.onClickUser onClickUser) {
        this.onClickUser = onClickUser;
    }

    public void setOnClickComment(PostAdapter.onClickComment onClickComment) {
        this.onClickComment = onClickComment;
    }

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
        Glide.with(holder.itemView.getContext())
                .load(post.getUrlImage())
                .into(holder.imageViewPoster);
        int count = post.getCountComment();
        holder.textViewAnotherComment.setText(getStringFormat(count));
        if (count == 0) {
            holder.textViewCountComments.setVisibility(View.GONE);
            holder.textViewAnotherComment.setVisibility(View.GONE);
            holder.textViewComment.setVisibility(View.VISIBLE);
            holder.imageViewComment.setVisibility(View.VISIBLE);
        } else {
            holder.textViewCountComments.setVisibility(View.VISIBLE);
            holder.textViewCountComments.setText(String.valueOf(count));
            holder.textViewComment.setVisibility(View.GONE);
            holder.textViewAnotherComment.setVisibility(View.VISIBLE);
            holder.imageViewComment.setVisibility(View.GONE);
        }
        holder.textViewAuthor.setText(post.getNickname());
        holder.textViewDataRelease.setText(post.getTimestamp());
        holder.textViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickComment != null) {
                    onClickComment.onClickComment(post);
                }
            }
        });
        holder.textViewAnotherComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickComment != null) {
                    onClickComment.onClickComment(post);
                }
            }
        });
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickTitle != null) {
                    onClickTitle.onCLickTitle(post);
                }
            }
        });
        ;
        holder.textViewAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickUser != null) {
                    onClickUser.onClickUser(post);
                }
            }
        });
        Glide.with(holder.itemView.getContext())
                .load(post.getUrlAuthorPhoto())
                .into(holder.imageViewPhotoAuthor);

        String title = post.getTitle();
        String article = post.getArticle();

        if ((title.length() + article.length()) <= 50) {
            holder.imageViewPoster.setVisibility(View.VISIBLE);
            holder.textViewArticle.setVisibility(View.VISIBLE);
            holder.textViewArticle.setText(article);
            holder.textViewTitle.setText(title);
        } else {
            holder.textViewArticle.setMaxLines(7);
            holder.imageViewPoster.setVisibility(View.GONE);
            holder.textViewArticle.setEllipsize(TextUtils.TruncateAt.END);
            holder.textViewShowFullText.setVisibility(View.VISIBLE);

            holder.textViewShowFullText.setOnClickListener(v -> {
                holder.textViewArticle.setMaxLines(Integer.MAX_VALUE);
                holder.textViewArticle.setEllipsize(null);
                holder.imageViewPoster.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext())
                        .load(post.getUrlImage())
                        .into(holder.imageViewPoster);
                holder.textViewShowFullText.setVisibility(View.GONE);
            });
        }
    }

    public static String getStringFormat(int count) {
        Map<Integer, String> formats = new HashMap<Integer, String>();
        formats.put(1, "комментарий");
        formats.put(2, "комментария");
        formats.put(3, "комментария");
        formats.put(4, "комментария");
        formats.put(5, "комментариев");
        return formats.get(count) != null ? formats.get(count) : formats.get(5);
    }

    public interface onClickComment {

        void onClickComment(Post post);
    }

    public interface onClickUser {

        void onClickUser(Post post);

    }

    public interface onClickTitle {

        void onCLickTitle(Post post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewAnotherComment;
        private final TextView textViewTitle;
        private final TextView textViewArticle;
        private final TextView textViewAuthor;
        private final ImageView imageViewPhotoAuthor;
        private final TextView textViewCountComments;
        private final TextView textViewComment;
        private final TextView textViewDataRelease;
        private ImageView imageViewPoster;
        private final ImageView imageViewComment;
        private final TextView textViewShowFullText;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAnotherComment = itemView.findViewById(R.id.textViewAnotherComment);
            textViewTitle = itemView.findViewById(R.id.textViewTitleDetail);
            textViewArticle = itemView.findViewById(R.id.textViewArticleDetail);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthorDetail);
            textViewCountComments = itemView.findViewById(R.id.textViewCountComments);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            imageViewPhotoAuthor = itemView.findViewById(R.id.imageViewPhotoAuthorDetail);
            textViewDataRelease = itemView.findViewById(R.id.textViewDataReleaseDetail);
            imageViewPoster = itemView.findViewById(R.id.imageViewPosterDetail);
            imageViewComment = itemView.findViewById(R.id.imageViewComment);
            textViewShowFullText = itemView.findViewById(R.id.textViewShowFullText);
        }
    }
}
