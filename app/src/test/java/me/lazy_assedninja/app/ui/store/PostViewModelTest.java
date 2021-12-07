package me.lazy_assedninja.app.ui.store;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static me.lazy_assedninja.app.common.TestUtil.createPostDTO;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.app.dto.PostDTO;
import me.lazy_assedninja.app.repository.PostRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.store.post.PostViewModel;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.app.vo.Resource;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class PostViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final PostViewModel viewModel = new PostViewModel(userRepository, postRepository);

    @Test
    public void testNUll() {
        assertThat(viewModel.posts, notNullValue());

        verify(postRepository, never()).loadPosts(any());
        viewModel.requestPost(createPostDTO());
        verify(postRepository, never()).loadPosts(any());
    }

    @Test
    public void sendResultToUI() {
        PostDTO postDTO = createPostDTO();
        MutableLiveData<Resource<List<Post>>> list = new MutableLiveData<>();
        when(postRepository.loadPosts(postDTO)).thenReturn(list);
        Observer<Resource<List<Post>>> observer = mock(Observer.class);
        viewModel.posts.observeForever(observer);
        viewModel.requestPost(postDTO);
        verify(observer, never()).onChanged(any());

        List<Post> data = new ArrayList<>();
        Resource<List<Post>> resource = Resource.success(data);
        list.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void loadPosts() {
        viewModel.posts.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(postRepository);

        PostDTO postDTO = createPostDTO();
        viewModel.requestPost(postDTO);
        verify(postRepository).loadPosts(postDTO);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }
}