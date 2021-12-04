package me.lazy_assedninja.app.ui.promotion;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.repository.PromotionRepository;
import me.lazy_assedninja.app.ui.promotion.promotion_information.PromotionInformationViewModel;

@RunWith(JUnit4.class)
public class PromotionInformationViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final PromotionRepository promotionRepository = mock(PromotionRepository.class);
    private final PromotionInformationViewModel viewModel =
            new PromotionInformationViewModel(promotionRepository);

    @Test
    public void get() {
        int id = 0;
        viewModel.get(id);

        verify(promotionRepository).getPromotionFromDb(id);
    }
}
