package me.lazy_assedninja.what_to_eat.ui.store.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.repository.StoreRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@HiltViewModel
public class MapViewModel extends ViewModel {

    private final UserRepository userRepository;
    private StoreRepository storeRepository;

    private final MutableLiveData<StoreDTO> storeRequest = new MutableLiveData<>();

    @Inject
    public MapViewModel(UserRepository userRepository, StoreRepository storeRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    public LiveData<Resource<List<Store>>> stores = Transformations.switchMap(storeRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return storeRepository.loadAllStores(request);
        }
    });

    public void requestStore(StoreDTO storeDTO) {
        if (storeRequest.getValue() == null){
            storeDTO.setUserID(userRepository.getUserID());
            storeRequest.setValue(storeDTO);
        }
    }
}