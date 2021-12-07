package me.lazy_assedninja.app.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createFile;
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
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.util.TimeUtil;
import okhttp3.MultipartBody;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class FileRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FileRepository repository;
    private final TimeUtil timeUtil = mock(TimeUtil.class);
    private final UserDao userDao = mock(UserDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.userDao()).thenReturn(userDao);
        repository = new FileRepository(new InstantExecutorUtil(), timeUtil, userDao, service);
    }

    @Test
    public void upload() {
        MultipartBody.Part file = createFile();
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.upload(file)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.upload(file).observeForever(observer);
        verify(service).upload(file);

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }
}