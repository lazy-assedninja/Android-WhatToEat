package me.lazy_assedninja.what_to_eat.util;

import androidx.lifecycle.LiveData;

/**
 * A LiveData class that has `null` value.
 */
public class AbsentLiveData<T> extends LiveData<T> {

    private AbsentLiveData() {
        postValue(null);
    }

    public static <T> LiveData<T> create() {
        return new AbsentLiveData<>();
    }
}