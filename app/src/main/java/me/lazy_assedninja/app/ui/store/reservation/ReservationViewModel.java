package me.lazy_assedninja.app.ui.store.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.ReservationDTO;
import me.lazy_assedninja.app.repository.Event;
import me.lazy_assedninja.app.repository.ReservationRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@HiltViewModel
public class ReservationViewModel extends ViewModel {

    private final UserRepository userRepository;
    private ReservationRepository reservationRepository;

    private final MutableLiveData<ReservationDTO> reservationRequest = new MutableLiveData<>();
    private final MutableLiveData<Reservation> cancelRequest = new MutableLiveData<>();

    @Inject
    public ReservationViewModel(UserRepository userRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    public int getUserID() {
        return userRepository.getUserID();
    }

    public final LiveData<Resource<List<Reservation>>> reservations = Transformations.switchMap(reservationRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return reservationRepository.loadReservations(request);
        }
    });

    public void requestReservation() {
        if (reservationRequest.getValue() == null || reservationRequest.getValue().getId() != getUserID()) {
            reservationRequest.setValue(new ReservationDTO("user", getUserID()));
        }
    }

    public void refresh() {
        if (reservationRequest.getValue() != null) {
            reservationRequest.setValue(reservationRequest.getValue());
        }
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(cancelRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return reservationRepository.addOrCancelReservation(false, request);
        }
    });

    public void setCancelRequest(Reservation reservation) {
        if (cancelRequest.getValue() == null || cancelRequest.getValue().getId() != reservation.getId()) {
            cancelRequest.setValue(reservation);
        }
    }
}