package me.lazy_assedninja.app.dto;

public class StoreDTO {

    private int userID;
    private int tagID;
    private String keyword;

    public StoreDTO(int userID) {
        this.userID = userID;
    }

    public StoreDTO(int userID, int tagID) {
        this.userID = userID;
        this.tagID = tagID;
    }

    public StoreDTO(int userID, String keyword) {
        this.userID = userID;
        this.keyword = keyword;
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