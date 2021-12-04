package me.lazy_assedninja.app.ui.store;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.store.reservation.reserve.ReserveViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ReserveViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final StoreRepository storeRepository = mock(StoreRepository.class);
    private final ReserveViewModel viewModel = new ReserveViewModel(userRepository, storeRepository);

    @Test
    public void testNull() {
        assertThat(viewModel.result, notNullValue());

        verify(storeRepository, never()).reserve(any());
        viewModel.reserve("name", "phone", "amount", "time");
        verify(storeRepository, never()).reserve(any());
    }

    @Test
    public void sendResultToUI() {
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(storeRepository.reserve(any())).thenReturn(result);
        Observer<Event<Resource<Result>>> resultObserver = mock(Observer.class);
        viewModel.result.observeForever(resultObserver);
        viewModel.reserve("name", "phone", "amount", "time");
        verify(resultObserver, never()).onChanged(any());
        Event<Resource<Result>> resultResource = new Event<>(Resource.success(createResult()));

        result.setValue(resultResource);
        verify(resultObserver).onChanged(resultResource);
    }

    @Test
    public void reserve() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(storeRepository);
        viewModel.reserve("name", "phone", "amount", "time");
        verify(storeRepository).reserve(any());
        verifyNoMoreInteractions(storeRepository);
    }
}
