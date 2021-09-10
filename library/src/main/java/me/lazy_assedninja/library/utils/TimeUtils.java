package me.lazy_assedninja.library.utils;

import java.text.DateFormat;

import javax.inject.Inject;

@SuppressWarnings("unsed")
public class TimeUtils {

    @Inject
    public TimeUtils() {
    }

    public String dateTime(long time) {
        return DateFormat.getDateTimeInstance().format(time);
    }
}
