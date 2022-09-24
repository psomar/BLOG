package com.example.blog.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Post extends PostId implements Serializable {


    private String postId;
    private String userId;
    private String title;
    private String article;
    private String nickname;
    private String otherNick;
    @ServerTimestamp
    private String timestamp;
    private int like;
    private int dislike;
    private int urlImage;
    private int urlAuthorPhoto;


    public Post(String postId, String userId, String title, String article, String nickname, String otherNick, String timestamp, int like, int dislike, int urlImage, int urlAuthorPhoto) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.otherNick = otherNick;
        this.article = article;
        this.timestamp = timestamp;
        this.like = like;
        this.dislike = dislike;
        this.urlImage = urlImage;
        this.urlAuthorPhoto = urlAuthorPhoto;
        this.nickname = nickname;

    }

    public Post() {
    }

    public String getOtherNick() {
        return otherNick;
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

    public void setLike(int like) {
        this.like = like;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public void setUrlImage(int urlImage) {
        this.urlImage = urlImage;
    }

    public void setUrlAuthorPhoto(int urlAuthorPhoto) {
        this.urlAuthorPhoto = urlAuthorPhoto;
    }

    public String getPostId() {
        return postId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPotsId() {
        return postId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getUrlImage() {
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

    public int getUrlAuthorPhoto() {
        return urlAuthorPhoto;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }

}
