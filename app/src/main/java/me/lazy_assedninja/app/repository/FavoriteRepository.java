package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiEmptyResponse;
import me.lazy_assedninja.app.api.ApiErrorResponse;
import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.ApiSuccessResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.FavoriteDao;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.dto.FavoriteDTO;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.TimeUtils;
import retrofit2.Response;

public class FavoriteRepository {

    private final ExecutorUtils executorUtils;
    private final TimeUtils timeUtils;
    private final StoreDao storeDao;
    private final FavoriteDao favoriteDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public FavoriteRepository(ExecutorUtils executorUtils, TimeUtils timeUtils, StoreDao storeDao,
                              FavoriteDao favoriteDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.timeUtils = timeUtils;
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
                return data == null || data.isEmpty();
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

    public LiveData<Resource<Result>> changeFavoriteStatus(Favorite favorite) {
        MutableLiveData<Resource<Result>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(resource);
            try {
                Response<Result> response = (favorite.getStatus()) ?
                        whatToEatService.addToFavorite(favorite).execute() :
                        whatToEatService.cancelFavorite(favorite).execute();
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());

                    // Update Db data
                    executorUtils.diskIO().execute(() ->
                            favoriteDao.updateFavoriteStatus(favorite.getStoreID(), favorite.getStatus(),
                                    timeUtils.dateTime(System.currentTimeMillis())));
                } else if (apiResponse instanceof ApiEmptyResponse) {
                    resource = Resource.error("No response.", null);
                } else if (apiResponse instanceof ApiErrorResponse) {
                    resource = Resource.error(
                            ((ApiErrorResponse<Result>) apiResponse).getErrorMessage(), null);
                } else {
                    resource = Resource.error("Unknown error.", null);
                }
            } catch (IOException e) {
                resource = Resource.error(e.getMessage(), null);
            }
            result.postValue(resource);
        });
        return result;
    }
}