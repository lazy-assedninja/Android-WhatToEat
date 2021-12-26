package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.PostDao;
import me.lazy_assedninja.what_to_eat.dto.PostDTO;
import me.lazy_assedninja.what_to_eat.util.RateLimiter;
import me.lazy_assedninja.what_to_eat.vo.Post;
import me.lazy_assedninja.what_to_eat.vo.Resource;

/**
 * Repository that handles Post objects.
 */
public class PostRepository {

    private final ExecutorUtil executorUtil;
    private final PostDao postDao;
    private final WhatToEatService whatToEatService;

    private final RateLimiter<PostDTO> rateLimiter = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public PostRepository(ExecutorUtil executorUtil, PostDao postDao,
                          WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.postDao = postDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Post>>> loadPosts(PostDTO postDTO) {
        return new NetworkBoundResource<List<Post>, List<Post>>(executorUtil) {

            @Override
            protected LiveData<List<Post>> loadFromDb() {
                return postDao.getPosts(postDTO.getStoreID());
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Post> data) {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(postDTO);
            }

            @Override
            protected LiveData<ApiResponse<List<Post>>> createCall() {
                return whatToEatService.getPostList(postDTO);
            }

            @Override
            protected void saveCallResult(List<Post> item) {
                postDao.insertAll(item);
            }

            @Override
            protected void onFetchFailed() {
                rateLimiter.reset(postDTO);
            }
        }.asLiveData();
    }
}