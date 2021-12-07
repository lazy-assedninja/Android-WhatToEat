package me.lazy_assedninja.app.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.app.vo.History;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.app.vo.User;

/**
 * Database descriptions.
 */
@Database(entities = {
        User.class, Store.class, Comment.class, Post.class, Promotion.class, History.class,
        Reservation.class
}, version = 1, exportSchema = false)
public abstract class WhatToEatDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract StoreDao storeDao();

    public abstract FavoriteDao favoriteDao();

    public abstract CommentDao commentDao();

    public abstract PostDao postDao();

    public abstract PromotionDao promotionDao();

    public abstract HistoryDao historyDao();

    public abstract ReservationDao reservationDao();
}