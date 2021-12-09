package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.ReservationDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.ReservationDTO;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.NetworkUtil;

public class ReservationRepository {

    private final ExecutorUtil executorUtil;
    private final NetworkUtil networkUtil;
    private final WhatToEatDatabase db;
    private final ReservationDao reservationDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public ReservationRepository(ExecutorUtil executorUtil, NetworkUtil networkUtil,
                                 WhatToEatDatabase db, ReservationDao reservationDao,
                                 WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.networkUtil = networkUtil;
        this.db = db;
        this.reservationDao = reservationDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Reservation>>> loadReservations(ReservationDTO reservationDTO) {
        return new NetworkBoundResource<List<Reservation>, List<Reservation>>(executorUtil) {

            @Override
            protected LiveData<List<Reservation>> loadFromDb() {
                return reservationDao.getReservations();
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Reservation> data) {
                return data == null || data.isEmpty() || networkUtil.isConnected();
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

    public LiveData<Event<Resource<Result>>> createOrCancelReservation(boolean isCreate,
                                                                       Reservation reservation) {
        return new NetworkResource<Result>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return (isCreate) ? whatToEatService.createReservation(reservation) :
                        whatToEatService.cancelReservation(reservation);
            }

            @Override
            protected void saveCallResult(Result item) {
                if (isCreate) {
                    reservationDao.insert(reservation);
                } else {
                    reservationDao.delete(reservation);
                }
            }
        }.asLiveData();
    }
}