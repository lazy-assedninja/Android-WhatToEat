package me.lazy_assedninja.app.ui.store.reservation.reserve;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.ReservationRepository;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.util.AbsentLiveData;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@HiltViewModel
public class ReserveViewModel extends ViewModel {

    private final UserRepository userRepository;
    private ReservationRepository reservationRepository;

    private final MutableLiveData<Reservation> reserve = new MutableLiveData<>();

    private int id;

    @Inject
    public ReserveViewModel(UserRepository userRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(reserve, reservation -> {
        if (reservation == null) {
            return AbsentLiveData.create();
        } else {
            return reservationRepository.createOrCancelReservation(true, reservation);
        }
    });

    public void reserve(Reservation reservation) {
        reservation.setStoreID(id);
        reservation.setUserID(userRepository.getUserID());
        reserve.setValue(reservation);
    }
}