package me.lazy_assedninja.what_to_eat.util;

import android.os.SystemClock;
import android.util.ArrayMap;

import java.util.concurrent.TimeUnit;

/**
 * Utility class that decides whether we should fetch some data or not.
 */
public class RateLimiter<KEY> {

    private final ArrayMap<KEY, Long> timestamps = new ArrayMap<>();
    private final long timeout;

    public RateLimiter(int timeout, TimeUnit timeUnit) {
        this.timeout = timeUnit.toMillis(timeout);
    }

    public synchronized boolean shouldFetch(KEY key) {
        Long lastFetched = timestamps.get(key);
        long now = now();
        if (lastFetched == null) {
            timestamps.put(key, now);
            return true;
        }
        if (now - lastFetched > timeout) {
            timestamps.put(key, now);
            return true;
        }
        return false;
    }

    private long now() {
        return SystemClock.uptimeMillis();
    }

    public synchronized void reset(KEY key) {
        timestamps.remove(key);
    }
}