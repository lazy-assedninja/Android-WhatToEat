package me.lazy_assedninja.app.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import me.lazy_assedninja.app.api.RetrofitManager;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class ProfileViewModel extends AndroidViewModel {
    private final ExecutorUtils executorUtils = new ExecutorUtils();
    private final UserRepository userRepository = new UserRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).userDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );

    public ProfileViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public LiveData<User> getUser() {
        return userRepository.getUserFormDb();
    }

    public void logout() {
        userRepository.setLoggedIn(getApplication(), 0);
        executorUtils.diskIO().execute(userRepository::deleteUser);
    }
}