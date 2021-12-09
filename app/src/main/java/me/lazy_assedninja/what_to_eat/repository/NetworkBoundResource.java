package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import me.lazy_assedninja.what_to_eat.api.ApiEmptyResponse;
import me.lazy_assedninja.what_to_eat.api.ApiErrorResponse;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.ApiSuccessResponse;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.library.util.ExecutorUtil;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final ExecutorUtil executorUtil;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource(ExecutorUtil executorUtil) {
        this.executorUtil = executorUtil;

        setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData ->
                        setValue(Resource.success(newData)));
            }
        });
    }

    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        // We re-attach dbSource as a new source, it will dispatch its latest value quickly.
        result.addSource(dbSource, newData ->
                setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if (response instanceof ApiSuccessResponse) {
                executorUtil.diskIO().execute(() -> {
                    saveCallResult(processResponse((ApiSuccessResponse<RequestType>) response));
                    executorUtil.mainThread().execute(() ->
                            // We specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(), newData ->
                                    setValue(Resource.success(newData))));
                });
            } else if (response instanceof ApiEmptyResponse) {
                executorUtil.mainThread().execute(() ->
                        // Reload from disk whatever we had.
                        result.addSource(loadFromDb(), newData ->
                                setValue(Resource.success(newData))));
            } else if (response instanceof ApiErrorResponse) {
                onFetchFailed();
                result.addSource(dbSource, newData ->
                        setValue(Resource.error(((ApiErrorResponse<RequestType>) response).getErrorMessage(), newData)));
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @MainThread
    protected abstract Boolean shouldFetch(@Nullable ResultType data);

    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    @WorkerThread
    protected RequestType processResponse(ApiSuccessResponse<RequestType> response) {
        return response.getBody();
    }

    protected void onFetchFailed() {
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }
}