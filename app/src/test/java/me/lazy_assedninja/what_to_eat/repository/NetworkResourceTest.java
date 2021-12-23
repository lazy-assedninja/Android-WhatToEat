package me.lazy_assedninja.what_to_eat.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createNinja;
import static me.lazy_assedninja.what_to_eat.util.ApiUtil.createCall;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.common.CountingExecutorUtil;
import me.lazy_assedninja.what_to_eat.common.Ninja;
import me.lazy_assedninja.what_to_eat.util.InstantExecutorUtil;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@SuppressWarnings("unchecked")
@RunWith(Parameterized.class)
public class NetworkResourceTest {

    @Parameters
    public static List<Boolean> param() {
        return Arrays.asList(true);
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private final boolean useRealExecutors;
    private CountingExecutorUtil countingExecutorUtil;
    private ExecutorUtil executorUtil;

    public NetworkResourceTest(boolean useRealExecutors) {
        this.useRealExecutors = useRealExecutors;
        if (useRealExecutors) {
            countingExecutorUtil = new CountingExecutorUtil();
        }
    }

    @Before
    public void init() {
        executorUtil = (useRealExecutors) ? countingExecutorUtil.getExecutorUtil() :
                new InstantExecutorUtil();
    }

    @Test
    public void basicFromNetwork() {
        AtomicReference<Ninja> saved = new AtomicReference<>();
        Ninja networkResult = createNinja();
        Consumer<Ninja> handleSaveCallResult = saved::set;
        Supplier<LiveData<ApiResponse<Ninja>>> handleCreateCall = () ->
                createCall(Response.success(networkResult));
        NetworkResource<Ninja, Void> networkResource = new NetworkResource<Ninja, Void>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Ninja>> createCall() {
                return handleCreateCall.get();
            }

            @Override
            protected Void saveCallResult(Ninja item) {
                handleSaveCallResult.accept(item);
                return null;
            }
        };

        Observer<Event<Resource<Ninja>>> observer = mock(Observer.class);
        networkResource.asLiveData().observeForever(observer);
        drain();
        assertThat(saved.get(), is(networkResult));
        if (useRealExecutors) verify(observer).onChanged(new Event<>(Resource.loading(null)));
        verify(observer).onChanged(new Event<>(Resource.success(networkResult)));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void failureFromNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        Consumer<Ninja> handleSaveCallResult = ninja -> saved.set(true);
        ResponseBody body = ResponseBody.create("Error.",
                MediaType.parse("application/txt"));
        Supplier<LiveData<ApiResponse<Ninja>>> handleCreateCall = () ->
                createCall(Response.error(500, body));
        NetworkResource<Ninja, Void> networkResource = new NetworkResource<Ninja, Void>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Ninja>> createCall() {
                return handleCreateCall.get();
            }

            @Override
            protected Void saveCallResult(Ninja item) {
                handleSaveCallResult.accept(item);
                return null;
            }
        };

        Observer<Event<Resource<Ninja>>> observer = mock(Observer.class);
        networkResource.asLiveData().observeForever(observer);
        drain();
        assertThat(saved.get(), is(false));
        if (useRealExecutors) verify(observer).onChanged(new Event<>(Resource.loading(null)));
        verify(observer).onChanged(new Event<>(Resource.error("Error.", null)));
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