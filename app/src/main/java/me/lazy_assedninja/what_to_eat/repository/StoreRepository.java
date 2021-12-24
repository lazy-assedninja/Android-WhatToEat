package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.StoreDao;
import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.util.RateLimiter;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

/**
 * Repository that handles Store objects.
 */
public class StoreRepository {

    private final ExecutorUtil executorUtil;
    private final StoreDao storeDao;
    private final WhatToEatService whatToEatService;

    private final RateLimiter<StoreDTO> rateLimiter = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public StoreRepository(ExecutorUtil executorUtil, StoreDao storeDao,
                           WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.storeDao = storeDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Store>>> loadStores(StoreDTO storeDTO) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtil) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return storeDao.getStores(storeDTO.getTagID());
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(storeDTO);
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.getStoreList(storeDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                for (Store store : item) {
                    store.setUserID(storeDTO.getUserID());
                }
                storeDao.insertAll(item);
            }

            @Override
            protected void onFetchFailed() {
                rateLimiter.reset(storeDTO);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Store>>> loadAllStores(StoreDTO storeDTO) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtil) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return storeDao.getStores();
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(storeDTO);
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.getAllStores(storeDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                storeDao.insertAll(item);
            }

            @Override
            protected void onFetchFailed() {
                rateLimiter.reset(storeDTO);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Store>>> search(StoreDTO storeDTO) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtil) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return storeDao.search("%" + storeDTO.getKeyword() + "%");
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(storeDTO);
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.search(storeDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                storeDao.insertAll(item);
            }

            @Override
            protected void onFetchFailed() {
                rateLimiter.reset(storeDTO);
            }
        }.asLiveData();
    }

    public LiveData<Store> getStoreFromDb(int id) {
        return storeDao.get(id);
    }

    public LiveData<Store> getStoreFromDb(String name) {
        return storeDao.get(name);
    }
}