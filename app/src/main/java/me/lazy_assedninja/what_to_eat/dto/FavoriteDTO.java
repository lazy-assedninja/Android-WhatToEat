package me.lazy_assedninja.what_to_eat.dto;

public class FavoriteDTO {

    private int storeID;
    private int userID;

    public FavoriteDTO() {
    }

    public FavoriteDTO(int storeID) {
        this.storeID = storeID;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}