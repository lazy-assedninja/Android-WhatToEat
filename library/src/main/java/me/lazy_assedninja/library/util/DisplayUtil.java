package me.lazy_assedninja.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Insets;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.RequiresApi;

@SuppressWarnings({"RedundantSuppression", "deprecation", "unused"})
public class DisplayUtil {

    private final Context context;

    public DisplayUtil(Context context) {
        this.context = context;
    }

    public static int dpToPx(float dp, Context context) {
        return dpToPx(dp, context.getResources());
    }

    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
        return (int) px;
    }

    public int getScreenHeightPix() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets insets = getInsets();
            return getWindowMetrics().getBounds().height() - insets.top - insets.bottom;
        } else {
            return getDisplayMetrics().heightPixels;
        }
    }

    public int getScreenWidthPix() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets insets = getInsets();
            return getWindowMetrics().getBounds().height() - insets.left - insets.right;
        } else {
            return getDisplayMetrics().widthPixels;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private WindowMetrics getWindowMetrics() {
        return ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE)).getCurrentWindowMetrics();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private Insets getInsets() {
        return getWindowMetrics().getWindowInsets()
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
    }

    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
}