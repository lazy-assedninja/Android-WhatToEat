package me.lazy_assedninja.what_to_eat.common;

import me.lazy_assedninja.what_to_eat.dto.CommentDTO;
import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.dto.PostDTO;
import me.lazy_assedninja.what_to_eat.dto.ReservationDTO;
import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.dto.UserDTO;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.GoogleAccount;
import me.lazy_assedninja.what_to_eat.vo.History;
import me.lazy_assedninja.what_to_eat.vo.Post;
import me.lazy_assedninja.what_to_eat.vo.Promotion;
import me.lazy_assedninja.what_to_eat.vo.Report;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Store;
import me.lazy_assedninja.what_to_eat.vo.Tag;
import me.lazy_assedninja.what_to_eat.vo.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TestUtil {

    public static User createUser() {
        return new User();
    }

    public static User createUser(String name) {
        return new User("", "", name, "", "");
    }

    public static GoogleAccount createGoogleAccount() {
        return new GoogleAccount();
    }

    public static Store createStore(int id, String name) {
        Store store = new Store();
        store.setId(id);
        store.setName(name);
        store.setTagID(Tag.HOME.getValue());
        return store;
    }

    public static Store createStore(int id, String name, boolean isFavorite) {
        Store store = new Store();
        store.setId(id);
        store.setName(name);
        store.setTagID(Tag.HOME.getValue());
        store.setFavorite(isFavorite);
        return store;
    }

    public static Favorite createFavorite() {
        return new Favorite(0, false);
    }

    public static Promotion createPromotion(int id, String title) {
        Promotion promotion = new Promotion();
        promotion.setId(id);
        promotion.setTitle(title);
        return promotion;
    }

    public static History createHistory(int id) {
        return new History(id);
    }

    public static Reservation createReservation() {
        return new Reservation();
    }

    public static Reservation createReservation(int id, String name) {
        Reservation reservation = new Reservation(name, "", "", "");
        reservation.setId(id);
        return reservation;
    }

    public static Post createPost(int id, String title, int storeID) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setStoreID(storeID);
        return post;
    }

    public static Comment createComment() {
        return new Comment();
    }

    public static Comment createComment(int id, String star, int storeID) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStar(star);
        comment.setStoreID(storeID);
        return comment;
    }

    public static Report createReport() {
        return new Report("");
    }

    public static Result createResult() {
        return new Result();
    }

    public static Ninja createNinja() {
        return new Ninja();
    }

    public static MultipartBody.Part createFile(){
        return MultipartBody.Part.createFormData("file", "file name", RequestBody
                .create("file path", MediaType.parse("multipart/form-data")));
    }

    public static UserDTO createUserDTO() {
        return new UserDTO("");
    }

    public static UserDTO createUserDTO(String googleID) {
        return new UserDTO(googleID, "", true);
    }

    public static StoreDTO createStoreDTO() {
        return new StoreDTO();
    }

    public static CommentDTO createCommentDTO(int storeID) {
        return new CommentDTO(storeID);
    }

    public static PostDTO createPostDTO() {
        return new PostDTO(0);
    }

    public static FavoriteDTO createFavoriteDTO() {
        return new FavoriteDTO(0);
    }

    public static ReservationDTO createReservationDTO() {
        return new ReservationDTO(0);
    }
}