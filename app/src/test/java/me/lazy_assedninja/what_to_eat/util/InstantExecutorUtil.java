package me.lazy_assedninja.what_to_eat.util;

import java.util.concurrent.Executor;

import me.lazy_assedninja.library.util.ExecutorUtil;

public class InstantExecutorUtil extends ExecutorUtil {

    private final static Executor instant = Runnable::run;

    public InstantExecutorUtil() {
        super(instant, instant, instant);
    }
}
