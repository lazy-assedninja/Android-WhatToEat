package me.lazy_assedninja.what_to_eat.repository;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Report;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.library.util.ExecutorUtil;

/**
 * Repository that handles CustomService related objects.
 */
public class CustomServiceRepository {

    private final ExecutorUtil executorUtil;
    private final WhatToEatService whatToEatService;

    @Inject
    public CustomServiceRepository(ExecutorUtil executorUtil, WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Event<Resource<Result>>> createReport(Report report) {
        return new NetworkResource<Result, Void>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.createReport(report);
            }
        }.asLiveData();
    }
}