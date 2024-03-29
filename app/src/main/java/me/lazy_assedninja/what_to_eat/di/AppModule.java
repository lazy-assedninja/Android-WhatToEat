package me.lazy_assedninja.what_to_eat.di;

import android.content.Context;

import androidx.room.Room;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import me.lazy_assedninja.what_to_eat.BuildConfig;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.CommentDao;
import me.lazy_assedninja.what_to_eat.db.FavoriteDao;
import me.lazy_assedninja.what_to_eat.db.HistoryDao;
import me.lazy_assedninja.what_to_eat.db.PostDao;
import me.lazy_assedninja.what_to_eat.db.PromotionDao;
import me.lazy_assedninja.what_to_eat.db.ReservationDao;
import me.lazy_assedninja.what_to_eat.db.StoreDao;
import me.lazy_assedninja.what_to_eat.db.UserDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.util.LiveDataCallAdapterFactory;
import me.lazy_assedninja.library.util.DisplayUtil;
import me.lazy_assedninja.library.util.NetworkUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Hilt module that installs into SingletonComponent.
 */
@SuppressWarnings("unused")
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public WhatToEatService provideService() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.URL)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(
                                new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                        )
                        .build())
                .build()
                .create(WhatToEatService.class);
    }

    @Provides
    @Singleton
    public WhatToEatDatabase provideDb(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context,
                WhatToEatDatabase.class, "what_to_eat.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    public UserDao provideUserDao(WhatToEatDatabase database) {
        return database.userDao();
    }

    @Provides
    @Singleton
    public StoreDao provideStoreDao(WhatToEatDatabase database) {
        return database.storeDao();
    }

    @Provides
    @Singleton
    public FavoriteDao provideFavoriteDao(WhatToEatDatabase database) {
        return database.favoriteDao();
    }

    @Provides
    @Singleton
    public CommentDao provideCommentDao(WhatToEatDatabase database) {
        return database.commentDao();
    }

    @Provides
    @Singleton
    public PostDao providePostDao(WhatToEatDatabase database) {
        return database.postDao();
    }

    @Provides
    @Singleton
    public PromotionDao providePromotionDao(WhatToEatDatabase database) {
        return database.promotionDao();
    }

    @Provides
    @Singleton
    public HistoryDao provideHistoryDao(WhatToEatDatabase database) {
        return database.historyDao();
    }

    @Provides
    @Singleton
    public ReservationDao provideReservationDao(WhatToEatDatabase database) {
        return database.reservationDao();
    }

    @Provides
    @Singleton
    public DisplayUtil provideDisplayUtil(@ApplicationContext Context context) {
        return new DisplayUtil(context);
    }

    @Provides
    @Singleton
    public NetworkUtil provideNetworkUtil(@ApplicationContext Context context) {
        return new NetworkUtil(context);
    }
}