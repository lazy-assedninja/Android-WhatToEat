package me.lazy_assedninja.what_to_eat.ui.store.reservation;

import me.lazy_assedninja.what_to_eat.vo.Reservation;

public interface ReservationCallback {

    void onCompleteClick(Reservation reservation);
}