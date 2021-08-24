package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.PromotionDao;
import me.lazy_assedninja.app.dto.StoreRequest;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class PromotionRepository {

    private final ExecutorUtils executorUtils;
    private final PromotionDao promotionDao;
    private final WhatToEatService whatToEatService;

    public PromotionRepository(ExecutorUtils executorUtils, PromotionDao promotionDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.promotionDao = promotionDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Promotion>>> loadPromotions() {
        return new NetworkBoundResource<List<Promotion>, List<Promotion>>(executorUtils) {

            @Override
            protected LiveData<List<Promotion>> loadFromDb() {
                return promotionDao.getPromotions();
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Promotion> data) {
                return data == null || data.isEmpty();
            }

            @Override
            protected LiveData<ApiResponse<List<Promotion>>> createCall() {
                return whatToEatService.getPromotionList();
            }

            @Override
            protected void saveCallResult(List<Promotion> item) {
                promotionDao.insertAll(item);
            }
        }.asLiveData();
    }

    public LiveData<Promotion> getPromotionFromDb(int id) {
        return promotionDao.getPromotion(id);
    }
}