package me.lazy_assedninja.app.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.jetbrains.annotations.NotNull;

import me.lazy_assedninja.app.api.RetrofitManager;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.UserRequest;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class LoginViewModel extends AndroidViewModel {

    private final UserRepository userRepository = new UserRepository(
            new ExecutorUtils(),
            WhatToEatDatabase.getInstance(getApplication()).userDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );

    private final MutableLiveData<UserRequest> login = new MutableLiveData<>();

    public LoginViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public LiveData<Resource<User>> user = Transformations.switchMap(login, userRequest -> {
        if (userRequest == null) {
            return AbsentLiveData.create();
        } else {
            return userRepository.login(userRequest);
        }
    });

    public void setLogin(UserRequest userRequest) {
        if (login.getValue() != userRequest) {
            login.setValue(userRequest);
        }
    }

    public void setLoggedIn(int LoggedInUserID) {
        userRepository.setLoggedIn(getApplication(), LoggedInUserID);
    }
}