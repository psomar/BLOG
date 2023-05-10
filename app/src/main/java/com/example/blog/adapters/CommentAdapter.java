package com.example.blog.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blog.R;
import com.example.blog.pojo.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {


    private List<Comment> comments = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public List<Comment> getComments() {
        return comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,
                parent,
                false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.textViewAuthorComment.setText(comment.getNickname());
        holder.textViewComment.setText(comment.getComment());
        holder.textViewDataComment.setText(comment.getData());
        Glide.with(holder.itemView.getContext())
                .load(comment.getUrlAuthorPhoto())
                .into(holder.imageViewAuthorComment);
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewAuthorComment;
        private final TextView textViewComment;
        private final TextView textViewDataComment;
        private final ImageView imageViewAuthorComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthorComment = itemView.findViewById(R.id.textViewAuthorComment);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewDataComment = itemView.findViewById(R.id.textViewDataComment);
            imageViewAuthorComment = itemView.findViewById(R.id.imageViewAuthorComment);
        }
    }
}
