package me.lazy_assedninja.library.utils;

import androidx.viewbinding.BuildConfig;

@SuppressWarnings("unused")
public class LogUtils {
    private static final boolean isDebug = BuildConfig.DEBUG;

    public static void v(String tag, String msg) {
        if (isDebug) return;
        android.util.Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug) return;
        android.util.Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug) return;
        android.util.Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isDebug) return;
        android.util.Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug) return;
        android.util.Log.e(tag, msg);
    }
}
