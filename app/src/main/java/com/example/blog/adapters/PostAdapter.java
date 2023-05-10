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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();


    public String id;

    private onClickComment onClickComment;
    private onClickTitle onClickTitle;

    public void setOnClickTitle(PostAdapter.onClickTitle onClickTitle) {
        this.onClickTitle = onClickTitle;
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
        if (post.getCountComment() == 0) {
            holder.textViewCountComments.setVisibility(View.GONE);
            holder.imageViewComment.setImageDrawable
                    (
                            holder.itemView.getContext().getDrawable(android.R.drawable.sym_action_chat)
                    );
        } else if (post.getCountComment() == 1) {
            holder.textViewAnotherComment.setVisibility(View.VISIBLE);
            holder.textViewAnotherComment.setText("комментарий");
            holder.imageViewComment.setVisibility(View.GONE);
            holder.textViewComment.setVisibility(View.GONE);
        } else if (post.getCountComment() == 2) {
            holder.textViewAnotherComment.setVisibility(View.VISIBLE);
            holder.textViewAnotherComment.setText("комментария");
            holder.textViewComment.setVisibility(View.GONE);
            holder.imageViewComment.setVisibility(View.GONE);
        } else if (post.getCountComment() == 3) {
            holder.textViewAnotherComment.setVisibility(View.VISIBLE);
            holder.textViewAnotherComment.setText("комментария");
            holder.textViewComment.setVisibility(View.GONE);
            holder.imageViewComment.setVisibility(View.GONE);
        } else if (post.getCountComment() == 4) {
            holder.textViewAnotherComment.setVisibility(View.VISIBLE);
            holder.textViewAnotherComment.setText("комментария");
            holder.textViewComment.setVisibility(View.GONE);
            holder.imageViewComment.setVisibility(View.GONE);
        } else {
            holder.textViewAnotherComment.setVisibility(View.VISIBLE);
            holder.textViewAnotherComment.setText("комментариев");
            holder.textViewComment.setVisibility(View.GONE);
            holder.imageViewComment.setVisibility(View.GONE);
        }
        holder.textViewCountComments.setText(String.valueOf(post.getCountComment()));
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
        Glide.with(holder.itemView.getContext())
                .load(post.getUrlAuthorPhoto())
                .into(holder.imageViewPhotoAuthor);
    }

    public interface onClickComment {

        void onClickComment(Post post);
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
        private final View viewLine;
        private final TextView textViewCountComments;
        private final TextView textViewComment;
        private final TextView textViewDataRelease;
        private ImageView imageViewPoster;
        private final ImageView imageViewComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAnotherComment = itemView.findViewById(R.id.textViewAnotherComment);
            textViewTitle = itemView.findViewById(R.id.textViewTitleDetail);
            textViewArticle = itemView.findViewById(R.id.textViewArticleDetail);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthorDetail);
            viewLine = itemView.findViewById(R.id.viewLine);
            textViewCountComments = itemView.findViewById(R.id.textViewCountComments);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            imageViewPhotoAuthor = itemView.findViewById(R.id.imageViewPhotoAuthorDetail);
            textViewDataRelease = itemView.findViewById(R.id.textViewDataReleaseDetail);
            imageViewPoster = itemView.findViewById(R.id.imageViewPosterDetail);
            imageViewComment = itemView.findViewById(R.id.imageViewComment);
        }
    }
}
