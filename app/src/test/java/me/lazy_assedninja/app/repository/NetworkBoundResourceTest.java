package me.lazy_assedninja.app.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static me.lazy_assedninja.app.common.TestUtil.createNinja;
import static me.lazy_assedninja.app.util.ApiUtil.createCall;

import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.common.CountingExecutorUtil;
import me.lazy_assedninja.app.common.Ninja;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.util.ExecutorUtil;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@SuppressWarnings("unchecked")
@RunWith(Parameterized.class)
public class NetworkBoundResourceTest {

    @Parameters
    public static List<Boolean> param() {
        return Arrays.asList(true, false);
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private final boolean useRealExecutors;
    private CountingExecutorUtil countingExecutorUtil;

    private Consumer<Ninja> handleSaveCallResult;
    private Predicate<Ninja> handleShouldFetch;
    private Supplier<LiveData<ApiResponse<Ninja>>> handleCreateCall;
    private NetworkBoundResource<Ninja, Ninja> networkBoundResource;
    private final MutableLiveData<Ninja> dbData = new MutableLiveData<>();
    private final AtomicBoolean fetchedOnce = new AtomicBoolean(false);

    public NetworkBoundResourceTest(boolean useRealExecutors) {
        this.useRealExecutors = useRealExecutors;
        if (useRealExecutors) {
            countingExecutorUtil = new CountingExecutorUtil();
        }
    }

    @Before
    public void init() {
        ExecutorUtil executorUtil = (useRealExecutors) ? countingExecutorUtil.getExecutorUtil() :
                new InstantExecutorUtil();
        networkBoundResource = new NetworkBoundResource<>(executorUtil) {

            @Override
            protected LiveData<Ninja> loadFromDb() {
                return dbData;
            }

            @Override
            protected Boolean shouldFetch(@Nullable Ninja data) {
                return handleShouldFetch.test(data) &&
                        fetchedOnce.compareAndSet(false, true);
            }

            @Override
            protected LiveData<ApiResponse<Ninja>> createCall() {
                return handleCreateCall.get();
            }

            @Override
            protected void saveCallResult(Ninja item) {
                handleSaveCallResult.accept(item);
            }
        };
    }

    @Test
    public void basicFromNetwork() {
        AtomicReference<Ninja> saved = new AtomicReference<>();
        Ninja fetchedDbValue = createNinja();
        Ninja networkResult = createNinja();
        handleShouldFetch = Objects::isNull;
        handleSaveCallResult = ninja -> {
            saved.set(ninja);
            dbData.setValue(fetchedDbValue);
        };
        handleCreateCall = () -> createCall(Response.success(networkResult));

        Observer<Resource<Ninja>> observer = mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.loading(null));

        dbData.setValue(null);
        drain();
        assertThat(saved.get(), is(networkResult));
        verify(observer).onChanged(Resource.success(fetchedDbValue));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void failureFromNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        handleShouldFetch = Objects::isNull;
        handleSaveCallResult = ninja -> saved.set(true);
        ResponseBody body = ResponseBody.create("Error.",
                MediaType.parse("application/txt"));
        handleCreateCall = () -> createCall(Response.error(500, body));

        Observer<Resource<Ninja>> observer = mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.loading(null));

        dbData.setValue(null);
        drain();
        assertThat(saved.get(), is(false));
        verify(observer).onChanged(Resource.error("Error.", null));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithoutNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        handleShouldFetch = Objects::isNull;
        handleSaveCallResult = ninja -> saved.set(true);
        MutableLiveData<ApiResponse<Ninja>> apiResponse = new MutableLiveData<>();
        handleCreateCall = () -> apiResponse;

        Ninja dbValue1 = createNinja();
        Ninja dbValue2 = createNinja();
        Observer<Resource<Ninja>> observer = mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.loading(null));

        dbData.setValue(dbValue1);
        drain();
        verify(observer).onChanged(Resource.success(dbValue1));
        assertThat(saved.get(), is(false));

        dbData.setValue(dbValue2);
        drain();
        verify(observer).onChanged(Resource.success(dbValue2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithFetchFailure() {
        Ninja dbValue1 = createNinja();
        Ninja dbValue2 = createNinja();
        AtomicBoolean saved = new AtomicBoolean(false);
        handleShouldFetch = ninja -> ninja == dbValue1;
        handleSaveCallResult = ninja -> saved.set(true);
        ResponseBody body = ResponseBody.create("Error.",
                MediaType.parse("application/txt"));
        MutableLiveData<ApiResponse<Ninja>> apiResponse = new MutableLiveData<>();
        handleCreateCall = () -> apiResponse;

        Observer<Resource<Ninja>> observer = mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.loading(null));

        dbData.setValue(dbValue1);
        drain();
        verify(observer).onChanged(Resource.loading(dbValue1));

        apiResponse.setValue(ApiResponse.create(Response.error(400, body)));
        drain();
        assertThat(saved.get(), is(false));
        verify(observer).onChanged(Resource.error("Error.", dbValue1));

        dbData.setValue(dbValue2);
        drain();
        verify(observer).onChanged(Resource.error("Error.", dbValue2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithReFetchSuccess() {
        Ninja dbValue1 = createNinja();
        Ninja dbValue2 = createNinja();
        AtomicReference<Ninja> saved = new AtomicReference<>();
        handleShouldFetch = ninja -> ninja == dbValue1;
        handleSaveCallResult = ninja -> {
            saved.set(ninja);
            dbData.setValue(dbValue2);
        };
        MutableLiveData<ApiResponse<Ninja>> apiResponse = new MutableLiveData<>();
        handleCreateCall = () -> apiResponse;

        Observer<Resource<Ninja>> observer = mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.loading(null));

        dbData.setValue(dbValue1);
        drain();
        verify(observer).onChanged(Resource.loading(dbValue1));

        Ninja networkResult = createNinja();
        apiResponse.setValue(ApiResponse.create(Response.success(networkResult)));
        drain();
        assertThat(saved.get(), is(networkResult));
        verify(observer).onChanged(Resource.success(dbValue2));
        verifyNoMoreInteractions(observer);
    }

    private void drain() {
        if (!useRealExecutors) return;
        try {
            countingExecutorUtil.drainTasks(1, TimeUnit.SECONDS);
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }
}