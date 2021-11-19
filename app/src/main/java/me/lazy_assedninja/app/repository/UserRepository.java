package me.lazy_assedninja.app.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.GoogleAccount;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.utils.EncryptUtils;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.TimeUtils;

public class UserRepository {

    private static final String PREFERENCES_USER = "preferences_user";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";

    private final Context context;
    private final ExecutorUtils executorUtils;
    private final EncryptUtils encryptUtils;
    private final TimeUtils timeUtils;
    private final UserDao userDao;
    private final WhatToEatService whatToEatService;

    public UserRepository(Context context, ExecutorUtils executorUtils, EncryptUtils encryptUtils,
                          TimeUtils timeUtils, UserDao userDao, WhatToEatService whatToEatService) {
        this.context = context;
        this.executorUtils = executorUtils;
        this.encryptUtils = encryptUtils;
        this.timeUtils = timeUtils;
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
                if (userDTO.isGoogleLogin()) {
                    return whatToEatService.googleLogin(userDTO);
                } else {
                    String password = userDTO.getPassword();
                    userDTO.setPassword(encryptUtils.sha256(password));
                    return whatToEatService.login(userDTO);
                }
            }

            @Override
            protected void saveCallResult(User item) {
                userDao.insert(item);
            }
        }.asLiveData();
    }

    public void setUserID(int value) {
        context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE)
                .edit()
                .putInt(USER_ID, value)
                .apply();
    }

    public int getUserID() {
        return context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE)
                .getInt(USER_ID, 0);
    }

    public void setUserEmail(String value) {
        context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE)
                .edit()
                .putString(USER_EMAIL, value)
                .apply();
    }

    public String getUserEmail() {
        return context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE)
                .getString(USER_EMAIL, "");
    }

    public LiveData<User> getUserFromDb() {
        return userDao.get();
    }

    public void deleteUser() {
        executorUtils.diskIO().execute(userDao::delete);
    }

    public LiveData<Event<Resource<Result>>> register(User user) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                String password = user.getPassword();
                user.setPassword(encryptUtils.sha256(password));
                return whatToEatService.register(user);
            }

            @Override
            protected void saveCallResult(Result item) {
                setUserEmail(user.getEmail());
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> bindGoogleAccount(GoogleAccount googleAccount) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.bindGoogleAccount(googleAccount);
            }

            @Override
            protected void saveCallResult(Result item) {
                userDao.updateGoogleID(googleAccount.getGoogleID(), timeUtils.now());
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> resetPassword(UserDTO userDTO) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                String oldPassword = userDTO.getOldPassword();
                userDTO.setOldPassword(encryptUtils.sha256(oldPassword));
                String newPassword = userDTO.getNewPassword();
                userDTO.setNewPassword(encryptUtils.sha256(newPassword));
                return whatToEatService.resetPassword(userDTO);
            }

            @Override
            protected void saveCallResult(Result item) {
                userDao.updatePassword(userDTO.getNewPassword(), timeUtils.now());
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> sendVerificationCode(UserDTO userDTO) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.sendVerificationCode(userDTO);
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> forgetPassword(UserDTO userDTO) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                String password = userDTO.getNewPassword();
                userDTO.setNewPassword(encryptUtils.sha256(password));
                return whatToEatService.forgetPassword(userDTO);
            }

            @Override
            protected void saveCallResult(Result item) {
                userDao.updatePassword(userDTO.getNewPassword(), timeUtils.now());
            }
        }.asLiveData();
    }
}