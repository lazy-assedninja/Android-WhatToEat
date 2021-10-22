package me.lazy_assedninja.app.repository;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import me.lazy_assedninja.app.api.ApiEmptyResponse;
import me.lazy_assedninja.app.api.ApiErrorResponse;
import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.ApiSuccessResponse;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.ExecutorUtils;

/**
 * A generic class that can provide a resource backed by the network.
 *
 * @param <T>
 */
public abstract class NetworkResource<T> {

    private final ExecutorUtils executorUtils;

    private final MediatorLiveData<Event<Resource<T>>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkResource(ExecutorUtils executorUtils) {
        this.executorUtils = executorUtils;

        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        setValue(Resource.loading(null));

        LiveData<ApiResponse<T>> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);

            Resource<T> resource;
            if (response instanceof ApiSuccessResponse) {
                executorUtils.diskIO().execute(() ->
                        saveCallResult(processResponse((ApiSuccessResponse<T>) response)));

                resource = (Resource.success(((ApiSuccessResponse<T>) response).getBody()));
            } else if (response instanceof ApiEmptyResponse) {
                resource = Resource.error("No response.", null);
            } else if (response instanceof ApiErrorResponse) {
                onFetchFailed();
                resource = Resource.error(
                        ((ApiErrorResponse<Result>) response).getErrorMessage(), null);
            } else {
                resource = Resource.error("Unknown error.", null);
            }
            setValue(resource);
        });
    }

    @MainThread
    private void setValue(Resource<T> newValue) {
        if (result.getValue() == null || result.getValue().peekContent() != newValue) {
            result.setValue(new Event<>(newValue));
        }
    }

    @MainThread
    protected abstract LiveData<ApiResponse<T>> createCall();

    @WorkerThread
    protected void saveCallResult(T item) {
    }

    @WorkerThread
    protected T processResponse(ApiSuccessResponse<T> response) {
        return response.getBody();
    }

    protected void onFetchFailed() {
    }

    public LiveData<Event<Resource<T>>> asLiveData() {
        return result;
    }
}