package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.TimeUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.FavoriteDao;
import me.lazy_assedninja.what_to_eat.db.StoreDao;
import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.util.RateLimiter;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Status;
import me.lazy_assedninja.what_to_eat.vo.Store;

/**
 * Repository that handles Favorite related objects.
 */
public class FavoriteRepository {

    private final ExecutorUtil executorUtil;
    private final TimeUtil timeUtil;
    private final StoreDao storeDao;
    private final FavoriteDao favoriteDao;
    private final WhatToEatService whatToEatService;

    private final RateLimiter<FavoriteDTO> rateLimiter = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public FavoriteRepository(ExecutorUtil executorUtil, TimeUtil timeUtil, StoreDao storeDao,
                              FavoriteDao favoriteDao, WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.timeUtil = timeUtil;
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
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(favoriteDTO);
            }

            @Override
            protected LiveData<ApiResponse<List<Store>>> createCall() {
                return whatToEatService.getFavoriteList(favoriteDTO);
            }

            @Override
            protected void saveCallResult(List<Store> item) {
                storeDao.insertAll(item);
            }

            @Override
            protected void onFetchFailed() {
                rateLimiter.reset(favoriteDTO);
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Favorite>>> changeFavoriteStatus(Favorite favorite) {
        return new NetworkResource<Favorite, Integer>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Favorite>> createCall() {
                return (favorite.getStatus()) ? whatToEatService.addToFavorite(favorite) :
                        whatToEatService.cancelFavorite(favorite);
            }

            @Override
            protected Integer saveCallResult(Favorite item) {
                return (favorite.isNeedUpdateTime()) ?
                        favoriteDao.updateFavoriteStatusAndTime(favorite.getStoreID(),
                                favorite.getStatus(), timeUtil.now()) :
                        favoriteDao.updateFavoriteStatus(favorite.getStoreID(), favorite.getStatus());
            }

            @Override
            protected Resource<Favorite> processResource(Resource<Favorite> resource) {
                if (resource.getData() == null) {
                    if (resource.getStatus() == Status.SUCCESS) {
                        resource.setData(new Favorite(favorite.getStoreID(), favorite.getStatus()));
                    } else if (resource.getStatus() == Status.ERROR){
                        resource.setData(new Favorite(favorite.getStoreID(), !favorite.getStatus()));
                    }
                }else{
                    Favorite data = resource.getData();
                    data.setStoreID(favorite.getStoreID());
                    data.setStatus(favorite.getStatus());
                }
                return resource;
            }
        }.asLiveData();
    }
}