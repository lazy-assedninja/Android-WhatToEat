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
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createHistory;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createStore;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.what_to_eat.repository.FavoriteRepository;
import me.lazy_assedninja.what_to_eat.repository.HistoryRepository;
import me.lazy_assedninja.what_to_eat.repository.StoreRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.store.store_information.StoreInformationViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.History;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class StoreInformationViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final StoreRepository storeRepository = mock(StoreRepository.class);
    private final HistoryRepository historyRepository = mock(HistoryRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final StoreInformationViewModel viewModel = new StoreInformationViewModel(userRepository,
            storeRepository, favoriteRepository, historyRepository);

    @Before
    public void getStore() {
        int storeID = 1;
        MutableLiveData<Store> store = new MutableLiveData<>();
        store.setValue(createStore(storeID, "store name"));
        when(storeRepository.getStoreFromDb(storeID)).thenReturn(store);

        viewModel.getStore(storeID);
        verify(storeRepository).getStoreFromDb(storeID);
    }

    @Test
    public void testNull() {
        assertThat(viewModel.result, notNullValue());

        verify(favoriteRepository, never()).changeFavoriteStatus(any());
        viewModel.changeFavoriteStatus(createFavorite());
        verify(favoriteRepository, never()).changeFavoriteStatus(any());
    }

    @Test
    public void sendResultToUI() {
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
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }

    @Test
    public void addToHistory() {
        History history = createHistory(1);
        viewModel.addToHistory(history);

        verify(historyRepository).addToHistory(history);
    }
}