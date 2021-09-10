package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiEmptyResponse;
import me.lazy_assedninja.app.api.ApiErrorResponse;
import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.ApiSuccessResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.ReservationDao;
import me.lazy_assedninja.app.dto.ReservationDTO;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import retrofit2.Response;

public class ReservationRepository {

    private final ExecutorUtils executorUtils;
    private final ReservationDao reservationDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public ReservationRepository(ExecutorUtils executorUtils, ReservationDao reservationDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
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
                return data == null || data.isEmpty();
            }

            @Override
            protected LiveData<ApiResponse<List<Reservation>>> createCall() {
                return whatToEatService.getReservationList(reservationDTO);
            }

            @Override
            protected void saveCallResult(List<Reservation> item) {
                reservationDao.insertAll(item);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> addOrCancelReservation(boolean isAdd, Reservation reservation) {
        MutableLiveData<Resource<Result>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(resource);
            try {
                Response<Result> response = (isAdd) ? whatToEatService.createReservation(reservation).execute() :
                        whatToEatService.cancelReservation(new ReservationDTO(reservation.getId())).execute();
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());

                    // Update Db data
                    executorUtils.diskIO().execute(() -> {
                        if (isAdd) {
                            reservationDao.insert(reservation);
                        } else {
                            reservationDao.delete(reservation);
                        }
                    });
                } else if (apiResponse instanceof ApiEmptyResponse) {
                    resource = Resource.error("No response.", null);
                } else if (apiResponse instanceof ApiErrorResponse) {
                    resource = Resource.error(
                            ((ApiErrorResponse<Result>) apiResponse).getErrorMessage(), null);
                } else {
                    resource = Resource.error("Unknown error.", null);
                }
            } catch (IOException e) {
                resource = Resource.error(e.getMessage(), null);
            }
            result.postValue(resource);
        });
        return result;
    }
}