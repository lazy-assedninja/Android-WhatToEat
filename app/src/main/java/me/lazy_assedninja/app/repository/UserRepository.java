package me.lazy_assedninja.app.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

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
import me.lazy_assedninja.library.utils.TimeUtils;
import retrofit2.Response;

public class UserRepository {

    private static final String PREFERENCES_USER = "preferences_user";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";

    private final Context context;
    private final ExecutorUtils executorUtils;
    private final TimeUtils timeUtils;
    private final UserDao userDao;
    private final WhatToEatService whatToEatService;

    public UserRepository(Context context, ExecutorUtils executorUtils, TimeUtils timeUtils,
                          UserDao userDao, WhatToEatService whatToEatService) {
        this.context = context;
        this.executorUtils = executorUtils;
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
                return whatToEatService.login(userDTO);
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
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(new Event<>(resource));
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
            result.postValue(new Event<>(resource));
        });
        return result;
    }

    public LiveData<Resource<Result>> resetPassword(UserDTO userDTO) {
        MutableLiveData<Resource<Result>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(resource);
            try {
                Response<Result> response = whatToEatService.resetPassword(userDTO).execute();
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());

                    // Update data
                    userDao.updatePassword(userDTO.getNewPassword(), timeUtils.now());
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

    public LiveData<Resource<Result>> sendVerificationCode(UserDTO userDTO) {
        MutableLiveData<Resource<Result>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(resource);
            try {
                Response<Result> response = whatToEatService.sendVerificationCode(userDTO).execute();
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());
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

    public LiveData<Resource<Result>> forgetPassword(UserDTO userDTO) {
        MutableLiveData<Resource<Result>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(resource);
            try {
                Response<Result> response = whatToEatService.forgetPassword(userDTO).execute();
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());

                    // Update data
                    userDao.updatePassword(userDTO.getNewPassword(), timeUtils.now());
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