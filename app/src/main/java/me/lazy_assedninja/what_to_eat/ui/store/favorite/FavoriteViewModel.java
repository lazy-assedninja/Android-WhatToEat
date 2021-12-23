package me.lazy_assedninja.what_to_eat.ui.store.favorite;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@HiltViewModel
public class FavoriteViewModel extends ViewModel {

    private final UserRepository userRepository;
    private FavoriteRepository favoriteRepository;

    private final MutableLiveData<FavoriteDTO> storeRequest = new MutableLiveData<>();
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    @Inject
    public FavoriteViewModel(UserRepository userRepository, FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public boolean isLoggedIn() {
        return userRepository.getUserID() == 0;
    }

    public LiveData<Resource<List<Store>>> stores = Transformations.switchMap(storeRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return favoriteRepository.loadFavorites(request);
        }
    });

    public void requestStore(FavoriteDTO favoriteDTO) {
        if (storeRequest.getValue() == null || storeRequest.getValue().getUserID() !=
                userRepository.getUserID()) {
            favoriteDTO.setUserID(userRepository.getUserID());
            storeRequest.setValue(favoriteDTO);
        }
    }

    public void refresh() {
        if (storeRequest.getValue() != null) {
            storeRequest.setValue(storeRequest.getValue());
        }
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