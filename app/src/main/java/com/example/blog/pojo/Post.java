package com.example.blog.pojo;

import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Post extends PostId implements Serializable {

    private String postId;
    private String userId;
    private String title;
    private String article;
    private String nickname;
    @ServerTimestamp
    private String timestamp;
    private String urlImage;
    private String urlAuthorPhoto;
    private int countComment;


    public Post(String postId, String userId, String title, String article, String nickname, String timestamp, String urlImage, String urlAuthorPhoto, int countComment) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.article = article;
        this.nickname = nickname;
        this.timestamp = timestamp;
        this.urlImage = urlImage;
        this.urlAuthorPhoto = urlAuthorPhoto;
        this.countComment = countComment;
    }

    public Post() {
    }


    public int getCountComment() {
        return countComment;
    }

    public void setCountComment(int countComment) {
        this.countComment = countComment;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlAuthorPhoto() {
        return urlAuthorPhoto;
    }

    public void setUrlAuthorPhoto(String urlAuthorPhoto) {
        this.urlAuthorPhoto = urlAuthorPhoto;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPostId() {
        return postId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getArticle() {
        return article;
    }

}
