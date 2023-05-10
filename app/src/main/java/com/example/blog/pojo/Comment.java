package com.example.blog.pojo;

import com.example.blog.pojo.Post;
import com.example.blog.pojo.User;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {

    private String idComment;
    private String userId;
    private String comment;
    private String nickname;
    private String postId;
    private String urlAuthorPhoto;
    private String timestamp;
    private User user;


    public Comment(String idComment, String userId, String comment, String nickname, String postId, String urlAuthorPhoto, String timestamp, User user) {
        this.idComment = idComment;
        this.userId = userId;
        this.comment = comment;
        this.nickname = nickname;
        this.postId = postId;
        this.urlAuthorPhoto = urlAuthorPhoto;
        this.timestamp = timestamp;
        this.user = user;
    }

    public Comment() {
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

    public User getUser() {
        return user;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUrlAuthorPhoto() {
        return urlAuthorPhoto;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getIdComment() {
        return idComment;
    }

    public String getComment() {
        return comment;
    }

    public String getData() {
        return timestamp;
    }
}
