package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.db.TagDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.app.vo.Tag;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.NetworkUtils;

public class StoreRepository {

    private final ExecutorUtils executorUtils;
    private final NetworkUtils networkUtils;
    private final WhatToEatDatabase db;
    private final TagDao tagDao;
    private final StoreDao storeDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public StoreRepository(ExecutorUtils executorUtils, NetworkUtils networkUtils, WhatToEatDatabase db,
                           TagDao tagDao, StoreDao storeDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.networkUtils = networkUtils;
        this.db = db;
        this.tagDao = tagDao;
        this.storeDao = storeDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Store>>> loadStores(StoreDTO storeDTO) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtils) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return storeDao.getStores(storeDTO.getTagID());
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || networkUtils.isConnected();
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
                db.runInTransaction(() -> {
                    tagDao.insert(new Tag(tagID));
                    storeDao.insertAll(item);
                });
            }
        }.asLiveData();
    }

    public LiveData<Store> getStoreFromDb(int id) {
        return storeDao.get(id);
    }
}