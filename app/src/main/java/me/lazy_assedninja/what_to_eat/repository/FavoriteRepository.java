package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.FavoriteDao;
import me.lazy_assedninja.what_to_eat.db.StoreDao;
import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Store;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.NetworkUtil;

public class FavoriteRepository {

    private final ExecutorUtil executorUtil;
    private final NetworkUtil networkUtil;
    private final StoreDao storeDao;
    private final FavoriteDao favoriteDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public FavoriteRepository(ExecutorUtil executorUtil, NetworkUtil networkUtil,
                              StoreDao storeDao, FavoriteDao favoriteDao,
                              WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.networkUtil = networkUtil;
        this.storeDao = storeDao;
        this.favoriteDao = favoriteDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Store>>> loadFavorites(FavoriteDTO favoriteDTO) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtil) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return favoriteDao.getFavorites(true);
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || networkUtil.isConnected();
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.getFavoriteList(favoriteDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                storeDao.insertAll(item);
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> changeFavoriteStatus(Favorite favorite) {
        return new NetworkResource<Result>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return (favorite.getStatus()) ? whatToEatService.addToFavorite(favorite) :
                        whatToEatService.cancelFavorite(favorite);
            }

            @Override
            protected void saveCallResult(Result item) {
                favoriteDao.updateFavoriteStatus(favorite.getStoreID(), favorite.getStatus());
            }
        }.asLiveData();
    }
}