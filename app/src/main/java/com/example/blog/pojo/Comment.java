package com.example.blog.pojo;

import com.example.blog.pojo.Post;
import com.example.blog.pojo.User;

import java.util.Date;

public class Comment {

    private String idComment;
    private String comment;
    private Date timestamp;
    private Post post;
    private User user;


    public Comment(String idComment, String comment, Date timestamp, Post post, User user) {
        this.idComment = idComment;
        this.comment = comment;
        this.timestamp = timestamp;
        this.post = post;
        this.user = user;
    }

    public String getIdComment() {
        return idComment;
    }

    public String getComment() {
        return comment;
    }

    public Date getData() {
        return timestamp;
    }

    public Post getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }
}
