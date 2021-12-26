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
import org.junit.runners.JUnit4;
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
@RunWith(JUnit4.class)
public class NetworkResourceTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private ExecutorUtil executorUtil;

    @Before
    public void init() {
        executorUtil = new InstantExecutorUtil();
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
        assertThat(saved.get(), is(networkResult));
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
        assertThat(saved.get(), is(false));
        verify(observer).onChanged(new Event<>(Resource.error("Error.", null)));
        verifyNoMoreInteractions(observer);
    }
}