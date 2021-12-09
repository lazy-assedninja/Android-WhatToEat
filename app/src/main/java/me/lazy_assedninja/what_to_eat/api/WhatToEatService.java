package me.lazy_assedninja.what_to_eat.api;

import androidx.lifecycle.LiveData;

import java.util.List;

import me.lazy_assedninja.what_to_eat.dto.CommentDTO;
import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.dto.PostDTO;
import me.lazy_assedninja.what_to_eat.dto.ReservationDTO;
import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.dto.UserDTO;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.GoogleAccount;
import me.lazy_assedninja.what_to_eat.vo.Post;
import me.lazy_assedninja.what_to_eat.vo.Promotion;
import me.lazy_assedninja.what_to_eat.vo.Report;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Store;
import me.lazy_assedninja.what_to_eat.vo.User;
import okhttp3.MultipartBody;
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
    LiveData<ApiResponse<Result>> register(@Body User user);

    @POST("User/BindGoogleAccount")
    LiveData<ApiResponse<Result>> bindGoogleAccount(@Body GoogleAccount googleAccount);

    @POST("User/Login")
    LiveData<ApiResponse<User>> login(@Body UserDTO userDTO);

    @POST("User/GoogleLogin")
    LiveData<ApiResponse<User>> googleLogin(@Body UserDTO userDTO);

    @POST("User/ResetPassword")
    LiveData<ApiResponse<Result>> resetPassword(@Body UserDTO userDTO);

    @POST("User/SendVerificationCode")
    LiveData<ApiResponse<Result>> sendVerificationCode(@Body UserDTO userDTO);

    @POST("User/ForgetPassword")
    LiveData<ApiResponse<Result>> forgetPassword(@Body UserDTO userDTO);

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

    @POST("Store/GetPostList")
    LiveData<ApiResponse<List<Post>>> getPostList(@Body PostDTO postDTO);

    /**
     * Favorite
     */
    @POST("Favorite/AddToFavorite")
    LiveData<ApiResponse<Result>> addToFavorite(@Body Favorite favorite);

    @POST("Favorite/GetFavoriteList")
    LiveData<ApiResponse<List<Store>>> getFavoriteList(@Body FavoriteDTO favoriteDTO);

    @POST("Favorite/CancelFavorite")
    LiveData<ApiResponse<Result>> cancelFavorite(@Body Favorite favorite);

    /**
     * Promotion
     */
    @GET("Promotion/GetPromotionList")
    LiveData<ApiResponse<List<Promotion>>> getPromotionList();

    /**
     * Reservation
     */
    @POST("Reservation/CreateReservation")
    LiveData<ApiResponse<Result>> createReservation(@Body Reservation reservation);

    @POST("Reservation/GetReservationList")
    LiveData<ApiResponse<List<Reservation>>> getReservationList(@Body ReservationDTO reservationDTO);

    @POST("Reservation/CancelReservation")
    LiveData<ApiResponse<Result>> cancelReservation(@Body Reservation reservation);

    /**
     * CustomService
     */
    @POST("CustomService/CreateReport")
    LiveData<ApiResponse<Result>> createReport(@Body Report report);

    /**
     * Picture
     **/
    @Multipart
    @POST("File/Upload")
    LiveData<ApiResponse<Result>> upload(@Part MultipartBody.Part file);
}