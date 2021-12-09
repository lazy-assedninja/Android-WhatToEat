package me.lazy_assedninja.what_to_eat.ui.promotion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.dto.PromotionDTO;
import me.lazy_assedninja.what_to_eat.repository.PromotionRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Promotion;
import me.lazy_assedninja.what_to_eat.vo.Resource;

@HiltViewModel
public class PromotionViewModel extends ViewModel {

    private PromotionRepository promotionRepository;

    private final MutableLiveData<PromotionDTO> promotionRequest = new MutableLiveData<>();

    @Inject
    public PromotionViewModel(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public final LiveData<Resource<List<Promotion>>> promotions = Transformations.switchMap(promotionRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return promotionRepository.loadPromotions();
        }
    });

    public void requestPromotion() {
        if (promotionRequest.getValue() == null) {
            promotionRequest.setValue(new PromotionDTO());
        }
    }
}