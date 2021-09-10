package me.lazy_assedninja.app.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiEmptyResponse;
import me.lazy_assedninja.app.api.ApiErrorResponse;
import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.ApiSuccessResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import retrofit2.Response;

public class UserRepository {

    private static final String PREFERENCES_USER_ID = "preferences_user_id";
    private static final String USER_ID = "user_id";

    private static final String PREFERENCES_USER_EMAIL = "preferences_user_email";
    private static final String USER_EMAIL = "user_email";

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

    public void setUserEmail(String value) {
        context.getSharedPreferences(PREFERENCES_USER_EMAIL, Context.MODE_PRIVATE)
                .edit()
                .putString(USER_EMAIL, value)
                .apply();
    }

    public String getUserEmail() {
        return context.getSharedPreferences(PREFERENCES_USER_EMAIL, Context.MODE_PRIVATE)
                .getString(USER_EMAIL, "");
    }

    public LiveData<User> getUserFromDb() {
        return userDao.get();
    }

    public void deleteUser() {
        executorUtils.diskIO().execute(userDao::delete);
    }

    public LiveData<Resource<Result>> register(User user) {
        MutableLiveData<Resource<Result>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(resource);
            try {
                Response<Result> response = whatToEatService.register(user).execute();
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());

                    // Update data
                    setUserEmail(user.getEmail());
                } else if (apiResponse instanceof ApiEmptyResponse) {
                    resource = Resource.error("No response.", null);
                } else if (apiResponse instanceof ApiErrorResponse) {
                    resource = Resource.error(
                            ((ApiErrorResponse<Result>) apiResponse).getErrorMessage(), null);
                } else {
                    resource = Resource.error("Unknown error.", null);
                }
            } catch (IOException e) {
                resource = Resource.error(e.getMessage(), null);
            }
            result.postValue(resource);
        });
        return result;
    }
}