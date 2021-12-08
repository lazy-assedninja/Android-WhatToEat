package me.lazy_assedninja.app.ui.store.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.repository.FavoriteRepository;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.util.AbsentLiveData;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final UserRepository userRepository;
    private StoreRepository storeRepository;
    private FavoriteRepository favoriteRepository;

    private final MutableLiveData<StoreDTO> storeRequest = new MutableLiveData<>();
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    @Inject
    public HomeViewModel(UserRepository userRepository, StoreRepository storeRepository,
                         FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public boolean isLoggedIn() {
        return userRepository.getUserID() == 0;
    }

    public LiveData<Resource<List<Store>>> stores = Transformations.switchMap(storeRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return storeRepository.loadStores(request);
        }
    });

    public void requestStore(StoreDTO storeDTO) {
        if (storeRequest.getValue() == null|| storeRequest.getValue().getUserID() !=
                userRepository.getUserID()) {
            storeDTO.setUserID(userRepository.getUserID());
            storeRequest.setValue(storeDTO);
        }
    }

    public void refresh() {
        if (storeRequest.getValue() != null) {
            storeRequest.setValue(storeRequest.getValue());
        }
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(favoriteRequest, favorite -> {
        if (favorite == null) {
            return AbsentLiveData.create();
        } else {
            return favoriteRepository.changeFavoriteStatus(favorite);
        }
    });

    public void changeFavoriteStatus(Favorite favorite) {
        Favorite request = favoriteRequest.getValue();
        if (request == null || request.getStoreID() != favorite.getStoreID() ||
                (request.getStoreID() == favorite.getStoreID() &&
                        request.getStatus() != favorite.getStatus())) {
            favorite.setUserID(userRepository.getUserID());
            favoriteRequest.setValue(favorite);
        }
    }
}