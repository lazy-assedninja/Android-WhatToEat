package me.lazy_assedninja.app.api;

import java.util.concurrent.TimeUnit;

import me.lazy_assedninja.app.repository.SecretRepository;
import me.lazy_assedninja.app.utils.LiveDataCallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static RetrofitManager INSTANCE = null;

    private final WhatToEatService whatToEatService;

    public static RetrofitManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RetrofitManager();
        }
        return INSTANCE;
    }

    private RetrofitManager() {
        whatToEatService = new Retrofit.Builder()
                .baseUrl(SecretRepository.URL)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient().newBuilder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .build()
                .create(WhatToEatService.class);
    }

    public WhatToEatService getWhatToEatService() {
        return whatToEatService;
    }
}