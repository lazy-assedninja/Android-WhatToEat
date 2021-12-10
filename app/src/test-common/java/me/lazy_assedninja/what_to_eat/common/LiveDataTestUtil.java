package me.lazy_assedninja.what_to_eat.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 * <p>
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 */
public class LiveDataTestUtil {

    public static <T> T getOrAwaitValue(LiveData<T> liveData) throws TimeoutException, InterruptedException {
        List<T> data = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data.add(t);
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(2L, TimeUnit.SECONDS)) {
            liveData.removeObserver(observer);
            throw new TimeoutException("LiveData value was never set.");
        }


        return data.get(0);
    }
}
