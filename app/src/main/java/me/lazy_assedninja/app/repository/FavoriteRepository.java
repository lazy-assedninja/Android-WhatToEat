package me.lazy_assedninja.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import me.lazy_assedninja.app.api.ApiEmptyResponse;
import me.lazy_assedninja.app.api.ApiErrorResponse;
import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.ApiSuccessResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.FavoriteDao;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import retrofit2.Response;

public class FavoriteRepository {

    private final ExecutorUtils executorUtils;
    private final FavoriteDao favoriteDao;
    private final WhatToEatService whatToEatService;

    public FavoriteRepository(ExecutorUtils executorUtils, FavoriteDao favoriteDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.favoriteDao = favoriteDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<Result>> changeFavoriteStatus(Favorite favorite) {
        MutableLiveData<Resource<Result>> result = new MutableLiveData<>();
        executorUtils.networkIO().execute(() -> {
            Resource<Result> resource = Resource.loading(null);
            result.postValue(resource);
            try {
                Response<Result> response;
                if (favorite.getStatus()) {
                    response = whatToEatService.addToFavorite(favorite).execute();
                } else {
                    response = whatToEatService.cancelFavorite(favorite).execute();
                }
                ApiResponse<Result> apiResponse = ApiResponse.create(response);
                if (apiResponse instanceof ApiSuccessResponse) {
                    resource = Resource.success(((ApiSuccessResponse<Result>) apiResponse).getBody());
                } else if (apiResponse instanceof ApiEmptyResponse) {
                    resource = Resource.error("Result empty.", null);
                } else if (apiResponse instanceof ApiErrorResponse) {
                    resource = Resource.error(((ApiErrorResponse<Result>) apiResponse).getErrorMessage(), null);
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

    public LiveData<List<Store>> loadFavorites() {
        return favoriteDao.getFavorites(true);
    }

    public void updateFavoriteStatus(int storeID, boolean isFavorite) {
        executorUtils.diskIO().execute(() -> favoriteDao.updateFavoriteStatus(storeID, isFavorite));
    }
}