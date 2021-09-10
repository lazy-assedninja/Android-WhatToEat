package me.lazy_assedninja.app.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class UserRepository {

    private static final String PREFERENCES_USER_ID = "preferences_user_id";
    private static final String USER_ID = "user_id";

    private final Context context;
    private final ExecutorUtils executorUtils;
    private final UserDao userDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public UserRepository(Context context, ExecutorUtils executorUtils,
                          UserDao userDao, WhatToEatService whatToEatService) {
        this.context = context;
        this.executorUtils = executorUtils;
        this.userDao = userDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<User>> loadUser(UserDTO userDTO) {
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
                return whatToEatService.login(userDTO);
            }

            @Override
            protected void saveCallResult(User item) {
                userDao.insert(item);
            }
        }.asLiveData();
    }

    public void setUserID(int value) {
        context.getSharedPreferences(PREFERENCES_USER_ID, Context.MODE_PRIVATE)
                .edit()
                .putInt(USER_ID, value)
                .apply();
    }

    public int getUserID() {
        return context.getSharedPreferences(PREFERENCES_USER_ID, Context.MODE_PRIVATE)
                .getInt(USER_ID, 0);
    }

    public LiveData<User> getUserFromDb() {
        return userDao.get();
    }

    public void deleteUser() {
        executorUtils.diskIO().execute(userDao::delete);
    }
}