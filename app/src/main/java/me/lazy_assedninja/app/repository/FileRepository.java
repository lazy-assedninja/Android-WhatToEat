package me.lazy_assedninja.app.repository;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.TimeUtil;
import okhttp3.MultipartBody;

public class FileRepository {

    private final ExecutorUtil executorUtil;
    private final TimeUtil timeUtil;
    private final UserDao userDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public FileRepository(ExecutorUtil executorUtil, TimeUtil timeUtil, UserDao userDao,
                          WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.timeUtil = timeUtil;
        this.userDao = userDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Event<Resource<Result>>> upload(MultipartBody.Part file) {
        return new NetworkResource<Result>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.upload(file);
            }

            @Override
            protected void saveCallResult(Result item) {
                userDao.updateFile(timeUtil.now());
            }
        }.asLiveData();
    }
}
