package me.lazy_assedninja.app.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import me.lazy_assedninja.app.vo.History;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.app.vo.Tag;
import me.lazy_assedninja.app.vo.User;

/**
 * Database descriptions.
 */
@Database(entities = {
        User.class, Tag.class, Store.class, Promotion.class, History.class, Reservation.class
}, version = 1, exportSchema = false)
public abstract class WhatToEatDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract TagDao tagDao();

    public abstract StoreDao storeDao();

    public abstract FavoriteDao favoriteDao();

    public abstract PromotionDao promotionDao();

    public abstract HistoryDao historyDao();

    public abstract ReservationDao reservationDao();
}