package me.lazy_assedninja.what_to_eat.ui.store.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.HistoryRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Store;
import me.lazy_assedninja.library.util.ExecutorUtil;

@HiltViewModel
public class HistoryViewModel extends ViewModel {

    private final ExecutorUtil executorUtil;
    private final UserRepository userRepository;
    private FavoriteRepository favoriteRepository;
    private HistoryRepository historyRepository;

    private final MutableLiveData<List<Integer>> storeRequest = new MutableLiveData<>();
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    @Inject
    public HistoryViewModel(ExecutorUtil executorUtil, UserRepository userRepository,
                            FavoriteRepository favoriteRepository, HistoryRepository historyRepository) {
        this.executorUtil = executorUtil;
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
            executorUtil.diskIO().execute(() ->
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