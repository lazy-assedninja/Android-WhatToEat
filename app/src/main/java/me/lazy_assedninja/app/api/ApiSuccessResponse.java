package me.lazy_assedninja.app.api;

public class ApiSuccessResponse<T> extends ApiResponse<T> {
    private T body;

    public ApiSuccessResponse(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}