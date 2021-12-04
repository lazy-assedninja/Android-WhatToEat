package me.lazy_assedninja.app.ui.user;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static me.lazy_assedninja.app.common.TestUtil.createResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.user.reset_password.ResetPasswordViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ResetPasswordViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ResetPasswordViewModel viewModel = new ResetPasswordViewModel(userRepository);

    @Test
    public void testNull(){
        assertThat(viewModel.result, notNullValue());

        verify(userRepository, never()).resetPassword(any());
        viewModel.resetPassword("old password", "new password");
        verify(userRepository, never()).resetPassword(any());
    }

    @Test
    public void sendResultToUI(){
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(userRepository.resetPassword(any())).thenReturn(result);
        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        viewModel.result.observeForever(observer);
        viewModel.resetPassword("old password", "new password");
        verify(observer, never()).onChanged(any());
        Event<Resource<Result>> resource = new Event<>(Resource.success(createResult()));

        result.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void resetPassword(){
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository);
        viewModel.resetPassword("old password", "new password");
        verify(userRepository).getUserEmail();
        verify(userRepository).resetPassword(any());
        verifyNoMoreInteractions(userRepository);
    }
}