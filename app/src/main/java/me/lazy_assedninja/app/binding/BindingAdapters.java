package me.lazy_assedninja.app.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;

/**
 * Data Binding adapters specific to the app.
 */
@SuppressWarnings("unused")
public class BindingAdapters {

    @BindingAdapter("visibleOrGone")
    public static void visibleOrGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}