package me.lazy_assedninja.app.vo;

import androidx.room.Ignore;

public class GoogleAccount {

    private String googleID;
    private String email;
    private String name;
    private String pictureURL;

    private int userID;

    public GoogleAccount() {
    }

    @Ignore
    public GoogleAccount(String googleID, String email, String name, String pictureURL) {
        this.googleID = googleID;
        this.email = email;
        this.name = name;
        this.pictureURL = pictureURL;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}