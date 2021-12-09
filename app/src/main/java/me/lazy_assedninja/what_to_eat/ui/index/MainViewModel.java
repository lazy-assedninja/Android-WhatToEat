package me.lazy_assedninja.what_to_eat.ui.index;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.repository.HistoryRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.vo.User;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    @Inject
    public MainViewModel(UserRepository userRepository, HistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    public boolean isLoggedIn() {
        return userRepository.getUserID() == 0;
    }

    public LiveData<User> getUser() {
        return userRepository.getUserFromDb();
    }

    public void clearHistory() {
        historyRepository.deleteAll();
    }
}