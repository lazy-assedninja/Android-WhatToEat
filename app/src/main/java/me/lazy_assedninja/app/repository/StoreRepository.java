package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.dto.StoreRequest;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class StoreRepository {

    private final ExecutorUtils executorUtils;
    private final StoreDao storeDao;
    private final WhatToEatService whatToEatService;

    public StoreRepository(ExecutorUtils executorUtils, StoreDao storeDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.storeDao = storeDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Store>>> loadStores(StoreRequest storeRequest) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtils) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return storeDao.getStores(storeRequest.getTagID());
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty();
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.getStoreList(storeRequest);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                storeDao.insertAll(item);
            }
        }.asLiveData();
    }

    public LiveData<Store> getStoreFromDb(int id) {
        return storeDao.getStore(id);
    }
}