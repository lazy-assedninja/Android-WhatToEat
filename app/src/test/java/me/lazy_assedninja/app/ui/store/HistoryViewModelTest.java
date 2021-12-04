package me.lazy_assedninja.app.ui.store;

import static org.hamcrest.CoreMatchers.is;
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

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.app.repository.FavoriteRepository;
import me.lazy_assedninja.app.repository.HistoryRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.store.history.HistoryViewModel;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class HistoryViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final HistoryRepository historyRepository = mock(HistoryRepository.class);
    private final HistoryViewModel viewModel = new HistoryViewModel(new InstantExecutorUtil(),
            userRepository, favoriteRepository, historyRepository);

    @Test
    public void testNull() {
        // Load stores
        assertThat(viewModel.stores, notNullValue());

        verify(historyRepository, never()).loadHistories(any());
        viewModel.requestHistory();
        verify(historyRepository, never()).loadHistories(any());

        // Change favorite status
        assertThat(viewModel.result, notNullValue());

        verify(favoriteRepository, never()).changeFavoriteStatus(any());
        viewModel.setFavoriteRequest(1, true);
        verify(favoriteRepository, never()).changeFavoriteStatus(any());
    }

    @Test
    public void sendResultToUI() {
        // Load stores
        MutableLiveData<List<Store>> list = new MutableLiveData<>();
        when(historyRepository.loadHistories(any())).thenReturn(list);
        Observer<List<Store>> listObserver = mock(Observer.class);
        viewModel.stores.observeForever(listObserver);
        viewModel.requestHistory();
        verify(listObserver, never()).onChanged(any());

        List<Store> listData = new ArrayList<>();
        list.setValue(listData);
        verify(listObserver).onChanged(listData);

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
    public void loadHistories() {
        viewModel.stores.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(historyRepository);
        viewModel.requestHistory();
        verify(historyRepository).getHistoryIDs();
        verify(historyRepository).loadHistories(any());
        verifyNoMoreInteractions(historyRepository);
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
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }
}
