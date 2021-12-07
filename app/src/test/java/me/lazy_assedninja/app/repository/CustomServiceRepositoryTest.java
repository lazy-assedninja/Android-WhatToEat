package me.lazy_assedninja.app.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createResult;
import static me.lazy_assedninja.app.util.ApiUtil.successCall;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.common.TestUtil;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Report;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class CustomServiceRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private CustomServiceRepository repository;
    private final WhatToEatService service = mock(WhatToEatService.class);

    @Before
    public void init() {
        repository = new CustomServiceRepository(new InstantExecutorUtil(), service);
    }

    @Test
    public void createReport() {
        Report report = TestUtil.createReport();
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.createReport(report)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.createReport(report).observeForever(observer);
        verify(service).createReport(report);

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }
}