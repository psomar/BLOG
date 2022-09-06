package com.example.blog;

import java.util.ArrayList;
import java.util.List;

public class BlogResponse {

    private List<Post> posts = new ArrayList<>();

    public BlogResponse(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    @Override
    public String toString() {
        return "BlogResponse{" +
                "posts=" + posts +
                '}';
    }
}
