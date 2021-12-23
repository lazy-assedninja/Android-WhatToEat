package me.lazy_assedninja.what_to_eat.api;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Common class used by API responses.
 *
 * @param <T> the type of the response object
 */
@SuppressWarnings("unused") // T is used in extending classes
public class ApiResponse<T> {

    public static <T> ApiErrorResponse<T> create(Throwable error) {
        return new ApiErrorResponse<>(error.getMessage() == null ? "Unknown error." : error.getMessage());
    }

    public static <T> ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();
            if (body == null && response.code() == 204) {
                return new ApiEmptyResponse<>();
            } else {
                return new ApiSuccessResponse<>(body);
            }
        } else {
            try {
                ResponseBody errorBody = response.errorBody();
                if (errorBody != null) {
                    String errorMessage = errorBody.string();
                    if (!errorMessage.isEmpty()) {
                        return new ApiErrorResponse<>(errorMessage);
                    } else {
                        return new ApiErrorResponse<>(response.message().isEmpty() ?
                                "Unknown error." : response.message());
                    }
                } else {
                    return new ApiErrorResponse<>("Unknown error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new ApiErrorResponse<>(e.toString());
            }
        }
    }
}