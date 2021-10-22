package me.lazy_assedninja.app.vo;

public class Report {

    private String content;
    private String createTime;

    private Integer storeID;
    private int userID;

    public Report(String content, String createTime, Integer storeID, int userID) {
        this.content = content;
        this.createTime = createTime;
        this.storeID = storeID;
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getStoreID() {
        return storeID;
    }

    public void setStoreID(Integer storeID) {
        this.storeID = storeID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}