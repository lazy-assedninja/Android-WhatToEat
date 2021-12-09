package me.lazy_assedninja.what_to_eat.ui.promotion;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.what_to_eat.repository.PromotionRepository;
import me.lazy_assedninja.what_to_eat.vo.Promotion;
import me.lazy_assedninja.what_to_eat.vo.Resource;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class PromotionViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final PromotionRepository promotionRepository = mock(PromotionRepository.class);
    private final PromotionViewModel viewModel = new PromotionViewModel(promotionRepository);

    @Test
    public void testNull() {
        assertThat(viewModel.promotions, notNullValue());

        verify(promotionRepository, never()).loadPromotions();
        viewModel.requestPromotion();
        verify(promotionRepository, never()).loadPromotions();
    }

    @Test
    public void sendResultToUI() {
        MutableLiveData<Resource<List<Promotion>>> list = new MutableLiveData<>();
        when(promotionRepository.loadPromotions()).thenReturn(list);
        Observer<Resource<List<Promotion>>> observer = mock(Observer.class);
        viewModel.promotions.observeForever(observer);
        viewModel.requestPromotion();
        verify(observer, never()).onChanged(any());

        List<Promotion> data = new ArrayList<>();
        Resource<List<Promotion>> resource = Resource.success(data);
        list.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void loadPromotions() {
        viewModel.promotions.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(promotionRepository);

        viewModel.requestPromotion();
        verify(promotionRepository).loadPromotions();
        verifyNoMoreInteractions(promotionRepository);
    }
}