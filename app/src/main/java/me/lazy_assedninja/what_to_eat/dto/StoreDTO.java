package me.lazy_assedninja.what_to_eat.dto;

public class StoreDTO {

    private int userID;
    private int tagID;
    private String keyword;

    public StoreDTO() {
    }

    public StoreDTO(int tagID) {
        this.tagID = tagID;
    }

    public StoreDTO(String keyword) {
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