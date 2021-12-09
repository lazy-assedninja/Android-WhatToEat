package me.lazy_assedninja.what_to_eat.ui.store;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static me.lazy_assedninja.what_to_eat.common.TestUtil.createCommentDTO;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.what_to_eat.dto.CommentDTO;
import me.lazy_assedninja.what_to_eat.repository.CommentRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.store.comment.CommentViewModel;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Resource;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class CommentViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final CommentViewModel viewModel = new CommentViewModel(userRepository,
            commentRepository);

    @Test
    public void testNull() {
        assertThat(viewModel.comments, notNullValue());

        verify(commentRepository, never()).loadComments(any());
        viewModel.requestComment(createCommentDTO());
        verify(commentRepository, never()).loadComments(any());
    }

    @Test
    public void sendResultToUI() {
        CommentDTO commentDTO = createCommentDTO();
        MutableLiveData<Resource<List<Comment>>> list = new MutableLiveData<>();
        when(commentRepository.loadComments(commentDTO)).thenReturn(list);
        Observer<Resource<List<Comment>>> observer = mock(Observer.class);
        viewModel.comments.observeForever(observer);
        viewModel.requestComment(commentDTO);
        verify(observer, never()).onChanged(any());

        List<Comment> data = new ArrayList<>();
        Resource<List<Comment>> resource = Resource.success(data);
        list.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void loadComments() {
        viewModel.comments.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(commentRepository);

        CommentDTO commentDTO = createCommentDTO();
        viewModel.requestComment(commentDTO);
        verify(commentRepository).loadComments(commentDTO);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }
}