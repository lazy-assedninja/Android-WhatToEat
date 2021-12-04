package me.lazy_assedninja.app.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import me.lazy_assedninja.app.api.ApiResponse;
import retrofit2.Response;

public class ApiUtil {

    public static <T> LiveData<ApiResponse<T>> successCall(T data) {
        return createCall(Response.success(data));
    }

    public static <T> LiveData<ApiResponse<T>> createCall(Response<T> response) {
        MutableLiveData<ApiResponse<T>> apiResponse = new MutableLiveData<>();
        apiResponse.setValue(ApiResponse.create(response));
        return apiResponse;
    }
}