package me.lazy_assedninja.app.db;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.History;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.app.vo.User;

@Database(entities = {
        User.class, Store.class, Promotion.class, History.class, Reservation.class
}, version = 1, exportSchema = false)
public abstract class WhatToEatDatabase extends RoomDatabase {
    private static volatile WhatToEatDatabase INSTANCE = null;

    public static WhatToEatDatabase getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (WhatToEatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            application,
                            WhatToEatDatabase.class, "what_to_eat.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract StoreDao storeDao();

    public abstract FavoriteDao favoriteDao();

    public abstract PromotionDao promotionDao();

    public abstract HistoryDao historyDao();

    public abstract ReservationDao reservationDao();
}