package me.lazy_assedninja.what_to_eat.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.UserDao;
import me.lazy_assedninja.what_to_eat.dto.UserDTO;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.GoogleAccount;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.User;
import me.lazy_assedninja.library.util.EncryptUtil;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.TimeUtil;

/**
 * Repository that handles User related objects.
 */
public class UserRepository {

    private static final String PREFERENCES_USER = "preferences_user";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";

    private final Context context;
    private final ExecutorUtil executorUtil;
    private final EncryptUtil encryptUtil;
    private final TimeUtil timeUtil;
    private final UserDao userDao;
    private final WhatToEatService whatToEatService;

    public UserRepository(Context context, ExecutorUtil executorUtil, EncryptUtil encryptUtil,
                          TimeUtil timeUtil, UserDao userDao, WhatToEatService whatToEatService) {
        this.context = context;
        this.executorUtil = executorUtil;
        this.encryptUtil = encryptUtil;
        this.timeUtil = timeUtil;
        this.userDao = userDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<User>> loadUser(UserDTO userDTO) {
        return new NetworkBoundResource<User, User>(executorUtil) {

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
                    userDTO.setPassword(encryptUtil.sha256(password));
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
        executorUtil.diskIO().execute(userDao::delete);
    }

    public LiveData<Event<Resource<Result>>> register(User user) {
        return new NetworkResource<Result, Void>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                String password = user.getPassword();
                user.setPassword(encryptUtil.sha256(password));
                return whatToEatService.register(user);
            }

            @Override
            protected Void saveCallResult(Result item) {
                setUserEmail(user.getEmail());
                return null;
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> bindGoogleAccount(GoogleAccount googleAccount) {
        return new NetworkResource<Result, Integer>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.bindGoogleAccount(googleAccount);
            }

            @Override
            protected Integer saveCallResult(Result item) {
                return userDao.updateGoogleID(googleAccount.getGoogleID(), timeUtil.now());
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> resetPassword(UserDTO userDTO) {
        return new NetworkResource<Result, Integer>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                String oldPassword = userDTO.getOldPassword();
                userDTO.setOldPassword(encryptUtil.sha256(oldPassword));
                String newPassword = userDTO.getNewPassword();
                userDTO.setNewPassword(encryptUtil.sha256(newPassword));
                return whatToEatService.resetPassword(userDTO);
            }

            @Override
            protected Integer saveCallResult(Result item) {
                return userDao.updatePassword(userDTO.getNewPassword(), timeUtil.now());
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> sendVerificationCode(UserDTO userDTO) {
        return new NetworkResource<Result, Void>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.sendVerificationCode(userDTO);
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> forgetPassword(UserDTO userDTO) {
        return new NetworkResource<Result, Integer>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                String password = userDTO.getNewPassword();
                userDTO.setNewPassword(encryptUtil.sha256(password));
                return whatToEatService.forgetPassword(userDTO);
            }

            @Override
            protected Integer saveCallResult(Result item) {
                return userDao.updatePassword(userDTO.getNewPassword(), timeUtil.now());
            }
        }.asLiveData();
    }
}