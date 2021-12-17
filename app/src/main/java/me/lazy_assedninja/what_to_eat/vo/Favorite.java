package me.lazy_assedninja.what_to_eat.vo;

public class Favorite {

    private int id;

    private int userID;
    private int storeID;

    private boolean status;

    private int position;
    private boolean needUpdate;

    public Favorite() {
    }

    public Favorite(int storeID, boolean status) {
        this.storeID = storeID;
        this.status = status;
    }

    public Favorite(int storeID, boolean status, int position) {
        this.storeID = storeID;
        this.status = status;
        this.position = position;
    }

    public Favorite(int storeID, boolean status, int position, boolean needUpdate) {
        this.storeID = storeID;
        this.status = status;
        this.position = position;
        this.needUpdate = needUpdate;
    }

    public void setInformation(int userID, int storeID, boolean needUpdate){
        this.userID = userID;
        this.storeID = storeID;
        this.needUpdate = needUpdate;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }
}