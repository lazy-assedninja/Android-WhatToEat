package me.lazy_assedninja.what_to_eat.vo;

public class RequestResult<T> {

    private String result;
    private T request;

    public RequestResult(String result, T request) {
        this.result = result;
        this.request = request;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }
}