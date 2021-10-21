package me.lazy_assedninja.app.di;

import android.content.Context;

import androidx.room.Room;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import me.lazy_assedninja.app.BuildConfig;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.CommentDao;
import me.lazy_assedninja.app.db.FavoriteDao;
import me.lazy_assedninja.app.db.HistoryDao;
import me.lazy_assedninja.app.db.PostDao;
import me.lazy_assedninja.app.db.PromotionDao;
import me.lazy_assedninja.app.db.ReservationDao;
import me.lazy_assedninja.app.db.StoreDao;
import me.lazy_assedninja.app.db.TagDao;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.utils.LiveDataCallAdapterFactory;
import me.lazy_assedninja.library.utils.DisplayUtils;
import me.lazy_assedninja.library.utils.NetworkUtils;
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
                        .addInterceptor(new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
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
    public TagDao provideTagDao(WhatToEatDatabase database) {
        return database.tagDao();
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
    public DisplayUtils provideDisplayUtils(@ApplicationContext Context context) {
        return new DisplayUtils(context);
    }

    @Provides
    @Singleton
    public NetworkUtils provideNetworkUtils(@ApplicationContext Context context) {
        return new NetworkUtils(context);
    }
}