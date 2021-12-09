package me.lazy_assedninja.what_to_eat.ui.index;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.what_to_eat.repository.HistoryRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;

@RunWith(JUnit4.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final HistoryRepository historyRepository = mock(HistoryRepository.class);
    private final MainViewModel viewModel = new MainViewModel(userRepository, historyRepository);

    @Test
    public void isLoggedIn() {
        when(userRepository.getUserID()).thenReturn(0);

        boolean isLoggedIn = viewModel.isLoggedIn();
        verify(userRepository).getUserID();
        assertThat(isLoggedIn, is(true));
    }

    @Test
    public void getUser(){
        viewModel.getUser();

        verify(userRepository).getUserFromDb();
    }

    @Test
    public void clearHistory(){
        viewModel.clearHistory();

        verify(historyRepository).deleteAll();
    }
}