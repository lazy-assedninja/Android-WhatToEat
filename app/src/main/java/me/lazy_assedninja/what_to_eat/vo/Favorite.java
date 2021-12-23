package me.lazy_assedninja.what_to_eat.vo;

public class Favorite {

    private int id;

    private int userID;
    private int storeID;

    private boolean status;
    private String result;

    private boolean needUpdateTime;

    public Favorite() {
    }

    public Favorite(int storeID, boolean status) {
        this.storeID = storeID;
        this.status = status;
    }

    public Favorite(int storeID, boolean status, boolean needUpdateTime) {
        this.storeID = storeID;
        this.status = status;
        this.needUpdateTime = needUpdateTime;
    }

    public void setInformation(int userID, int storeID, boolean needUpdateTime){
        this.userID = userID;
        this.storeID = storeID;
        this.needUpdateTime = needUpdateTime;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setNeedUpdateTime(boolean needUpdateTime) {
        this.needUpdateTime = needUpdateTime;
    }

    public boolean isNeedUpdateTime() {
        return needUpdateTime;
    }
}