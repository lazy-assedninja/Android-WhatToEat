package me.lazy_assedninja.what_to_eat.ui.store;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFavorite;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createRequestResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.HistoryRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.store.history.HistoryViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.RequestResult;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class HistoryViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final HistoryRepository historyRepository = mock(HistoryRepository.class);
    private final HistoryViewModel viewModel = new HistoryViewModel(userRepository,
            favoriteRepository, historyRepository);

    @Test
    public void testNull() {
        // Load stores
        assertThat(viewModel.stores, notNullValue());

        verify(historyRepository, never()).loadHistories(any());
        viewModel.requestHistory(new ArrayList<>());
        verify(historyRepository, never()).loadHistories(any());

        // Change favorite status
        assertThat(viewModel.result, notNullValue());

        verify(favoriteRepository, never()).changeFavoriteStatus(any());
        viewModel.changeFavoriteStatus(createFavorite());
        verify(favoriteRepository, never()).changeFavoriteStatus(any());
    }

    @Test
    public void sendResultToUI() {
        // Load stores
        List<Integer> ids = new ArrayList<>();
        MutableLiveData<List<Store>> list = new MutableLiveData<>();
        when(historyRepository.loadHistories(ids)).thenReturn(list);
        Observer<List<Store>> listObserver = mock(Observer.class);
        viewModel.stores.observeForever(listObserver);
        viewModel.requestHistory(ids);
        verify(listObserver, never()).onChanged(any());

        List<Store> listData = new ArrayList<>();
        list.setValue(listData);
        verify(listObserver).onChanged(listData);

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
    public void loadHistories() {
        viewModel.stores.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(historyRepository);

        List<Integer> ids = new ArrayList<>();
        viewModel.requestHistory(ids);
        verify(historyRepository).loadHistories(ids);
        verifyNoMoreInteractions(historyRepository);
    }

    @Test
    public void changeFavoriteStatus() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(favoriteRepository);

        Favorite favorite = createFavorite();
        viewModel.changeFavoriteStatus(favorite);
        verify(userRepository).getUserID();
        verify(favoriteRepository).changeFavoriteStatus(favorite);
        verifyNoMoreInteractions(favoriteRepository);
    }

    @Test
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }

    @Test
    public void getHistoryIDs() {
        viewModel.getHistoryIDs();

        verify(historyRepository).getHistoryIDs();
    }
}