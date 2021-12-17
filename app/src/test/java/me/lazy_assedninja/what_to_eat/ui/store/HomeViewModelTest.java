package me.lazy_assedninja.what_to_eat.ui.store;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFavorite;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createRequestResult;
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
import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.StoreRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.store.home.HomeViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.RequestResult;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final StoreRepository storeRepository = mock(StoreRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final HomeViewModel viewModel = new HomeViewModel(userRepository, storeRepository,
            favoriteRepository);

    @Test
    public void testNull() {
        // Load stores
        assertThat(viewModel.stores, notNullValue());

        verify(storeRepository, never()).loadStores(any());
        viewModel.requestStore(createStoreDTO());
        verify(storeRepository, never()).loadStores(any());

        // Change favorite status
        assertThat(viewModel.result, notNullValue());

        verify(favoriteRepository, never()).changeFavoriteStatus(any());
        viewModel.changeFavoriteStatus(createFavorite());
        verify(favoriteRepository, never()).changeFavoriteStatus(any());
    }

    @Test
    public void sendResultToUI() {
        // Load stores
        StoreDTO storeDTO = createStoreDTO();
        MutableLiveData<Resource<List<Store>>> list = new MutableLiveData<>();
        when(storeRepository.loadStores(storeDTO)).thenReturn(list);
        Observer<Resource<List<Store>>> listObserver = mock(Observer.class);
        viewModel.stores.observeForever(listObserver);
        viewModel.requestStore(storeDTO);
        verify(listObserver, never()).onChanged(any());

        List<Store> listData = new ArrayList<>();
        Resource<List<Store>> listResource = Resource.success(listData);
        list.setValue(listResource);
        verify(listObserver).onChanged(listResource);

        // Change favorite status
        Favorite favorite = createFavorite();
        MutableLiveData<Event<Resource<RequestResult<Favorite>>>> result = new MutableLiveData<>();
        when(favoriteRepository.changeFavoriteStatus(favorite)).thenReturn(result);
        Observer<Event<Resource<RequestResult<Favorite>>>> resultObserver = mock(Observer.class);
        viewModel.result.observeForever(resultObserver);
        viewModel.changeFavoriteStatus(favorite);
        verify(resultObserver, never()).onChanged(any());

        Event<Resource<RequestResult<Favorite>>> resultResource =
                new Event<>(Resource.success(createRequestResult(createFavorite())));
        result.setValue(resultResource);
        verify(resultObserver).onChanged(resultResource);
    }

    @Test
    public void loadStores() {
        viewModel.stores.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository, storeRepository);

        StoreDTO storeDTO = createStoreDTO();
        viewModel.requestStore(storeDTO);
        verify(userRepository).getUserID();
        verify(storeRepository).loadStores(storeDTO);
        verifyNoMoreInteractions(userRepository, storeRepository);
    }

    @Test
    public void changeFavoriteStatus() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository, favoriteRepository);

        Favorite favorite = createFavorite();
        viewModel.changeFavoriteStatus(favorite);
        verify(userRepository).getUserID();
        verify(favoriteRepository).changeFavoriteStatus(favorite);
        verifyNoMoreInteractions(userRepository, favoriteRepository);
    }

    @Test
    public void refresh() {
        StoreDTO storeDTO = createStoreDTO();
        viewModel.requestStore(storeDTO);
        verifyNoMoreInteractions(storeRepository);
        viewModel.refresh();
        verifyNoMoreInteractions(storeRepository);

        Observer<Resource<List<Store>>> userObserver = mock(Observer.class);
        viewModel.stores.observeForever(userObserver);
        verify(storeRepository).loadStores(storeDTO);
        reset(storeRepository);

        viewModel.refresh();
        verify(storeRepository).loadStores(storeDTO);
        reset(storeRepository);
        viewModel.stores.removeObserver(userObserver);

        viewModel.refresh();
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }
}