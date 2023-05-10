package com.example.blog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blog.BuildConfig;
import com.example.blog.R;
import com.example.blog.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<User> users = new ArrayList<>();

    public void setProfiles(List<User> profiles) {
        this.users = profiles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item,
                parent,
                false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        User user = users.get(position);
        holder.textViewName.setText(user.getNickname());
        holder.textViewEmail.setText(user.getEmail());
        if (user.getProfileImage() == null) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.imageViewProfileCircle);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfileImage())
                    .into(holder.imageViewProfileCircle);
        }
        holder.textViewVersionNumber.setText(BuildConfig.VERSION_NAME);
        holder.textViewPostSize.setText(String.valueOf(user.getMyPost()));
        holder.textViewFavouriteSize.setText(String.valueOf(user.getFavouritePost()));
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewProfileCircle;
        private final TextView textViewName;
        private final TextView textViewEmail;
        private final TextView textViewFavouriteSize;
        private final TextView textViewVersionNumber;
        private final TextView textViewPostSize;


        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfileCircle = itemView.findViewById(R.id.imageViewProfileCircle);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewFavouriteSize = itemView.findViewById(R.id.textViewFavouriteSize);
            textViewVersionNumber = itemView.findViewById(R.id.textViewVersionNumber);
            textViewPostSize = itemView.findViewById(R.id.textViewPostSize);

        }
    }
}
