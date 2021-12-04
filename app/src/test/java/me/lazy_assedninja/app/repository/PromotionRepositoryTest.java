package me.lazy_assedninja.app.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createPromotion;
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
import me.lazy_assedninja.app.db.PromotionDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.util.NetworkUtil;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class PromotionRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PromotionRepository repository;
    private final NetworkUtil networkUtil = mock(NetworkUtil.class);
    private final PromotionDao promotionDao = mock(PromotionDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.promotionDao()).thenReturn(promotionDao);
        doCallRealMethod().when(db).runInTransaction((Runnable) any());
        repository = new PromotionRepository(new InstantExecutorUtil(),
                networkUtil, db, promotionDao, service);
    }

    @Test
    public void loadPromotionFromNetwork() {
        MutableLiveData<List<Promotion>> dbData = new MutableLiveData<>();
        when(promotionDao.getPromotions()).thenReturn(dbData);

        List<Promotion> list = new ArrayList<>();
        list.add(createPromotion(1, "promotion title"));
        LiveData<ApiResponse<List<Promotion>>> call = successCall(list);
        when(service.getPromotionList()).thenReturn(call);

        LiveData<Resource<List<Promotion>>> data = repository.loadPromotions();
        verify(promotionDao).getPromotions();
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Promotion>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Promotion>> updateDbData = new MutableLiveData<>();
        when(promotionDao.getPromotions()).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).getPromotionList();
        verify(promotionDao).delete();
        verify(promotionDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void loadPromotionFromDb() {
        MutableLiveData<List<Promotion>> dbData = new MutableLiveData<>();
        when(promotionDao.getPromotions()).thenReturn(dbData);

        LiveData<Resource<List<Promotion>>> data = repository.loadPromotions();
        verify(promotionDao).getPromotions();
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Promotion>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));

        List<Promotion> list = new ArrayList<>();
        list.add(createPromotion(1, "promotion title"));
        dbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void getPromotionFromDb() {
        int id = 1;
        repository.getPromotionFromDb(id);

        verify(promotionDao).get(id);
    }
}