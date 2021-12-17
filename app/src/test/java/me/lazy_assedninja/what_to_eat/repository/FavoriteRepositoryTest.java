package me.lazy_assedninja.what_to_eat.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFavorite;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFavoriteDTO;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createRequestResult;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createStore;
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

import me.lazy_assedninja.library.util.TimeUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.FavoriteDao;
import me.lazy_assedninja.what_to_eat.db.StoreDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.util.InstantExecutorUtil;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.RequestResult;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Store;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class FavoriteRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FavoriteRepository repository;
    private final TimeUtil timeUtil = mock(TimeUtil.class);
    private final StoreDao storeDao = mock(StoreDao.class);
    private final FavoriteDao favoriteDao = mock(FavoriteDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    private final String storeName = "store name";

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.storeDao()).thenReturn(storeDao);
        when(db.favoriteDao()).thenReturn(favoriteDao);
        repository = new FavoriteRepository(new InstantExecutorUtil(), timeUtil, storeDao,
                favoriteDao, service);
    }

    @Test
    public void loadFavoriteFromNetwork() {
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(favoriteDao.getFavorites(true)).thenReturn(dbData);

        FavoriteDTO favoriteDTO = createFavoriteDTO();
        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
        LiveData<ApiResponse<List<Store>>> call = successCall(list);
        when(service.getFavoriteList(favoriteDTO)).thenReturn(call);

        LiveData<Resource<List<Store>>> data = repository.loadFavorites(favoriteDTO);
        verify(favoriteDao).getFavorites(true);
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Store>> updateDbData = new MutableLiveData<>();
        when(favoriteDao.getFavorites(true)).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).getFavoriteList(favoriteDTO);
        verify(storeDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void loadFavoriteFromDb() {
        FavoriteDTO favoriteDTO = createFavoriteDTO();
        MutableLiveData<List<Store>> dbData = new MutableLiveData<>();
        when(favoriteDao.getFavorites(true)).thenReturn(dbData);

        Observer<Resource<List<Store>>> observer = mock(Observer.class);
        repository.loadFavorites(favoriteDTO).observeForever(observer);
        verify(favoriteDao).getFavorites(true);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, storeName));
        dbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void addToFavorite() {
        Favorite favorite = createFavorite();
        favorite.setStoreID(1);
        favorite.setStatus(true);
        RequestResult<Favorite> requestResult = createRequestResult(favorite);
        LiveData<ApiResponse<RequestResult<Favorite>>> call = successCall(requestResult);
        when(service.addToFavorite(favorite)).thenReturn(call);

        Observer<Event<Resource<RequestResult<Favorite>>>> observer = mock(Observer.class);
        repository.changeFavoriteStatus(favorite).observeForever(observer);
        verify(service).addToFavorite(favorite);
        verify(favoriteDao).updateFavoriteStatus(favorite.getStoreID(), favorite.getStatus());

        verify(observer).onChanged(new Event<>(Resource.success(requestResult)));
    }

    @Test
    public void addToFavoriteAndUpdateTime() {
        Favorite favorite = createFavorite();
        favorite.setStoreID(1);
        favorite.setStatus(true);
        favorite.setNeedUpdate(true);
        RequestResult<Favorite> requestResult = createRequestResult(favorite);
        LiveData<ApiResponse<RequestResult<Favorite>>> call = successCall(requestResult);
        when(service.addToFavorite(favorite)).thenReturn(call);

        Observer<Event<Resource<RequestResult<Favorite>>>> observer = mock(Observer.class);
        repository.changeFavoriteStatus(favorite).observeForever(observer);
        verify(service).addToFavorite(favorite);
        verify(favoriteDao).updateFavoriteStatusAndTime(favorite.getStoreID(), favorite.getStatus(),
                timeUtil.now());

        verify(observer).onChanged(new Event<>(Resource.success(requestResult)));
    }

    @Test
    public void cancelFavorite() {
        Favorite favorite = createFavorite();
        favorite.setStoreID(1);
        favorite.setStatus(false);
        RequestResult<Favorite> requestResult = createRequestResult(favorite);
        LiveData<ApiResponse<RequestResult<Favorite>>> call = successCall(requestResult);
        when(service.cancelFavorite(favorite)).thenReturn(call);

        Observer<Event<Resource<RequestResult<Favorite>>>> observer = mock(Observer.class);
        repository.changeFavoriteStatus(favorite).observeForever(observer);
        verify(service).cancelFavorite(favorite);
        verify(favoriteDao).updateFavoriteStatus(favorite.getStoreID(), favorite.getStatus());

        verify(observer).onChanged(new Event<>(Resource.success(requestResult)));
    }

    @Test
    public void cancelFavoriteAndUpdateTime() {
        Favorite favorite = createFavorite();
        favorite.setStoreID(1);
        favorite.setStatus(false);
        favorite.setNeedUpdate(true);
        RequestResult<Favorite> requestResult = createRequestResult(favorite);
        LiveData<ApiResponse<RequestResult<Favorite>>> call = successCall(requestResult);
        when(service.cancelFavorite(favorite)).thenReturn(call);

        Observer<Event<Resource<RequestResult<Favorite>>>> observer = mock(Observer.class);
        repository.changeFavoriteStatus(favorite).observeForever(observer);
        verify(service).cancelFavorite(favorite);
        verify(favoriteDao).updateFavoriteStatusAndTime(favorite.getStoreID(), favorite.getStatus(),
                timeUtil.now());

        verify(observer).onChanged(new Event<>(Resource.success(requestResult)));
    }
}