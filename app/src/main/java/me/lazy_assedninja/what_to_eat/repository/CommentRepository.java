package me.lazy_assedninja.what_to_eat.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.CommentDao;
import me.lazy_assedninja.what_to_eat.db.UserDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.CommentDTO;
import me.lazy_assedninja.what_to_eat.util.RateLimiter;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.User;

/**
 * Repository that handles Comment objects.
 */
public class CommentRepository {

    private final ExecutorUtil executorUtil;
    private final WhatToEatDatabase db;
    private final UserDao userDao;
    private final CommentDao commentDao;
    private final WhatToEatService whatToEatService;

    private final RateLimiter<CommentDTO> rateLimiter = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public CommentRepository(ExecutorUtil executorUtil, WhatToEatDatabase db, UserDao userDao,
                             CommentDao commentDao, WhatToEatService whatToEatService) {
        this.executorUtil = executorUtil;
        this.db = db;
        this.userDao = userDao;
        this.commentDao = commentDao;
        this.whatToEatService = whatToEatService;
    }

    public LiveData<Resource<List<Comment>>> loadComments(CommentDTO commentDTO) {
        return new NetworkBoundResource<List<Comment>, List<Comment>>(executorUtil) {

            @Override
            protected LiveData<List<Comment>> loadFromDb() {
                return commentDao.getComments(commentDTO.getStoreID());
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Comment> data) {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(commentDTO);
            }

            @Override
            protected LiveData<ApiResponse<List<Comment>>> createCall() {
                return whatToEatService.getCommentList(commentDTO);
            }

            @Override
            protected void saveCallResult(List<Comment> item) {
                commentDao.insertAll(item);
            }

            @Override
            protected void onFetchFailed() {
                rateLimiter.reset(commentDTO);
            }
        }.asLiveData();
    }

    public LiveData<Event<Resource<Result>>> createComment(Comment comment) {
        return new NetworkResource<Result, Long>(executorUtil) {

            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return whatToEatService.createComment(comment);
            }

            @Override
            protected Long saveCallResult(Result item) {
                return db.runInTransaction(() -> {
                    User user = userDao.getUser();
                    comment.setId(commentDao.getLastCommentID() + 1);
                    comment.setUserName(user.getName());
                    comment.setUserPicture(user.getHeadPortrait());
                    return commentDao.insert(comment);
                });
            }
        }.asLiveData();
    }
}