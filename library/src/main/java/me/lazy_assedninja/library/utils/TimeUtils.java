package me.lazy_assedninja.library.utils;

import org.joda.time.DateTime;

import javax.inject.Inject;

@SuppressWarnings("unsed")
public class TimeUtils {

    @Inject
    public TimeUtils() {
    }

    public String now() {
        return DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}
