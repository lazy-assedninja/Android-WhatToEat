package me.lazy_assedninja.app.ui.store;

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

import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.store.map.MapViewModel;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final StoreRepository storeRepository = mock(StoreRepository.class);
    private final MapViewModel viewModel = new MapViewModel(userRepository, storeRepository);

    @Test
    public void testNull() {
        assertThat(viewModel.stores, notNullValue());

        verify(storeRepository, never()).loadAllStores(any());
        viewModel.requestStore();
        verify(storeRepository, never()).loadAllStores(any());
    }

    @Test
    public void sendResultToUI() {
        MutableLiveData<Resource<List<Store>>> list = new MutableLiveData<>();
        when(storeRepository.loadAllStores(any())).thenReturn(list);
        Observer<Resource<List<Store>>> listObserver = mock(Observer.class);
        viewModel.stores.observeForever(listObserver);
        viewModel.requestStore();
        verify(listObserver, never()).onChanged(any());
        List<Store> data = new ArrayList<>();
        Resource<List<Store>> resource = Resource.success(data);

        list.setValue(resource);
        verify(listObserver).onChanged(resource);
    }

    @Test
    public void loadStores() {
        viewModel.stores.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(storeRepository);
        viewModel.requestStore();
        verify(storeRepository).loadAllStores(any());
        verifyNoMoreInteractions(storeRepository);
    }
}
