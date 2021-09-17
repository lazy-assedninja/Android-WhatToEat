package me.lazy_assedninja.app.ui.store.reservation.reserve;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@HiltViewModel
public class ReserveViewModel extends ViewModel {

    private final UserRepository userRepository;
    private StoreRepository storeRepository;

    private final MutableLiveData<Reservation> reserve = new MutableLiveData<>();

    @Inject
    public ReserveViewModel(UserRepository userRepository, StoreRepository storeRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    public int getUserID() {
        return userRepository.getUserID();
    }

    public LiveData<Resource<Result>> result = Transformations.switchMap(reserve, reservation -> {
        if (reservation == null) {
            return AbsentLiveData.create();
        } else {
            return storeRepository.reserve(reservation);
        }
    });

    public void reserve(String name, String phone, String amount, String time, int storeID) {
        reserve.setValue(new Reservation(name, phone, amount, time, storeID, getUserID()));
    }
}