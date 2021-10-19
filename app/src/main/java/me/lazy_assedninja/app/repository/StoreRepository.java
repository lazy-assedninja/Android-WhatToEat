package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.ReservationDao;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.db.TagDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
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
    private final ReservationDao reservationDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public StoreRepository(ExecutorUtils executorUtils, NetworkUtils networkUtils,
                           WhatToEatDatabase db, TagDao tagDao, StoreDao storeDao,
                           ReservationDao reservationDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.networkUtils = networkUtils;
        this.db = db;
        this.tagDao = tagDao;
        this.storeDao = storeDao;
        this.reservationDao = reservationDao;
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

    public LiveData<Resource<List<Store>>> loadAllStores(StoreDTO storeDTO) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtils) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return storeDao.getStores();
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || networkUtils.isConnected();
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
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtils) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return storeDao.search("%" + storeDTO.getKeyword() + "%");
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || networkUtils.isConnected();
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

    public void initTags() {
        executorUtils.diskIO().execute(() -> {
            if (tagDao.getTagSize() != 2) {
                tagDao.insert(new Tag(2));
            }
        });
    }

    public LiveData<Event<Resource<Result>>> reserve(Reservation reservation) {
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.createReservation(reservation);
            }

            @Override
            protected void saveCallResult(Result item) {
                reservationDao.insert(reservation);
            }
        }.asLiveData();
    }
}