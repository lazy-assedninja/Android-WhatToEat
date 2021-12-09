package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.PromotionDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.vo.Promotion;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.NetworkUtil;

public class PromotionRepository {

    private final ExecutorUtil executorUtil;
    private final NetworkUtil networkUtil;
    private final WhatToEatDatabase db;
    private final PromotionDao promotionDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public PromotionRepository(ExecutorUtil executorUtil, NetworkUtil networkUtil,
                               WhatToEatDatabase db, PromotionDao promotionDao,
                               WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.networkUtil = networkUtil;
        this.db = db;
        this.promotionDao = promotionDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Promotion>>> loadPromotions() {
        return new NetworkBoundResource<List<Promotion>, List<Promotion>>(executorUtil) {

            @Override
            protected LiveData<List<Promotion>> loadFromDb() {
                return promotionDao.getPromotions();
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Promotion> data) {
                return data == null || data.isEmpty() || networkUtil.isConnected();
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