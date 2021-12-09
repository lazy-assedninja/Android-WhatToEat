package me.lazy_assedninja.what_to_eat.dto;

public class CommentDTO {

    private int storeID;

    public CommentDTO(int storeID) {
        this.storeID = storeID;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }
}