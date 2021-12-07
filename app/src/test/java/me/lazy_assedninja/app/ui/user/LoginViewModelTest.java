package me.lazy_assedninja.app.ui.user;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createUser;
import static me.lazy_assedninja.app.common.TestUtil.createUserDTO;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.user.login.LoginViewModel;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.User;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final LoginViewModel viewModel = new LoginViewModel(userRepository);

    @Test
    public void testNull() {
        assertThat(viewModel.user, notNullValue());

        verify(userRepository, never()).loadUser(any());
        viewModel.login(createUserDTO());
        verify(userRepository, never()).loadUser(any());
    }

    @Test
    public void sendResultToUI() {
        UserDTO userDTO = createUserDTO();
        MutableLiveData<Resource<User>> list = new MutableLiveData<>();
        when(userRepository.loadUser(userDTO)).thenReturn(list);
        Observer<Resource<User>> observer = mock(Observer.class);
        viewModel.user.observeForever(observer);
        viewModel.login(userDTO);
        verify(observer, never()).onChanged(any());

        Resource<User> resource = Resource.success(createUser());
        list.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void loadUser() {
        viewModel.user.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository);

        UserDTO userDTO = createUserDTO();
        viewModel.login(userDTO);
        verify(userRepository).loadUser(userDTO);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void loggedIn() {
        int userID = 1;
        String email = "email";
        viewModel.loggedIn(userID, email);

        verify(userRepository).setUserID(userID);
        verify(userRepository).setUserEmail(email);
    }

    @Test
    public void getUserEmail() {
        viewModel.getUserEmail();

        verify(userRepository).getUserEmail();
    }
}