package com.example.blog;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

public class Post {

    private String id;
    private String title;
    private String article;
    private double dataRelease;
    private int like;
    private int dislike;
    private int urlAuthorPhoto;
    private User user;


    public Post(String id, String title, String article, double dataRelease, int like, int dislike, int urlAuthorPhoto, User user) {
        this.id = id;
        this.title = title;
        this.article = article;
        this.dataRelease = dataRelease;
        this.like = like;
        this.dislike = dislike;
        this.urlAuthorPhoto = urlAuthorPhoto;
        this.user = user;
    }

    public Post() {
    }


    public User getUser() {
        return user;
    }

    public String getId() {
        return id;
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

    public double getDataRelease() {
        return dataRelease;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }

}
