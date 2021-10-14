package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.FavoriteDao;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.dto.FavoriteDTO;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.NetworkUtils;

public class FavoriteRepository {

    private final ExecutorUtils executorUtils;
    private final NetworkUtils networkUtils;
    private final StoreDao storeDao;
    private final FavoriteDao favoriteDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public FavoriteRepository(ExecutorUtils executorUtils, NetworkUtils networkUtils,
                              StoreDao storeDao, FavoriteDao favoriteDao,
                              WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.networkUtils = networkUtils;
        this.storeDao = storeDao;
        this.favoriteDao = favoriteDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Store>>> loadFavorites(FavoriteDTO favoriteDTO) {
        return new NetworkBoundResource<List<Store>, List<Store>>(executorUtils) {

            @Override
            protected LiveData<List<Store>> loadFromDb() {
                return favoriteDao.getFavorites(true);
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Store> data) {
                return data == null || data.isEmpty() || networkUtils.isConnected();
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
        return new NetworkResource<Result>(executorUtils) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return (favorite.getStatus()) ?
                        whatToEatService.addToFavorite(favorite) :
                        whatToEatService.cancelFavorite(favorite);
            }

            @Override
            protected void saveCallResult(Result item) {
                favoriteDao.updateFavoriteStatus(favorite.getStoreID(), favorite.getStatus());
            }
        }.asLiveData();
    }
}