package me.lazy_assedninja.what_to_eat.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * A value holder that automatically clears the reference if the Fragment's view is destroyed.
 *
 * @param <T>
 */
public class AutoClearedValue<T> {

    private T value;

    public AutoClearedValue(Fragment fragment, T value) {
        fragment.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                fragment.getViewLifecycleOwnerLiveData().observe(fragment, lifecycleOwner ->
                        lifecycleOwner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                            @Override
                            public void onDestroy(@NonNull LifecycleOwner owner) {
                                AutoClearedValue.this.value = null;
                            }
                        })
                );
            }
        });
        this.value = value;
    }

    public T get() {
        return value;
    }
}