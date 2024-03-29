package me.lazy_assedninja.what_to_eat.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createCommentDTO;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFavorite;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFavoriteDTO;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createGoogleAccount;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createPostDTO;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createReservationDTO;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createStoreDTO;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createUser;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createUserDTO;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

import me.lazy_assedninja.what_to_eat.common.TestUtil;
import me.lazy_assedninja.what_to_eat.util.LiveDataCallAdapterFactory;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Post;
import me.lazy_assedninja.what_to_eat.vo.Promotion;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Store;
import me.lazy_assedninja.what_to_eat.vo.User;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(JUnit4.class)
public class WhatToEatServiceTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private MockWebServer mockWebServer;
    private WhatToEatService service;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        service = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient()
                        .newBuilder()
                        .addInterceptor(new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .build()
                .create(WhatToEatService.class);
    }

    @After
    public void clear() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void register() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.register(createUser());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/User/Register"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void bindGoogleAccount() throws IOException, InterruptedException, TimeoutException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.bindGoogleAccount(createGoogleAccount());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/User/BindGoogleAccount"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void login() throws IOException, InterruptedException, TimeoutException {
        enqueueResponse("user.json");
        LiveData<ApiResponse<User>> apiResponse = service.login(createUserDTO());
        User user = ((ApiSuccessResponse<User>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/User/Login"));

        assertThat(user, notNullValue());
        assertThat(user.getName(), is("test"));
        assertThat(user.getGoogleAccount().getEmail(), is("test@gmail.com"));
    }

    @Test
    public void googleLogin() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("user.json");
        LiveData<ApiResponse<User>> apiResponse = service.googleLogin(createUserDTO());
        User user = ((ApiSuccessResponse<User>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/User/GoogleLogin"));

        assertThat(user, notNullValue());
        assertThat(user.getName(), is("test"));
        assertThat(user.getGoogleAccount().getEmail(), is("test@gmail.com"));
    }

    @Test
    public void resetPassword() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.resetPassword(createUserDTO());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/User/ResetPassword"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void sendVerificationCode() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.sendVerificationCode(createUserDTO());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/User/SendVerificationCode"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void forgetPassword() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.forgetPassword(createUserDTO());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/User/ForgetPassword"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void getStoreList() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("store.json");
        LiveData<ApiResponse<List<Store>>> apiResponse = service.getStoreList(createStoreDTO());
        List<Store> list = ((ApiSuccessResponse<List<Store>>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Store/GetStoreList"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getName(), is("火樹銀花韓式燒烤"));
        assertThat(list.get(1).getName(), is("巧之味手工水餃 濟南店"));
    }

    @Test
    public void getAllStores() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("store.json");
        LiveData<ApiResponse<List<Store>>> apiResponse = service.getAllStores(createStoreDTO());
        List<Store> list = ((ApiSuccessResponse<List<Store>>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Store/GetAllStores"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getName(), is("火樹銀花韓式燒烤"));
        assertThat(list.get(1).getName(), is("巧之味手工水餃 濟南店"));
    }

    @Test
    public void search() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("store.json");
        LiveData<ApiResponse<List<Store>>> apiResponse = service.search(createStoreDTO());
        List<Store> list = ((ApiSuccessResponse<List<Store>>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Store/Search"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getName(), is("火樹銀花韓式燒烤"));
        assertThat(list.get(1).getName(), is("巧之味手工水餃 濟南店"));
    }

    @Test
    public void createComment() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.createComment(TestUtil.createComment());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Store/CreateComment"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void getCommentList() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("comment.json");
        LiveData<ApiResponse<List<Comment>>> apiResponse = service.getCommentList(createCommentDTO(1));
        List<Comment> list = ((ApiSuccessResponse<List<Comment>>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Store/GetCommentList"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getContent(), is("Good."));
        assertThat(list.get(1).getContent(), is("Excellent!"));
    }

    @Test
    public void getPostList() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("post.json");
        LiveData<ApiResponse<List<Post>>> apiResponse = service.getPostList(createPostDTO());
        List<Post> list = ((ApiSuccessResponse<List<Post>>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Store/GetPostList"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getTitle(), is("活動"));
        assertThat(list.get(1).getTitle(), is("週年慶"));
    }

    @Test
    public void addToFavorite() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Favorite>> apiResponse =
                service.addToFavorite(createFavorite());
        Favorite result = ((ApiSuccessResponse<Favorite>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Favorite/AddToFavorite"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void getFavoriteList() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("store.json");
        LiveData<ApiResponse<List<Store>>> apiResponse = service.getFavoriteList(createFavoriteDTO());
        List<Store> list = ((ApiSuccessResponse<List<Store>>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Favorite/GetFavoriteList"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getName(), is("火樹銀花韓式燒烤"));
        assertThat(list.get(1).getName(), is("巧之味手工水餃 濟南店"));
    }

    @Test
    public void cancelFavorite() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Favorite>> apiResponse =
                service.cancelFavorite(createFavorite());
        Favorite result = ((ApiSuccessResponse<Favorite>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Favorite/CancelFavorite"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void getPromotionList() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("promotion.json");
        LiveData<ApiResponse<List<Promotion>>> apiResponse = service.getPromotionList();
        List<Promotion> list = ((ApiSuccessResponse<List<Promotion>>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Promotion/GetPromotionList"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getTitle(), is("打折"));
        assertThat(list.get(1).getTitle(), is("促銷活動"));
    }

    @Test
    public void createReservation() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.createReservation(
                TestUtil.createReservation());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse)).getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Reservation/CreateReservation"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void getReservationList() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("reservation.json");
        LiveData<ApiResponse<List<Reservation>>> apiResponse = service.getReservationList(
                createReservationDTO());
        List<Reservation> list = ((ApiSuccessResponse<List<Reservation>>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Reservation/GetReservationList"));

        assertThat(list, notNullValue());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getName(), is("test1"));
        assertThat(list.get(1).getName(), is("test2"));
    }

    @Test
    public void cancelReservation() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.cancelReservation(TestUtil.createReservation());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/Reservation/CancelReservation"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    @Test
    public void createReport() throws IOException, TimeoutException, InterruptedException {
        enqueueResponse("result.json");
        LiveData<ApiResponse<Result>> apiResponse = service.createReport(TestUtil.createReport());
        Result result = ((ApiSuccessResponse<Result>) getOrAwaitValue(apiResponse))
                .getBody();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/CustomService/CreateReport"));

        assertThat(result, notNullValue());
        assertThat(result.getResult(), is("Success."));
    }

    private void enqueueResponse(String fileName) throws IOException {
        if (getClass().getClassLoader() == null) return;
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("api_response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        mockWebServer.enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)));
    }
}