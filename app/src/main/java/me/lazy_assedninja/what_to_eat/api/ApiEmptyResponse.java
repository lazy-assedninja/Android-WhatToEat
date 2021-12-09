package me.lazy_assedninja.what_to_eat.api;

/**
 * Separate class for HTTP 204 responses so that we can make SuccessResponse's body non-null.
 */
public class ApiEmptyResponse<T> extends ApiResponse<T> {
}