package me.lazy_assedninja.app.ui.store.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.lazy_assedninja.app.api.RetrofitManager;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.StoreRequest;
import me.lazy_assedninja.app.repository.FavoriteRepository;
import me.lazy_assedninja.app.repository.StoreRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.LogUtils;

public class HomeViewModel extends AndroidViewModel {

    private final ExecutorUtils executorUtils = new ExecutorUtils();
    private final UserRepository userRepository = new UserRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).userDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );
    private final StoreRepository storeRepository = new StoreRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).storeDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );
    private final FavoriteRepository favoriteRepository = new FavoriteRepository(
            executorUtils,
            WhatToEatDatabase.getInstance(getApplication()).favoriteDao(),
            RetrofitManager.getInstance().getWhatToEatService()
    );

    private final MutableLiveData<StoreRequest> storeRequest = new MutableLiveData<>();
    private final MutableLiveData<Favorite> favoriteRequest = new MutableLiveData<>();

    public HomeViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public int getLoggedInUserID() {
        return userRepository.isLoggedIn(getApplication());
    }

    public LiveData<Resource<List<Store>>> store = Transformations.switchMap(storeRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return storeRepository.loadStores(request);
        }
    });

    public void setStoreRequest(StoreRequest request) {
        if (storeRequest.getValue() != request) {
            storeRequest.setValue(request);
        }
    }

    public LiveData<Resource<Result>> favorite = Transformations.switchMap(favoriteRequest, favorite -> {
        if (favorite == null) {
            return AbsentLiveData.create();
        } else {
            favoriteRepository.updateFavoriteStatus(favorite.getStoreID(), favorite.getStatus());
            return favoriteRepository.changeFavoriteStatus(favorite);
        }
    });

    public void setFavoriteRequest(Favorite request) {
        if (favoriteRequest.getValue() != request) {
            favoriteRequest.setValue(request);
        }
    }
}