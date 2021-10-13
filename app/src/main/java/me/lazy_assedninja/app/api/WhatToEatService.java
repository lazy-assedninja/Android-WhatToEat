package me.lazy_assedninja.app.api;

import androidx.lifecycle.LiveData;

import java.util.List;

import me.lazy_assedninja.app.dto.CommentDTO;
import me.lazy_assedninja.app.dto.FavoriteDTO;
import me.lazy_assedninja.app.dto.PostDTO;
import me.lazy_assedninja.app.dto.ReservationDTO;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.GoogleAccount;
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

/**
 * REST API access points.
 */
@SuppressWarnings("unused")
public interface WhatToEatService {

    /**
     * User
     */
    @POST("User/Register")
    Call<Result> register(@Body User user);

    @POST("User/BindGoogleAccount")
    Call<Result> bindGoogleAccount(@Body GoogleAccount googleAccount);

    @POST("User/Login")
    LiveData<ApiResponse<User>> login(@Body UserDTO userDTO);

    @POST("User/GoogleLogin")
    LiveData<ApiResponse<User>> googleLogin(@Body UserDTO userDTO);

    @POST("User/ResetPassword")
    Call<Result> resetPassword(@Body UserDTO userDTO);

    @POST("User/SendVerificationCode")
    Call<Result> sendVerificationCode(@Body UserDTO userDTO);

    @POST("User/ForgetPassword")
    Call<Result> forgetPassword(@Body UserDTO userDTO);

    @POST("User/UpdatePermissionDeadline")
    Call<ApiResponse<Result>> updatePermissionDeadline(@Body UserDTO userDTO);

    @POST("User/UpdateHeadPortrait")
    Call<Result> updateHeadPortrait(@Body UserDTO userDTO);

    @POST("User/GetHeadPortrait")
    LiveData<ApiResponse<User>> getHeadPortrait(@Body UserDTO userDTO);

    /**
     * Store
     */
    @POST("Store/GetStoreList")
    LiveData<ApiResponse<List<Store>>> getStoreList(@Body StoreDTO storeDTO);

    @POST("Store/GetAllStores")
    LiveData<ApiResponse<List<Store>>> getAllStores(@Body StoreDTO storeDTO);

    @POST("Store/Search")
    LiveData<ApiResponse<List<Store>>> search(@Body StoreDTO storeDTO);

    @POST("Store/CreateComment")
    LiveData<ApiResponse<Result>> createComment(@Body Comment comment);

    @POST("Store/GetCommentList")
    LiveData<ApiResponse<List<Comment>>> getCommentList(@Body CommentDTO commentDTO);

    @POST("Store/CreatePost")
    LiveData<ApiResponse<Result>> createPost(@Body Post post);

    @POST("Store/GetPostList")
    LiveData<ApiResponse<List<Post>>> getPostList(@Body PostDTO postDTO);

    /**
     * Favorite
     */
    @POST("Favorite/AddToFavorite")
    Call<Result> addToFavorite(@Body Favorite favorite);

    @POST("Favorite/GetFavoriteList")
    LiveData<ApiResponse<List<Store>>> getFavoriteList(@Body FavoriteDTO favoriteDTO);

    @POST("Favorite/CancelFavorite")
    Call<Result> cancelFavorite(@Body Favorite favorite);

    /**
     * Promotion
     */
    @GET("Promotion/GetPromotionList")
    LiveData<ApiResponse<List<Promotion>>> getPromotionList();

    /**
     * Reservation
     */
    @POST("Reservation/CreateReservation")
    Call<Result> createReservation(@Body Reservation reservation);

    @POST("Reservation/GetReservationList")
    LiveData<ApiResponse<List<Reservation>>> getReservationList(@Body ReservationDTO reservationDTO);

    @POST("Reservation/CancelReservation")
    Call<Result> cancelReservation(@Body ReservationDTO reservationDTO);

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
    Call<Result> upload(@Part MultipartBody.Part file);
}