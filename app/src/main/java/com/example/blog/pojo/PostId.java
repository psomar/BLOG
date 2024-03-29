package com.example.blog.pojo;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class PostId {

    @Exclude
    public String postId;

    public <T extends PostId> T withId(@NonNull final String id) {
        this.postId = id;
        return (T) this;
    }
}
