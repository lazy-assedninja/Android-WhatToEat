package me.lazy_assedninja.app.ui.reservation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import me.lazy_assedninja.app.api.RetrofitManager;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.ReservationRequest;
import me.lazy_assedninja.app.repository.ReservationRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class ReservationViewModel extends AndroidViewModel {

    private final ExecutorUtils executorUtils = new ExecutorUtils();
    private final UserRepository userRepository = new UserRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).userDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );
    private final ReservationRepository reservationRepository = new ReservationRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).reservationDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );

    private final MutableLiveData<ReservationRequest> reservationRequest = new MutableLiveData<>();

    public ReservationViewModel(@NonNull Application application) {
        super(application);
    }

    public int getLoggedInUserID() {
        return userRepository.isLoggedIn(getApplication());
    }

    public final LiveData<Resource<List<Reservation>>> reservations = Transformations.switchMap(reservationRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return reservationRepository.loadReservations(request);
        }
    });

    public void setReservationRequest(ReservationRequest request) {
        if (reservationRequest.getValue() != request) {
            reservationRequest.setValue(request);
        }
    }

    public void cancelReservation(Reservation reservation) {
        reservationRepository.cancelReservation(reservation);
    }
}