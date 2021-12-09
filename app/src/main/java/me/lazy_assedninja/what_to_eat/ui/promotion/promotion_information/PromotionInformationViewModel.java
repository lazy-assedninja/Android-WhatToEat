package me.lazy_assedninja.what_to_eat.ui.promotion.promotion_information;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.repository.PromotionRepository;
import me.lazy_assedninja.what_to_eat.vo.Promotion;

@HiltViewModel
public class PromotionInformationViewModel extends ViewModel {

    private final PromotionRepository promotionRepository;

    @Inject
    public PromotionInformationViewModel(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public LiveData<Promotion> get(int id) {
        return promotionRepository.getPromotionFromDb(id);
    }
}