package me.lazy_assedninja.app.ui.promotion;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import me.lazy_assedninja.app.api.RetrofitManager;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.PromotionRequest;
import me.lazy_assedninja.app.dto.StoreRequest;
import me.lazy_assedninja.app.repository.PromotionRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class PromotionViewModel extends AndroidViewModel {

    private final ExecutorUtils executorUtils = new ExecutorUtils();
    private final PromotionRepository promotionRepository  = new PromotionRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).promotionDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );

    private final MutableLiveData<PromotionRequest> promotionRequest = new MutableLiveData<>();

    public PromotionViewModel(@NonNull Application application) {
        super(application);
    }

    public final LiveData<Resource<List<Promotion>>> promotion = Transformations.switchMap(promotionRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return promotionRepository.loadPromotions();
        }
    });

    public void setPromotionRequest(PromotionRequest request){
        if (promotionRequest.getValue() != request){
            promotionRequest.setValue(request);
        }
    }
}