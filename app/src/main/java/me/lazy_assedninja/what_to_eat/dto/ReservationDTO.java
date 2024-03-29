package me.lazy_assedninja.what_to_eat.dto;

public class ReservationDTO {

    private String type;
    private int id;

    public ReservationDTO(int id) {
        this.id = id;
    }

    public ReservationDTO(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}