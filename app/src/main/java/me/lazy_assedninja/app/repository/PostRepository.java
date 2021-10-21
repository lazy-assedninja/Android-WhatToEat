package me.lazy_assedninja.app.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.PostDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.PostDTO;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.NetworkUtils;

public class PostRepository {

    private final ExecutorUtils executorUtils;
    private final NetworkUtils networkUtils;
    private final WhatToEatDatabase db;
    private final PostDao postDao;
    private final WhatToEatService whatToEatService;

    @Inject
    public PostRepository(ExecutorUtils executorUtils, NetworkUtils networkUtils,
                          WhatToEatDatabase db, PostDao postDao, WhatToEatService whatToEatService) {
        this.executorUtils = executorUtils;
        this.networkUtils = networkUtils;
        this.db = db;
        this.postDao = postDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Post>>> loadPosts(PostDTO postDTO) {
        return new NetworkBoundResource<List<Post>, List<Post>>(executorUtils) {

            @Override
            protected LiveData<List<Post>> loadFromDb() {
                return postDao.getPosts(postDTO.getStoreID());
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Post> data) {
                return data == null || data.isEmpty() || networkUtils.isConnected();
            }

            @Override
            protected LiveData<ApiResponse<List<Post>>> createCall() {
                return whatToEatService.getPostList(postDTO);
            }

            @Override
            protected void saveCallResult(List<Post> item) {
                db.runInTransaction(() -> {
                    postDao.deleteByStoreID(postDTO.getStoreID());
                    postDao.insertAll(item);
                });
            }
        }.asLiveData();
    }
}
