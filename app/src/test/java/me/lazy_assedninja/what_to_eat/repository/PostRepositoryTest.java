package me.lazy_assedninja.what_to_eat.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createPost;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createPostDTO;
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

import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.PostDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.PostDTO;
import me.lazy_assedninja.what_to_eat.util.InstantExecutorUtil;
import me.lazy_assedninja.what_to_eat.vo.Post;
import me.lazy_assedninja.what_to_eat.vo.Resource;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class PostRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PostRepository repository;
    private final PostDao postDao = mock(PostDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.postDao()).thenReturn(postDao);
        repository = new PostRepository(new InstantExecutorUtil(), postDao, service);
    }

    @Test
    public void loadPostsFromNetwork() {
        PostDTO postDTO = createPostDTO();
        postDTO.setStoreID(1);
        MutableLiveData<List<Post>> dbData = new MutableLiveData<>();
        when(postDao.getPosts(postDTO.getStoreID())).thenReturn(dbData);

        List<Post> list = new ArrayList<>();
        list.add(createPost(1, "post title", 1));
        LiveData<ApiResponse<List<Post>>> call = successCall(list);
        when(service.getPostList(postDTO)).thenReturn(call);

        LiveData<Resource<List<Post>>> data = repository.loadPosts(postDTO);
        verify(postDao).getPosts(postDTO.getStoreID());
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Post>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Post>> updateDbData = new MutableLiveData<>();
        when(postDao.getPosts(postDTO.getStoreID())).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).getPostList(postDTO);
        verify(postDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }
}