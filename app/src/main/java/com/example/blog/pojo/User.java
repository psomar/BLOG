package com.example.blog.pojo;

import java.io.Serializable;

public class User implements Serializable {

    private String userId;
    private String nickname;
    private String email;
    private String password;
    private String profileImage;
    private int myPost = 0;
    private int favouritePost = 0;


    public User(String userId, String nickname, String email, String profileImage, int myPost, int favouritePost) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.myPost = myPost;
        this.favouritePost = favouritePost;
    }

    public User() {
    }

    public int getMyPost() {
        return myPost;
    }

    public void setMyPost(int myPost) {
        this.myPost = myPost;
    }

    public int getFavouritePost() {
        return favouritePost;
    }

    public void setFavouritePost(int favouritePost) {
        this.favouritePost = favouritePost;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setId(String userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
