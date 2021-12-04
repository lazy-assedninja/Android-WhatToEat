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
import me.lazy_assedninja.app.ui.user.forget_password.ForgetPasswordViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ForgetPasswordViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ForgetPasswordViewModel viewModel = new ForgetPasswordViewModel(userRepository);

    @Test
    public void testNull() {
        // Send verification code
        assertThat(viewModel.sendVerificationResult, notNullValue());

        verify(userRepository, never()).sendVerificationCode(any());
        viewModel.sendVerificationCode("email");
        verify(userRepository, never()).sendVerificationCode(any());

        // Forget password
        assertThat(viewModel.forgetPasswordResult, notNullValue());

        verify(userRepository, never()).forgetPassword(any());
        viewModel.forgetPassword("email", "verification code",
                "new password");
        verify(userRepository, never()).forgetPassword(any());
    }

    @Test
    public void sendResultToUI() {
        // Send verification code
        MutableLiveData<Event<Resource<Result>>> sendVerificationResult = new MutableLiveData<>();
        when(userRepository.sendVerificationCode(any())).thenReturn(sendVerificationResult);
        Observer<Event<Resource<Result>>> sendVerificationObserver = mock(Observer.class);
        viewModel.sendVerificationResult.observeForever(sendVerificationObserver);
        viewModel.sendVerificationCode("email");
        verify(sendVerificationObserver, never()).onChanged(any());
        Event<Resource<Result>> sendVerificationResource = new Event<>(Resource.success(createResult()));

        sendVerificationResult.setValue(sendVerificationResource);
        verify(sendVerificationObserver).onChanged(sendVerificationResource);

        // Forget password
        MutableLiveData<Event<Resource<Result>>> forgetPasswordResult = new MutableLiveData<>();
        when(userRepository.forgetPassword(any())).thenReturn(forgetPasswordResult);
        Observer<Event<Resource<Result>>> forgetPasswordObserver = mock(Observer.class);
        viewModel.forgetPasswordResult.observeForever(forgetPasswordObserver);
        viewModel.forgetPassword("email", "verification code",
                "new password");
        verify(forgetPasswordObserver, never()).onChanged(any());
        Event<Resource<Result>> forgetPasswordResource = new Event<>(Resource.success(createResult()));

        forgetPasswordResult.setValue(forgetPasswordResource);
        verify(forgetPasswordObserver).onChanged(forgetPasswordResource);
    }

    @Test
    public void sendVerificationCode() {
        viewModel.sendVerificationResult.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository);
        viewModel.sendVerificationCode("email");
        verify(userRepository).sendVerificationCode(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void forgetPassword() {
        viewModel.forgetPasswordResult.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository);
        viewModel.forgetPassword("email", "verification code",
                "new password");
        verify(userRepository).forgetPassword(any());
        verifyNoMoreInteractions(userRepository);
    }
}