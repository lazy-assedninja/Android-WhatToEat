package me.lazy_assedninja.app.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.dto.UserRequest;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class UserRepository {

    private static final String PREFERENCES_LOGGED_IN = "logged_in";
    private static final String LOGGED_IN_USER_ID = "logged_in_user_id";

    private final ExecutorUtils executorUtils;
    private final UserDao userDao;
    private final WhatToEatService whatToEatService;

    public UserRepository(ExecutorUtils executorUtils, UserDao userDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.userDao = userDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<User>> login(UserRequest userRequest) {
        return new NetworkBoundResource<User, User>(executorUtils) {

            @Override
            protected LiveData<User> loadFromDb() {
                return userDao.get();
            }

            @Override
            protected Boolean shouldFetch(@Nullable User data) {
                return data == null;
            }

            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                return whatToEatService.login(userRequest);
            }

            @Override
            protected void saveCallResult(User item) {
                userDao.insert(item);
            }
        }.asLiveData();
    }

    public void setLoggedIn(Context context, int value) {
        context.getSharedPreferences(PREFERENCES_LOGGED_IN, Context.MODE_PRIVATE)
                .edit()
                .putInt(LOGGED_IN_USER_ID, value)
                .apply();
    }

    public int isLoggedIn(Context context) {
        return context.getSharedPreferences(PREFERENCES_LOGGED_IN, Context.MODE_PRIVATE)
                .getInt(LOGGED_IN_USER_ID, 0);
    }

    public LiveData<User> getUserFormDb() {
        return userDao.get();
    }

    public void deleteUser() {
        userDao.delete();
    }
}