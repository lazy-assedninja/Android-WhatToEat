package me.lazy_assedninja.app.ui.index;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import me.lazy_assedninja.app.api.RetrofitManager;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.repository.HistoryRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class MainViewModel extends AndroidViewModel {

    private final ExecutorUtils executorUtils = new ExecutorUtils();
    private final UserRepository userRepository = new UserRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).userDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );
    private final HistoryRepository historyRepository = new HistoryRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).historyDao()
    );

    public MainViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public int getLoggedInUserID() {
        return userRepository.isLoggedIn(getApplication());
    }

    public LiveData<User> getUser() {
        return userRepository.getUserFormDb();
    }

    public void clearHistory() {
        historyRepository.deleteAll();
    }
}