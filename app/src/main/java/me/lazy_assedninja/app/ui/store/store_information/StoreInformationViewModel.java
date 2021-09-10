package me.lazy_assedninja.app.ui.store.store_information;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.FavoriteRepository;
import me.lazy_assedninja.app.repository.HistoryRepository;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;

@HiltViewModel
public class StoreInformationViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final HistoryRepository historyRepository;
    private FavoriteRepository favoriteRepository;

    public LiveData<Store> store;
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    @Inject
    public StoreInformationViewModel(UserRepository userRepository, StoreRepository storeRepository,
                                     FavoriteRepository favoriteRepository, HistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.favoriteRepository = favoriteRepository;
        this.historyRepository = historyRepository;
    }

    public boolean isLoggedIn() {
        return getUserID() == 0;
    }

    public int getUserID() {
        return userRepository.getUserID();
    }

    public LiveData<Store> getStore(int id) {
        if (store == null) {
            store = storeRepository.getStoreFromDb(id);
        }
        return store;
    }

    public LiveData<Resource<Result>> result = Transformations.switchMap(favoriteRequest, favorite -> {
        if (favorite == null) {
            return AbsentLiveData.create();
        } else {
            return favoriteRepository.changeFavoriteStatus(favorite);
        }
    });

    public void setFavoriteRequest() {
        if (store.getValue() == null) return;

        int storeID = store.getValue().getId();
        boolean isFavorite = store.getValue().isFavorite();
        Favorite favorite = favoriteRequest.getValue();
        if (favorite == null || favorite.getStatus() == isFavorite) {
            favoriteRequest.setValue(new Favorite(getUserID(), storeID, !isFavorite));
        }
    }

    public void addHistory(int storeID) {
        historyRepository.addHistory(storeID);
    }
}