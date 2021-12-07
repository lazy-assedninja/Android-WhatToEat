package me.lazy_assedninja.app.vo;

public enum Tag {
    HOME(1),
    RECOMMEND(2);

    private final int value;

    Tag(int value) {
        this.value=value;
    }

    public  int getValue(){
        return  value;
    }
}