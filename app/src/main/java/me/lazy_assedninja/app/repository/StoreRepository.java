package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.NetworkUtil;

public class StoreRepository {

    private final ExecutorUtil executorUtil;
    private final NetworkUtil networkUtil;
    private final WhatToEatDatabase db;
    private final StoreDao storeDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public StoreRepository(ExecutorUtil executorUtil, NetworkUtil networkUtil, WhatToEatDatabase db,
                           StoreDao storeDao, WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.networkUtil = networkUtil;
        this.db = db;
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
                return data == null || data.isEmpty() || networkUtil.isConnected();
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.getStoreList(storeDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                int tagID = storeDTO.getTagID();
                for (Store store : item) {
                    store.setTagID(tagID);
                }
                db.runInTransaction(() -> storeDao.insertAll(item));
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
                return data == null || data.isEmpty() || networkUtil.isConnected();
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.getAllStores(storeDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                storeDao.insertAll(item);
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
                return data == null || data.isEmpty() || networkUtil.isConnected();
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.search(storeDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                storeDao.insertAll(item);
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