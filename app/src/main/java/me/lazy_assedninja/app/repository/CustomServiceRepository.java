package me.lazy_assedninja.app.repository;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Report;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class CustomServiceRepository {

    private final ExecutorUtils executorUtils;
    private final WhatToEatService whatToEatService;

    @Inject
    public CustomServiceRepository(ExecutorUtils executorUtils, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Event<Resource<Result>>> createReport(Report report) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.createReport(report);
            }
        }.asLiveData();
    }
}