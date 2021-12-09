package me.lazy_assedninja.what_to_eat.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.UserDao;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.library.util.EncryptUtil;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.library.util.TimeUtil;

/**
 * Hilt module that installs into SingletonComponent.
 */
@SuppressWarnings("unused")
@Module
@InstallIn(ViewModelComponent.class)
public class RepositoryModule {

    @Provides
    @ViewModelScoped
    public UserRepository provideUserRepository(@ApplicationContext Context context,
                                                ExecutorUtil executorUtil, EncryptUtil encryptUtil,
                                                TimeUtil timeUtil, UserDao userDao,
                                                WhatToEatService whatToEatService) {
        return new UserRepository(context, executorUtil, encryptUtil, timeUtil, userDao,
                whatToEatService);
    }
}