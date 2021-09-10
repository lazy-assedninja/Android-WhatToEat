package me.lazy_assedninja.app.ui.store.reservation;

import me.lazy_assedninja.app.vo.Reservation;

public interface ReservationCallback {

    void onCompleteClick(Reservation reservation);
}