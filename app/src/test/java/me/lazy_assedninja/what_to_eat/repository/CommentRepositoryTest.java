package me.lazy_assedninja.what_to_eat.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createCommentDTO;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createResult;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createUser;
import static me.lazy_assedninja.what_to_eat.util.ApiUtil.successCall;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.common.TestUtil;
import me.lazy_assedninja.what_to_eat.db.CommentDao;
import me.lazy_assedninja.what_to_eat.db.UserDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.CommentDTO;
import me.lazy_assedninja.what_to_eat.util.InstantExecutorUtil;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.User;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class CommentRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private CommentRepository repository;
    private final UserDao userDao = mock(UserDao.class);
    private final CommentDao commentDao = mock(CommentDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.userDao()).thenReturn(userDao);
        when(db.commentDao()).thenReturn(commentDao);
        doCallRealMethod().when(db).runInTransaction((Callable<Long>) any());
        repository = new CommentRepository(new InstantExecutorUtil(), db, userDao, commentDao, service);
    }

    @Test
    public void loadCommentsFromNetwork() {
        CommentDTO commentDTO = createCommentDTO(1);
        MutableLiveData<List<Comment>> dbData = new MutableLiveData<>();
        when(commentDao.getComments(commentDTO.getStoreID())).thenReturn(dbData);

        List<Comment> list = new ArrayList<>();
        list.add(TestUtil.createComment());
        LiveData<ApiResponse<List<Comment>>> call = successCall(list);
        when(service.getCommentList(commentDTO)).thenReturn(call);

        LiveData<Resource<List<Comment>>> data = repository.loadComments(commentDTO);
        verify(commentDao).getComments(commentDTO.getStoreID());
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Comment>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Comment>> updateDbData = new MutableLiveData<>();
        when(commentDao.getComments(commentDTO.getStoreID())).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).getCommentList(commentDTO);
        verify(commentDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void createComment() {
        String userName = "Lazy-assed Ninja";
        String headPortrait = "picture URL";
        int commentID = 1;
        User userData = createUser();
        userData.setName(userName);
        userData.setHeadPortrait(headPortrait);
        when(userDao.getUser()).thenReturn(userData);
        when(commentDao.getLastCommentID()).thenReturn(commentID);

        Comment comment = TestUtil.createComment();
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.createComment(comment)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.createComment(comment).observeForever(observer);
        verify(service).createComment(comment);

        comment.setId(commentID  + 1);
        comment.setUserName(userName);
        comment.setUserPicture(headPortrait);
        verify(userDao).getUser();
        verify(commentDao).getLastCommentID();
        verify(commentDao).insert(comment);

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }
}