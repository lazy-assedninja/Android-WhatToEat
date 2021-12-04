package me.lazy_assedninja.app.repository;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Report;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.util.ExecutorUtil;

public class CustomServiceRepository {

    private final ExecutorUtil executorUtil;
    private final WhatToEatService whatToEatService;

    @Inject
    public CustomServiceRepository(ExecutorUtil executorUtil, WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Event<Resource<Result>>> createReport(Report report) {
        return new NetworkResource<Result>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.createReport(report);
            }
        }.asLiveData();
    }
}