package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.ReservationDao;
import me.lazy_assedninja.app.dto.ReservationRequest;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class ReservationRepository {

    private final ExecutorUtils executorUtils;
    private final ReservationDao reservationDao;
    private final WhatToEatService whatToEatService;

    public ReservationRepository(ExecutorUtils executorUtils, ReservationDao reservationDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.reservationDao = reservationDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Reservation>>> loadReservations(ReservationRequest reservationRequest) {
        return new NetworkBoundResource<List<Reservation>, List<Reservation>>(executorUtils) {

            @Override
            protected LiveData<List<Reservation>> loadFromDb() {
                return reservationDao.getReservations();
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Reservation> data) {
                return data == null || data.isEmpty();
            }

            @Override
            protected LiveData<ApiResponse<List<Reservation>>> createCall() {
                return whatToEatService.getReservationList(reservationRequest);
            }

            @Override
            protected void saveCallResult(List<Reservation> item) {
                reservationDao.insertAll(item);
            }
        }.asLiveData();
    }

    public void addReservation(Reservation reservation) {
        reservationDao.insert(reservation);
    }

    public void cancelReservation(Reservation reservation) {
        reservationDao.delete(reservation);
    }
}