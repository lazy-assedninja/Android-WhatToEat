package me.lazy_assedninja.app.vo;

public class Favorite {

    private int id;

    private int userID;
    private int storeID;

    private boolean status;

    public Favorite() {
    }

    public Favorite(int storeID, boolean status) {
        this.storeID = storeID;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}