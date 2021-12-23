package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.ReservationDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.ReservationDTO;
import me.lazy_assedninja.what_to_eat.util.RateLimiter;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;

/**
 * Repository that handles Reservation objects.
 */
public class  ReservationRepository {

    private final ExecutorUtil executorUtil;
    private final WhatToEatDatabase db;
    private final ReservationDao reservationDao;
    private final WhatToEatService whatToEatService;

    private final RateLimiter<ReservationDTO> rateLimiter = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public ReservationRepository(ExecutorUtil executorUtil, WhatToEatDatabase db,
                                 ReservationDao reservationDao, WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
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
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(reservationDTO);
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

            @Override
            protected void onFetchFailed() {
                rateLimiter.reset(reservationDTO);
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> createOrCancelReservation(boolean isCreate,
                                                                       Reservation reservation) {
        return new NetworkResource<Result, Void>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return (isCreate) ? whatToEatService.createReservation(reservation) :
                        whatToEatService.cancelReservation(reservation);
            }

            @Override
            protected Void saveCallResult(Result item) {
                if (!isCreate) reservationDao.delete(reservation);
                return null;
            }
        }.asLiveData();
    }
}