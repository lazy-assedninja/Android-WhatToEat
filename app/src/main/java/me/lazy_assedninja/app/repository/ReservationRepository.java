package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.ReservationDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.ReservationDTO;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.NetworkUtils;

public class ReservationRepository {

    private final ExecutorUtils executorUtils;
    private final NetworkUtils networkUtils;
    private final WhatToEatDatabase db;
    private final ReservationDao reservationDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public ReservationRepository(ExecutorUtils executorUtils, NetworkUtils networkUtils,
                                 WhatToEatDatabase db, ReservationDao reservationDao,
                                 WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.networkUtils = networkUtils;
        this.db = db;
        this.reservationDao = reservationDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Reservation>>> loadReservations(ReservationDTO reservationDTO) {
        return new NetworkBoundResource<List<Reservation>, List<Reservation>>(executorUtils) {

            @Override
            protected LiveData<List<Reservation>> loadFromDb() {
                return reservationDao.getReservations();
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Reservation> data) {
                return data == null || data.isEmpty() || networkUtils.isConnected();
            }

            @Override
            protected LiveData<ApiResponse<List<Reservation>>> createCall() {
                return whatToEatService.getReservationList(reservationDTO);
            }

            @Override
            protected void saveCallResult(List<Reservation> item) {
                db.runInTransaction(() -> {
                    reservationDao.delete();
                    reservationDao.insertAll(item);
                });
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> addOrCancelReservation(boolean isAdd, Reservation reservation) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return (isAdd) ? whatToEatService.createReservation(reservation) :
                        whatToEatService.cancelReservation(new ReservationDTO(reservation.getId()));
            }

            @Override
            protected void saveCallResult(Result item) {
                if (isAdd) {
                    reservationDao.insert(reservation);
                } else {
                    reservationDao.delete(reservation);
                }
            }
        }.asLiveData();
    }
}