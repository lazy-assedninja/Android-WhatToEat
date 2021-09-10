package me.lazy_assedninja.app.api;

/**
 * Separate class for success responses.
 */
public class ApiSuccessResponse<T> extends ApiResponse<T> {
    private final T body;

    public ApiSuccessResponse(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }
}