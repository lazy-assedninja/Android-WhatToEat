package me.lazy_assedninja.app.ui.store;

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

import static me.lazy_assedninja.app.common.TestUtil.createResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.app.repository.FavoriteRepository;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.store.search.SearchViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class SearchViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final StoreRepository storeRepository = mock(StoreRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final SearchViewModel viewModel = new SearchViewModel(userRepository, storeRepository,
            favoriteRepository);

    @Test
    public void testNull() {
        // Search stores
        assertThat(viewModel.stores, notNullValue());

        verify(storeRepository, never()).search(any());
        viewModel.setStoreRequest("keyword");
        verify(storeRepository, never()).search(any());

        // Change favorite status
        assertThat(viewModel.result, notNullValue());

        verify(favoriteRepository, never()).changeFavoriteStatus(any());
        viewModel.setFavoriteRequest(1, true);
        verify(favoriteRepository, never()).changeFavoriteStatus(any());
    }

    @Test
    public void sendResultToUI() {
        // Search stores
        MutableLiveData<Resource<List<Store>>> list = new MutableLiveData<>();
        when(storeRepository.search(any())).thenReturn(list);
        Observer<Resource<List<Store>>> listObserver = mock(Observer.class);
        viewModel.stores.observeForever(listObserver);
        viewModel.setStoreRequest("keyword");
        verify(listObserver, never()).onChanged(any());
        List<Store> listData = new ArrayList<>();
        Resource<List<Store>> listResource = Resource.success(listData);

        list.setValue(listResource);
        verify(listObserver).onChanged(listResource);

        // Change favorite status
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(favoriteRepository.changeFavoriteStatus(any())).thenReturn(result);
        Observer<Event<Resource<Result>>> resultObserver = mock(Observer.class);
        viewModel.result.observeForever(resultObserver);
        viewModel.setFavoriteRequest(1, true);
        verify(resultObserver, never()).onChanged(any());
        Event<Resource<Result>> resultResource = new Event<>(Resource.success(createResult()));

        result.setValue(resultResource);
        verify(resultObserver).onChanged(resultResource);
    }

    @Test
    public void search() {
        viewModel.stores.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(storeRepository);
        viewModel.setStoreRequest("keyword");
        verify(storeRepository).search(any());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    public void changeFavoriteStatus() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(favoriteRepository);
        viewModel.setFavoriteRequest(1, true);
        verify(favoriteRepository).changeFavoriteStatus(any());
        verifyNoMoreInteractions(favoriteRepository);
    }

    @Test
    public void refresh() {
        viewModel.setStoreRequest("keyword");
        verifyNoMoreInteractions(storeRepository);
        viewModel.refresh();
        verifyNoMoreInteractions(storeRepository);
        Observer<Resource<List<Store>>> userObserver = mock(Observer.class);
        viewModel.stores.observeForever(userObserver);

        verify(storeRepository).search(any());
        reset(storeRepository);

        viewModel.refresh();
        verify(storeRepository).search(any());
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