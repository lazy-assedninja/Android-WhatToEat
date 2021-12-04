package me.lazy_assedninja.library.util;

import org.joda.time.DateTime;

import javax.inject.Inject;

@SuppressWarnings("unsed")
public class TimeUtil {

    @Inject
    public TimeUtil() {
    }

    public String now() {
        return DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}