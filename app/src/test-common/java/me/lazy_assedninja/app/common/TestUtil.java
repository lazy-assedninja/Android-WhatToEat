package me.lazy_assedninja.app.common;

import me.lazy_assedninja.app.dto.CommentDTO;
import me.lazy_assedninja.app.dto.FavoriteDTO;
import me.lazy_assedninja.app.dto.PostDTO;
import me.lazy_assedninja.app.dto.ReservationDTO;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.GoogleAccount;
import me.lazy_assedninja.app.vo.History;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Report;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.app.vo.Tag;
import me.lazy_assedninja.app.vo.User;
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

    public static Tag createTag(int id) {
        return new Tag(id);
    }

    public static Store createStore(int id, String name) {
        Store store = new Store();
        store.setId(id);
        store.setName(name);
        store.setTagID(1);
        return store;
    }

    public static Store createStore(int id, String name, boolean isFavorite) {
        Store store = new Store();
        store.setId(id);
        store.setName(name);
        store.setTagID(1);
        store.setFavorite(isFavorite);
        return store;
    }

    public static Favorite createFavorite() {
        return new Favorite(0, 0, false);
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
        Reservation reservation = new Reservation(name, "", "", "", 0,
                1);
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
        return new Report("", "", 0, 0);
    }

    public static Result createResult() {
        return new Result();
    }

    public static Ninja createNinja() {
        return new Ninja();
    }

    public static MultipartBody.Part createFile(){
        return MultipartBody.Part.createFormData("file", "file_name",
                RequestBody.create("file_path", MediaType.parse("multipart/form-data")));
    }

    public static UserDTO createUserDTO() {
        return new UserDTO("");
    }

    public static UserDTO createUserDTO(String googleID) {
        return new UserDTO(googleID, "", true);
    }

    public static StoreDTO createStoreDTO() {
        return new StoreDTO(0);
    }

    public static CommentDTO createCommentDTO() {
        return new CommentDTO(0);
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