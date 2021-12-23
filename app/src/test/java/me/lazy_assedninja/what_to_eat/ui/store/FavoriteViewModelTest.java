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
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFavoriteDTO;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.store.favorite.FavoriteViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class FavoriteViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final FavoriteViewModel viewModel = new FavoriteViewModel(userRepository,
            favoriteRepository);

    @Test
    public void testNull() {
        // Load favorites
        assertThat(viewModel.stores, notNullValue());

        verify(favoriteRepository, never()).loadFavorites(any());
        viewModel.requestStore(createFavoriteDTO());
        verify(favoriteRepository, never()).loadFavorites(any());

        // Change favorite status
        assertThat(viewModel.result, notNullValue());

        verify(favoriteRepository, never()).changeFavoriteStatus(any());
        viewModel.changeFavoriteStatus(createFavorite());
        verify(favoriteRepository, never()).changeFavoriteStatus(any());
    }

    @Test
    public void sendResultToUI() {
        // Load favorites
        FavoriteDTO favoriteDTO = createFavoriteDTO();
        MutableLiveData<Resource<List<Store>>> list = new MutableLiveData<>();
        when(favoriteRepository.loadFavorites(favoriteDTO)).thenReturn(list);
        Observer<Resource<List<Store>>> listObserver = mock(Observer.class);
        viewModel.stores.observeForever(listObserver);
        viewModel.requestStore(favoriteDTO);
        verify(listObserver, never()).onChanged(any());

        List<Store> listData = new ArrayList<>();
        Resource<List<Store>> listResource = Resource.success(listData);
        list.setValue(listResource);
        verify(listObserver).onChanged(listResource);

        // Change favorite status
        Favorite favorite = createFavorite();
        MutableLiveData<Event<Resource<Favorite>>> result = new MutableLiveData<>();
        when(favoriteRepository.changeFavoriteStatus(favorite)).thenReturn(result);
        Observer<Event<Resource<Favorite>>> resultObserver = mock(Observer.class);
        viewModel.result.observeForever(resultObserver);
        viewModel.changeFavoriteStatus(favorite);
        verify(resultObserver, never()).onChanged(any());

        Event<Resource<Favorite>> resultResource = new Event<>(Resource.success(createFavorite()));
        result.setValue(resultResource);
        verify(resultObserver).onChanged(resultResource);
    }

    @Test
    public void loadFavorites() {
        viewModel.stores.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository, favoriteRepository);

        FavoriteDTO favoriteDTO = createFavoriteDTO();
        viewModel.requestStore(favoriteDTO);
        verify(userRepository).getUserID();
        verify(favoriteRepository).loadFavorites(favoriteDTO);
        verifyNoMoreInteractions(userRepository, favoriteRepository);
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
        FavoriteDTO favoriteDTO = createFavoriteDTO();
        viewModel.requestStore(favoriteDTO);
        verifyNoMoreInteractions(favoriteRepository);
        viewModel.refresh();
        verifyNoMoreInteractions(favoriteRepository);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        viewModel.stores.observeForever(observer);
        verify(favoriteRepository).loadFavorites(favoriteDTO);
        reset(favoriteRepository);

        viewModel.refresh();
        verify(favoriteRepository).loadFavorites(favoriteDTO);
        reset(favoriteRepository);
        viewModel.stores.removeObserver(observer);

        viewModel.refresh();
        verifyNoMoreInteractions(favoriteRepository);
    }

    @Test
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }
}