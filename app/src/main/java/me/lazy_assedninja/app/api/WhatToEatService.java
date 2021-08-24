package me.lazy_assedninja.app.api;

import androidx.lifecycle.LiveData;

import java.util.List;

import me.lazy_assedninja.app.dto.CommentRequest;
import me.lazy_assedninja.app.dto.FavoriteRequest;
import me.lazy_assedninja.app.dto.GoogleAccountRequest;
import me.lazy_assedninja.app.dto.PostRequest;
import me.lazy_assedninja.app.dto.ReservationRequest;
import me.lazy_assedninja.app.dto.StoreRequest;
import me.lazy_assedninja.app.dto.UserRequest;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.GoogleAccount;
import me.lazy_assedninja.app.vo.Picture;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Report;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.app.vo.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WhatToEatService {

    /**
     * User
     */
    @POST("User/SignUp")
    LiveData<ApiResponse<Result>> signUp(@Body User user);

    @POST("User/BindGoogleAccount")
    LiveData<ApiResponse<Result>> bindGoogleAccount(@Body GoogleAccount googleAccount);

    @POST("User/Login")
    LiveData<ApiResponse<User>> login(@Body UserRequest userRequest);

    @POST("User/GoogleLogin")
    LiveData<ApiResponse<User>> googleLogin(@Body GoogleAccountRequest googleAccountRequest);

    @POST("User/UpdatePassword")
    LiveData<ApiResponse<Result>> updatePassword(@Body UserRequest userRequest);

//    @POST("User/SendVerificationEmail")
//    Call<ApiResponse<Result>> sendVerificationEmail(@Body UserRequest userRequest);
//
//    @POST("User/ResetPassword")
//    Call<ApiResponse<Result>> resetPassword(@Body UserRequest userRequest);
//
//    @POST("User/UpdatePermissionDeadline")
//    Call<ApiResponse<Result>> updatePermissionDeadline(@Body UserRequest userRequest);

    @POST("User/GetHeadPortrait")
    LiveData<ApiResponse<User>> getHeadPortrait(@Body UserRequest userRequest);

    /**
     * Store
     */
    @POST("Store/GetStoreList")
    LiveData<ApiResponse<List<Store>>> getStoreList(@Body StoreRequest storeRequest);

    @POST("Store/Search")
    LiveData<ApiResponse<List<Store>>> search(@Body StoreRequest storeRequest);

    @POST("Store/CreateComment")
    LiveData<ApiResponse<Result>> createComment(@Body Comment comment);

    @POST("Store/GetCommentList")
    LiveData<ApiResponse<List<Comment>>> getCommentList(@Body CommentRequest commentRequest);

    @POST("Store/CreatePost")
    LiveData<ApiResponse<Result>> createPost(@Body Post post);

    @POST("Store/GetPostList")
    LiveData<ApiResponse<List<Post>>> getPostList(@Body PostRequest postRequest);

    /**
     * Favorite
     */
    @POST("Favorite/AddToFavorite")
    Call<Result> addToFavorite(@Body Favorite favorite);

    @POST("Favorite/GetFavoriteList")
    LiveData<ApiResponse<List<Favorite>>> getFavoriteList(@Body FavoriteRequest favoriteRequest);

    @POST("Favorite/CancelFavorite")
    Call<Result> cancelFavorite(@Body Favorite favorite);

    /**
     * Promotion
     */
    @GET("Promotion/GetPromotionList")
    LiveData<ApiResponse<List<Promotion>>> getPromotionList();

    /**
     * ReservationDao
     */
    @POST("ReservationDao/CreateReservation")
    LiveData<ApiResponse<Result>> createReservation(@Body Reservation reservation);

    @POST("ReservationDao/GetReservationList")
    LiveData<ApiResponse<List<Reservation>>> getReservationList(@Body ReservationRequest reservationRequest);

    @POST("ReservationDao/CancelReservation")
    LiveData<ApiResponse<Result>> cancelReservation(@Body ReservationRequest reservationRequest);

    /**
     * CustomService
     */
    @POST("CustomService/CreateReport")
    LiveData<ApiResponse<Result>> createReport(@Body Report report);

    @GET("CustomService/GetReportList")
    LiveData<ApiResponse<List<Report>>> getReportList();

    /**
     * Picture
     **/
    @Multipart
    @POST("File/Upload")
    LiveData<Picture> upload(@Part MultipartBody.Part file);
}