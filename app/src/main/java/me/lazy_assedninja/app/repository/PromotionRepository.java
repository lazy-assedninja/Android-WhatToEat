package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.PromotionDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.NetworkUtils;

public class PromotionRepository {

    private final ExecutorUtils executorUtils;
    private final NetworkUtils networkUtils;
    private final WhatToEatDatabase db;
    private final PromotionDao promotionDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public PromotionRepository(ExecutorUtils executorUtils, NetworkUtils networkUtils,
                               WhatToEatDatabase db, PromotionDao promotionDao,
                               WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.networkUtils = networkUtils;
        this.db = db;
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
                return data == null || data.isEmpty() || networkUtils.isConnected();
            }

            @Override
            protected LiveData<ApiResponse<List<Promotion>>> createCall() {
                return whatToEatService.getPromotionList();
            }

            @Override
            protected void saveCallResult(List<Promotion> item) {
                db.runInTransaction(() -> {
                    promotionDao.delete();
                    promotionDao.insertAll(item);
                });
            }
        }.asLiveData();
    }

    public LiveData<Promotion> getPromotionFromDb(int id) {
        return promotionDao.get(id);
    }
}