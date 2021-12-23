package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.api.ApiEmptyResponse;
import me.lazy_assedninja.what_to_eat.api.ApiErrorResponse;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.ApiSuccessResponse;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Resource;

/**
 * A generic class that can provide a resource backed by the network.
 *
 * @param <ResultType>
 * @param <DbResultType>
 */
@SuppressWarnings("unused")
public abstract class NetworkResource<ResultType, DbResultType> {

    private final ExecutorUtil executorUtil;

    private final MediatorLiveData<Event<Resource<ResultType>>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkResource(ExecutorUtil executorUtil) {
        this.executorUtil = executorUtil;

        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        setValue(Resource.loading(null));

        LiveData<ApiResponse<ResultType>> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);

            if (response instanceof ApiSuccessResponse) {
                executorUtil.diskIO().execute(() -> {
                    ResultType result = processResponse((ApiSuccessResponse<ResultType>) response);
                    DbResultType dbResult = saveCallResult(result);
                    Resource<ResultType> resource = processResource(Resource.success(result));
                    executorUtil.mainThread().execute(() -> setValue(resource));
                });
            } else if (response instanceof ApiEmptyResponse) {
                Resource<ResultType> resource = processResource(Resource.success(null));
                executorUtil.mainThread().execute(() -> setValue(resource));
            } else if (response instanceof ApiErrorResponse) {
                onFetchFailed();

                Resource<ResultType> resource = processResource(Resource.error(
                        ((ApiErrorResponse<ResultType>) response).getErrorMessage(), null));
                executorUtil.mainThread().execute(() -> setValue(resource));
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (result.getValue() == null || result.getValue().peekContent() != newValue) {
            result.setValue(new Event<>(newValue));
        }
    }

    @MainThread
    protected abstract LiveData<ApiResponse<ResultType>> createCall();

    @WorkerThread
    protected DbResultType saveCallResult(ResultType item) {
        return null;
    }

    @WorkerThread
    protected ResultType processResponse(ApiSuccessResponse<ResultType> response) {
        return response.getBody();
    }

    @WorkerThread
    protected Resource<ResultType> processResource(Resource<ResultType> resource) {
        return resource;
    }

    protected void onFetchFailed() {
    }

    public LiveData<Event<Resource<ResultType>>> asLiveData() {
        return result;
    }
}