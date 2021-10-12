package me.lazy_assedninja.app.ui.store.map.map_information;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.vo.Store;

@HiltViewModel
public class MapInformationViewModel extends ViewModel {

    private final StoreRepository storeRepository;

    private Store store;

    @Inject
    public MapInformationViewModel(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public LiveData<Store> getStore(String name) {
        return storeRepository.getStoreFromDb(name);
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
