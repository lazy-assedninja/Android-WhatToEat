package me.lazy_assedninja.app.common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.lazy_assedninja.library.util.ExecutorUtil;

@SuppressWarnings("unused")
public class CountingExecutorUtil {

    private final ExecutorUtil executorUtil;

    private final Object lock = new Object();

    private int taskCount = 0;

    public CountingExecutorUtil() {
        Counting counting = new Counting() {

            @Override
            public void increment() {
                synchronized (lock) {
                    taskCount++;
                }
            }

            @Override
            public void decrement() {
                synchronized (lock) {
                    taskCount--;
                    if (taskCount == 0) {
                        lock.notifyAll();
                    }
                }
            }
        };
        executorUtil = new ExecutorUtil(
                new CountingExecutor(counting),
                new CountingExecutor(counting),
                new CountingExecutor(counting)
        );
    }

    public ExecutorUtil getExecutorUtil() {
        return executorUtil;
    }

    public int taskCount(){
        synchronized(lock) {
            return taskCount;
        }
    }

    public void drainTasks(int time, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        long end = System.currentTimeMillis() + timeUnit.toMillis(time);
        while (true) {
            synchronized(lock) {
                if (taskCount == 0) return;
                long now = System.currentTimeMillis();
                long remaining = end - now;
                if (remaining > 0) {
                    lock.wait(remaining);
                } else {
                    throw new TimeoutException("Could not drain tasks.");
                }
            }
        }
    }

    private static class CountingExecutor implements Executor {

        private final Counting counting;
        private final Executor delegate = Executors.newSingleThreadExecutor();

        CountingExecutor(Counting counting) {
            this.counting = counting;
        }

        @Override
        public void execute(Runnable command) {
            counting.increment();
            delegate.execute(() -> {
                try {
                    command.run();
                } finally {
                    counting.decrement();
                }
            });
        }
    }

    private interface Counting {

        void increment();

        void decrement();
    }
}