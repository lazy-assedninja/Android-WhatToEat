package me.lazy_assedninja.app.ui.store.store_information;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.repository.FavoriteRepository;
import me.lazy_assedninja.app.repository.HistoryRepository;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.util.AbsentLiveData;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.History;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;

@HiltViewModel
public class StoreInformationViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final HistoryRepository historyRepository;
    private FavoriteRepository favoriteRepository;

    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    private int id;
    public LiveData<Store> store;

    @Inject
    public StoreInformationViewModel(UserRepository userRepository, StoreRepository storeRepository,
                                     FavoriteRepository favoriteRepository, HistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.favoriteRepository = favoriteRepository;
        this.historyRepository = historyRepository;
    }

    public boolean isLoggedIn() {
        return userRepository.getUserID() == 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LiveData<Store> getStore(int id) {
        if (store == null) {
            store = storeRepository.getStoreFromDb(id);
        }
        return store;
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(favoriteRequest, favorite -> {
        if (favorite == null) {
            return AbsentLiveData.create();
        } else {
            return favoriteRepository.changeFavoriteStatus(favorite);
        }
    });

    public void changeFavoriteStatus(Favorite favorite) {
        if (store.getValue() == null) return;

        favorite.setStoreID(store.getValue().getId());
        favorite.setUserID(userRepository.getUserID());
        boolean isFavorite = store.getValue().isFavorite();
        favorite.setStatus(!isFavorite);
        Favorite request = favoriteRequest.getValue();
        if (request == null || request.getStatus() == isFavorite) {
            favoriteRequest.setValue(favorite);
        }
    }

    public void addToHistory(History history) {
        historyRepository.addToHistory(history);
    }
}