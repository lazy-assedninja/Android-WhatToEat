package me.lazy_assedninja.what_to_eat.ui.store.reservation.reserve;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.repository.ReservationRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;

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