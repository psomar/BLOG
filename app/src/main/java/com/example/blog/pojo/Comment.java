package com.example.blog.pojo;

import java.io.Serializable;

public class Comment implements Serializable {

    private String idComment;
    private String userId;
    private String comment;
    private String nickname;
    private String postId;
    private String authorPhoto;
    private String timestamp;


    public Comment(String idComment, String userId, String comment, String nickname, String postId, String urlAuthorPhoto, String timestamp) {
        this.idComment = idComment;
        this.userId = userId;
        this.comment = comment;
        this.nickname = nickname;
        this.postId = postId;
        this.authorPhoto = urlAuthorPhoto;
        this.timestamp = timestamp;
    }

    public Comment() {
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAuthorPhoto() {
        return authorPhoto;
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
