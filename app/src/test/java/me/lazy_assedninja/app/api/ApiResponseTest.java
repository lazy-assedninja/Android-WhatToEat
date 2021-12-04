package me.lazy_assedninja.app.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class ApiResponseTest {

    @Test
    public void success() {
        ApiSuccessResponse<String> apiResponse =
                (ApiSuccessResponse<String>) ApiResponse.create(Response.success("Success."));
        assertThat(apiResponse.getBody(), is("Success."));
    }

    @Test
    public void error() {
        Response<String> errorResponse = Response.error(500,
                ResponseBody.create("Error.", MediaType.parse("application/txt")));
        String errorMessage = ((ApiErrorResponse<String>) ApiResponse.create(errorResponse))
                .getErrorMessage();
        assertThat(errorMessage, is("Error."));
    }

    @Test
    public void exception() {
        Exception exception = new Exception("Exception.");
        String errorMessage = ApiResponse.create(exception).getErrorMessage();
        assertThat(errorMessage, is("Exception."));
    }
}