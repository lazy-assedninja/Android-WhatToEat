package me.lazy_assedninja.app.repository;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.TimeUtils;
import okhttp3.MultipartBody;

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
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.upload(file);
            }

            @Override
            protected void saveCallResult(Result item) {
                userDao.updateFile(timeUtils.now());
            }
        }.asLiveData();
    }
}
