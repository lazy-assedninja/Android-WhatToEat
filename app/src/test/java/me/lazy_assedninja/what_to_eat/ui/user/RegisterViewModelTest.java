package me.lazy_assedninja.what_to_eat.ui.user;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static me.lazy_assedninja.what_to_eat.common.TestUtil.createResult;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createUser;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.user.register.RegisterViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.User;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class RegisterViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RegisterViewModel viewModel = new RegisterViewModel(userRepository);

    @Test
    public void testNull(){
        assertThat(viewModel.result, notNullValue());

        verify(userRepository, never()).register(any());
        viewModel.register(createUser());
        verify(userRepository, never()).register(any());
    }

    @Test
    public void sendResultToUI(){
        User user = createUser();
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(userRepository.register(user)).thenReturn(result);
        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        viewModel.result.observeForever(observer);
        viewModel.register(user);
        verify(observer, never()).onChanged(any());

        Event<Resource<Result>> resource = new Event<>(Resource.success(createResult()));
        result.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void register(){
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository);

        User user = createUser();
        viewModel.register(user);
        verify(userRepository).register(user);
        verifyNoMoreInteractions(userRepository);
    }
}