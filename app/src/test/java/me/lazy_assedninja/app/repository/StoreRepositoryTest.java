package me.lazy_assedninja.app.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createReservation;
import static me.lazy_assedninja.app.common.TestUtil.createResult;
import static me.lazy_assedninja.app.common.TestUtil.createStore;
import static me.lazy_assedninja.app.common.TestUtil.createStoreDTO;
import static me.lazy_assedninja.app.util.ApiUtil.successCall;

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

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.ReservationDao;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.db.TagDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.util.NetworkUtil;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class StoreRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private StoreRepository repository;
    private final NetworkUtil networkUtil = mock(NetworkUtil.class);
    private final TagDao tagDao = mock(TagDao.class);
    private final StoreDao storeDao = mock(StoreDao.class);
    private final ReservationDao reservationDao = mock(ReservationDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    private final String storeName = "store name";

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.tagDao()).thenReturn(tagDao);
        when(db.storeDao()).thenReturn(storeDao);
        when(db.reservationDao()).thenReturn(reservationDao);
        doCallRealMethod().when(db).runInTransaction((Runnable) any());
        repository = new StoreRepository(new InstantExecutorUtil(),
                networkUtil, db, tagDao, storeDao, reservationDao, service);
    }

    @Test
    public void loadStoresFromNetwork() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setTagID(1);
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.getStores(storeDTO.getTagID())).thenReturn(dbData);

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
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
    public void loadStoresFromDb() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setTagID(1);
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.getStores(storeDTO.getTagID())).thenReturn(dbData);

        LiveData<Resource<List<Store>>> data = repository.loadStores(storeDTO);
        verify(storeDao).getStores(storeDTO.getTagID());
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
        dbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void loadAllStoresFromNetwork() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setTagID(1);
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.getStores()).thenReturn(dbData);

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
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
    public void loadAllStoresFromDb() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setTagID(1);
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.getStores()).thenReturn(dbData);

        LiveData<Resource<List<Store>>> data = repository.loadAllStores(storeDTO);
        verify(storeDao).getStores();
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
        dbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void searchFromNetwork() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setKeyword("keyword");
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.search("%" + storeDTO.getKeyword() + "%")).thenReturn(dbData);

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
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
    public void searchFromDb() {
        StoreDTO storeDTO = createStoreDTO();
        storeDTO.setKeyword("keyword");
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(storeDao.search("%" + storeDTO.getKeyword() + "%")).thenReturn(dbData);

        LiveData<Resource<List<Store>>> data = repository.search(storeDTO);
        verify(storeDao).search("%" + storeDTO.getKeyword() + "%");
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
        dbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void getStoreFromDbByID() {
        int id = 1;
        repository.getStoreFromDb(id);

        verify(storeDao).get(id);
    }

    @Test
    public void getStoreFromDbByName() {
        int name = 1;
        repository.getStoreFromDb(name);

        verify(storeDao).get(name);
    }

    @Test
    public void reserve() {
        Reservation reservation = createReservation();
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.createReservation(reservation)).thenReturn(call);

        LiveData<Event<Resource<Result>>> data = repository.reserve(reservation);
        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verify(service).createReservation(reservation);
        verify(reservationDao).insert(reservation);

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }
}