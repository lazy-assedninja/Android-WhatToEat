package me.lazy_assedninja.app.ui.index;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.HistoryRepository;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.vo.User;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final HistoryRepository historyRepository;

    @Inject
    public MainViewModel(UserRepository userRepository, StoreRepository storeRepository,
                         HistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.historyRepository = historyRepository;
    }

    public boolean isLoggedIn() {
        return getUserID() == 0;
    }

    public int getUserID() {
        return userRepository.getUserID();
    }

    public LiveData<User> getUser() {
        return userRepository.getUserFromDb();
    }

    public void clearHistory() {
        historyRepository.deleteAll();
    }

    public void initTags() {
        storeRepository.initTags();
    }
}