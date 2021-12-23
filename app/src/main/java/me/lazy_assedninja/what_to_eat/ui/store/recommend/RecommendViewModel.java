package me.lazy_assedninja.what_to_eat.ui.store.recommend;

import static java.util.Collections.emptyList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.StoreRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@HiltViewModel
public class RecommendViewModel extends ViewModel {

    private final UserRepository userRepository;
    private StoreRepository storeRepository;
    private FavoriteRepository favoriteRepository;

    private final MutableLiveData<StoreDTO> storeRequest = new MutableLiveData<>();
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    private List<Store> list = emptyList();

    @Inject
    public RecommendViewModel(UserRepository userRepository, StoreRepository storeRepository,
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

    public void setList(List<Store> list) {
        this.list = list;
    }

    public int getStorePosition(int id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) return i;
        }
        return -1;
    }

    public LiveData<Event<Resource<Favorite>>> result =
            Transformations.switchMap(favoriteRequest, favorite -> {
                if (favorite == null) {
                    return AbsentLiveData.create();
                } else {
                    return favoriteRepository.changeFavoriteStatus(favorite);
                }
            });

    public void changeFavoriteStatus(Favorite favorite) {
        favorite.setUserID(userRepository.getUserID());
        favoriteRequest.setValue(favorite);
    }
}