package me.lazy_assedninja.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.sql.Time;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiEmptyResponse;
import me.lazy_assedninja.app.api.ApiErrorResponse;
import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.ApiSuccessResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.TimeUtils;
import okhttp3.MultipartBody;
import retrofit2.Response;

public class FileRepository {

    private final ExecutorUtils executorUtils;
    private final TimeUtils timeUtils;
    private final UserDao userDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public FileRepository(ExecutorUtils executorUtils, TimeUtils timeUtils, UserDao userDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.timeUtils = timeUtils;
        this.userDao = userDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Event<Resource<Result>>> upload(MultipartBody.Part file) {
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(new Event<>(resource));
            try {
                Response<Result> response = whatToEatService.upload(file).execute();
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());

                    // Update update time
                    userDao.updateFile(timeUtils.now());
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
}
