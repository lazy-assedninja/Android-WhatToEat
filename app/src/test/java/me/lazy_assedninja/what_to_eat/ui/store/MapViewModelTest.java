package me.lazy_assedninja.what_to_eat.ui.store;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static me.lazy_assedninja.what_to_eat.common.TestUtil.createStoreDTO;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.repository.StoreRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.store.map.MapViewModel;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

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
        viewModel.requestStore(createStoreDTO());
        verify(storeRepository, never()).loadAllStores(any());
    }

    @Test
    public void sendResultToUI() {
        StoreDTO storeDTO = createStoreDTO();
        MutableLiveData<Resource<List<Store>>> list = new MutableLiveData<>();
        when(storeRepository.loadAllStores(storeDTO)).thenReturn(list);
        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        viewModel.stores.observeForever(observer);
        viewModel.requestStore(storeDTO);
        verify(observer, never()).onChanged(any());

        List<Store> data = new ArrayList<>();
        Resource<List<Store>> resource = Resource.success(data);
        list.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void loadStores() {
        viewModel.stores.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository, storeRepository);

        StoreDTO storeDTO = createStoreDTO();
        viewModel.requestStore(storeDTO);
        verify(userRepository).getUserID();
        verify(storeRepository).loadAllStores(storeDTO);
        verifyNoMoreInteractions(userRepository, storeRepository);
    }
}