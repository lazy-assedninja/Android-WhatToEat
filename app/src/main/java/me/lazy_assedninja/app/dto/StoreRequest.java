package me.lazy_assedninja.app.dto;

public class StoreRequest {
    private int userID;
    private int tagID;
    private String keyword;

    public StoreRequest() {
    }

    public StoreRequest(int userID, int tagID) {
        this.userID = userID;
        this.tagID = tagID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}