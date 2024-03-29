package me.lazy_assedninja.what_to_eat.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createStore;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createStoreDTO;
import static me.lazy_assedninja.what_to_eat.util.ApiUtil.successCall;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.StoreDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.util.InstantExecutorUtil;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;
import me.lazy_assedninja.what_to_eat.vo.Tag;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class StoreRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private StoreRepository repository;
    private final StoreDao storeDao = mock(StoreDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    private final int storeID = 1;
    private final String storeName = "store name";
    private final String keyword = "keyword";

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.storeDao()).thenReturn(storeDao);
        repository = new StoreRepository(new InstantExecutorUtil(), storeDao, service);
    }

    @Test
    public void loadStoresFromNetwork() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setTagID(Tag.HOME.getValue());
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.getStores(storeDTO.getTagID())).thenReturn(dbData);

        List<Store> list = new ArrayList<>();
        list.add(createStore(storeID, storeName));
        LiveData<ApiResponse<List<Store>>> call = successCall(list);
        when(service.getStoreList(storeDTO)).thenReturn(call);

        LiveData<Resource<List<Store>>> data = repository.loadStores(storeDTO);
        verify(storeDao).getStores(storeDTO.getTagID());
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Store>> updateDbData = new MutableLiveData<>();
        when(storeDao.getStores(storeDTO.getTagID())).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).getStoreList(storeDTO);
        verify(storeDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void loadAllStoresFromNetwork() {
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.getStores()).thenReturn(dbData);

        StoreDTO storeDTO = createStoreDTO();
        List<Store> list = new ArrayList<>();
        list.add(createStore(storeID, storeName));
        LiveData<ApiResponse<List<Store>>> call = successCall(list);
        when(service.getAllStores(storeDTO)).thenReturn(call);

        LiveData<Resource<List<Store>>> data = repository.loadAllStores(storeDTO);
        verify(storeDao).getStores();
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Store>> updateDbData = new MutableLiveData<>();
        when(storeDao.getStores()).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).getAllStores(storeDTO);
        verify(storeDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void searchFromNetwork() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setKeyword(keyword);
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.search("%" + storeDTO.getKeyword() + "%")).thenReturn(dbData);

        List<Store> list = new ArrayList<>();
        list.add(createStore(storeID, storeName));
        LiveData<ApiResponse<List<Store>>> call = successCall(list);
        when(service.search(storeDTO)).thenReturn(call);

        LiveData<Resource<List<Store>>> data = repository.search(storeDTO);
        verify(storeDao).search("%" + storeDTO.getKeyword() + "%");
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Store>> updateDbData = new MutableLiveData<>();
        when(storeDao.search("%" + storeDTO.getKeyword() + "%")).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).search(storeDTO);
        verify(storeDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void getStoreFromDbByID() {
        repository.getStoreFromDb(storeID);

        verify(storeDao).get(storeID);
    }

    @Test
    public void getStoreFromDbByName() {
        repository.getStoreFromDb(storeName);

        verify(storeDao).get(storeName);
    }
}