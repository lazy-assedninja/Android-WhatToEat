package me.lazy_assedninja.app.ui.store;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.ui.store.map.map_information.MapInformationViewModel;

@RunWith(JUnit4.class)
public class MapInformationViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final StoreRepository storeRepository = mock(StoreRepository.class);
    private final MapInformationViewModel viewModel = new MapInformationViewModel(storeRepository);

    @Test
    public void getStore() {
        String name = "store name";
        viewModel.getStore(name);

        verify(storeRepository).getStoreFromDb(name);
    }
}