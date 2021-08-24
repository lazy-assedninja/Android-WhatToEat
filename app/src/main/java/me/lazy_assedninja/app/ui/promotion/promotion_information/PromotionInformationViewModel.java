package me.lazy_assedninja.app.ui.promotion.promotion_information;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import me.lazy_assedninja.app.api.RetrofitManager;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.repository.PromotionRepository;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class PromotionInformationViewModel extends AndroidViewModel {

    private final ExecutorUtils executorUtils = new ExecutorUtils();
    private final PromotionRepository promotionRepository = new PromotionRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).promotionDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );

    public PromotionInformationViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public LiveData<Promotion> getPromotion(int id) {
        return promotionRepository.getPromotionFromDb(id);
    }
}