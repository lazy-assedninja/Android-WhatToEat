package me.lazy_assedninja.what_to_eat.ui.store.history;

import static java.util.Collections.emptyList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.HistoryRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@HiltViewModel
public class HistoryViewModel extends ViewModel {

    private final UserRepository userRepository;
    private FavoriteRepository favoriteRepository;
    private HistoryRepository historyRepository;

    private final MutableLiveData<List<Integer>> storeRequest = new MutableLiveData<>();
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    private List<Store> list = emptyList();

    @Inject
    public HistoryViewModel(UserRepository userRepository, FavoriteRepository favoriteRepository,
                            HistoryRepository historyRepository) {
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

    public void requestHistory(List<Integer> ids) {
        storeRequest.setValue(ids);
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

    public LiveData<List<Integer>> getHistoryIDs() {
        return historyRepository.getHistoryIDs();
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