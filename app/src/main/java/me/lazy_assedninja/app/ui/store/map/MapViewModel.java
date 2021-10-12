package me.lazy_assedninja.app.ui.store.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;

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

    public int getUserID() {
        return userRepository.getUserID();
    }

    public LiveData<Resource<List<Store>>> stores = Transformations.switchMap(storeRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return storeRepository.loadAllStores(request);
        }
    });

    public void requestStore() {
        storeRequest.setValue(new StoreDTO(getUserID()));
    }
}