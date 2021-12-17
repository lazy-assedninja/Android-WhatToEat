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
import me.lazy_assedninja.what_to_eat.vo.RequestResult;
import me.lazy_assedninja.what_to_eat.vo.Resource;

/**
 * A generic class that can provide a resource backed by the network and request parameter.
 */
@SuppressWarnings("unused")
public abstract class NetworkResultWithRequest<RequestType, ResultType> {

    private final ExecutorUtil executorUtil;
    private final RequestType request;

    private final MediatorLiveData<Event<Resource<RequestResult<RequestType>>>> result =
            new MediatorLiveData<>();

    @MainThread
    public NetworkResultWithRequest(ExecutorUtil executorUtil, RequestType request) {
        this.executorUtil = executorUtil;
        this.request = request;

        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        setValue(Resource.loading(null));

        LiveData<ApiResponse<RequestResult<RequestType>>> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);

            if (response instanceof ApiSuccessResponse) {
                executorUtil.diskIO().execute(() -> {
                    ResultType dbResult = saveCallResult(
                            processResponse((ApiSuccessResponse<RequestResult<RequestType>>) response));
                    Resource<RequestResult<RequestType>> resource = Resource.success(
                            ((ApiSuccessResponse<RequestResult<RequestType>>) response).getBody());
                    resource.getData().setRequest(request);
                    postValue(resource);
                });
            } else if (response instanceof ApiEmptyResponse) {
                setValue(Resource.error("No response.", null));
            } else if (response instanceof ApiErrorResponse) {
                onFetchFailed();
                setValue(Resource.error(((ApiErrorResponse<RequestResult<RequestType>>) response)
                                .getErrorMessage(), null));
            } else {
                setValue(Resource.error("Unknown error.", null));
            }
        });
    }

    @MainThread
    private void setValue(Resource<RequestResult<RequestType>> newValue) {
        if (result.getValue() == null || result.getValue().peekContent() != newValue) {
            result.setValue(new Event<>(newValue));
        }
    }

    @WorkerThread
    private void postValue(Resource<RequestResult<RequestType>> newValue) {
        if (result.getValue() == null || result.getValue().peekContent() != newValue) {
            result.postValue(new Event<>(newValue));
        }
    }

    @MainThread
    protected abstract LiveData<ApiResponse<RequestResult<RequestType>>> createCall();

    @WorkerThread
    protected abstract ResultType saveCallResult(RequestResult<RequestType> item);

    @WorkerThread
    protected RequestResult<RequestType> processResponse(
            ApiSuccessResponse<RequestResult<RequestType>> response) {
        return response.getBody();
    }

    protected void onFetchFailed() {
    }

    public LiveData<Event<Resource<RequestResult<RequestType>>>> asLiveData() {
        return result;
    }
}