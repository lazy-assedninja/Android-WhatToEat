package me.lazy_assedninja.app.ui.store.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.repository.FavoriteRepository;
import me.lazy_assedninja.app.repository.HistoryRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;

@HiltViewModel
public class HistoryViewModel extends ViewModel {

    private final ExecutorUtils executorUtils;
    private final UserRepository userRepository;
    private FavoriteRepository favoriteRepository;
    private HistoryRepository historyRepository;

    private final MutableLiveData<List<Integer>> storeRequest = new MutableLiveData<>();
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    @Inject
    public HistoryViewModel(ExecutorUtils executorUtils, UserRepository userRepository,
                            FavoriteRepository favoriteRepository, HistoryRepository historyRepository) {
        this.executorUtils = executorUtils;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.historyRepository = historyRepository;
    }

    public boolean isLoggedIn() {
        return userRepository.getUserID() == 0;
    }

    public LiveData<List<Store>> stores = Transformations.switchMap(storeRequest, ids -> {
        if (ids == null) {
            return AbsentLiveData.create();
        } else {
            return historyRepository.loadHistories(ids);
        }
    });

    public void requestHistory() {
        if (storeRequest.getValue() == null) {
            executorUtils.diskIO().execute(() ->
                    storeRequest.postValue(historyRepository.getHistoryIDs()));
        }
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(favoriteRequest, favorite -> {
        if (favorite == null) {
            return AbsentLiveData.create();
        } else {
            return favoriteRepository.changeFavoriteStatus(favorite);
        }
    });

    public void setFavoriteRequest(int storeID, boolean isFavorite) {
        Favorite favorite = favoriteRequest.getValue();
        if (favorite == null || favorite.getStoreID() != storeID ||
                (favorite.getStoreID() == storeID && favorite.getStatus() == isFavorite)) {
            favoriteRequest.setValue(new Favorite(userRepository.getUserID(), storeID, !isFavorite));
        }
    }
}