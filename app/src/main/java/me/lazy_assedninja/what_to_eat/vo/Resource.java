package me.lazy_assedninja.what_to_eat.vo;

import static me.lazy_assedninja.what_to_eat.vo.Status.ERROR;
import static me.lazy_assedninja.what_to_eat.vo.Status.LOADING;
import static me.lazy_assedninja.what_to_eat.vo.Status.SUCCESS;

import java.util.Objects;

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T> </T>
 */
public class Resource<T> {

    private final Status status;
    private T data;
    private final String message;

    public Resource(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(LOADING, data, null);
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String message, T data) {
        return new Resource<>(ERROR, data, message);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        else if (object == null || getClass() != object.getClass()) return false;

        Resource<?> resource = (Resource<?>) object;
        if (status != resource.status) return false;
        else if (!Objects.equals(message, resource.message)) return false;
        else return Objects.equals(data, resource.data);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = result + (message != null ? message.hashCode() : 0)
                + (data != null ? data.hashCode() : 0);
        return result;
    }
}