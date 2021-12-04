package me.lazy_assedninja.library.util;

import javax.inject.Inject;

import me.lazy_assedninja.library.BuildConfig;

@SuppressWarnings("unused")
public class LogUtil {
    private static final boolean isDebug = BuildConfig.DEBUG;

    @Inject
    public LogUtil() {
    }

    public void v(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.v(tag, msg);
    }

    public void d(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.d(tag, msg);
    }

    public void i(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.i(tag, msg);
    }

    public void w(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.w(tag, msg);
    }

    public void e(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.e(tag, msg);
    }
}