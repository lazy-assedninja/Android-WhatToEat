package me.lazy_assedninja.app.ui.user;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createFile;
import static me.lazy_assedninja.app.common.TestUtil.createResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.repository.FileRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.user.profile.ProfileViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ProfileViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final FileRepository fileRepository = mock(FileRepository.class);
    private final ProfileViewModel viewModel = new ProfileViewModel(userRepository, fileRepository);

    @Test
    public void testNull() {
        // Bind google account
        assertThat(viewModel.bindGoogleResult, notNullValue());

        verify(userRepository, never()).bindGoogleAccount(any());
        viewModel.bindGoogleAccount("google ID", "email", "name",
                "picture URL");
        verify(userRepository, never()).bindGoogleAccount(any());

        // Upload file
        assertThat(viewModel.uploadResult, notNullValue());

        verify(fileRepository, never()).upload(any());
        viewModel.uploadFile(createFile());
        verify(fileRepository, never()).upload(any());
    }

    @Test
    public void sendResultToUI() {
        // Bind google account
        MutableLiveData<Event<Resource<Result>>> bindGoogleResult = new MutableLiveData<>();
        when(userRepository.bindGoogleAccount(any())).thenReturn(bindGoogleResult);
        Observer<Event<Resource<Result>>> bindGoogleObserver = mock(Observer.class);
        viewModel.bindGoogleResult.observeForever(bindGoogleObserver);
        viewModel.bindGoogleAccount("google ID", "email", "name",
                "picture URL");
        verify(bindGoogleObserver, never()).onChanged(any());
        Event<Resource<Result>> bindGoogleResource = new Event<>(Resource.success(createResult()));

        bindGoogleResult.setValue(bindGoogleResource);
        verify(bindGoogleObserver).onChanged(bindGoogleResource);

        // Upload file
        MutableLiveData<Event<Resource<Result>>> uploadFileResult = new MutableLiveData<>();
        when(fileRepository.upload(any())).thenReturn(uploadFileResult);
        Observer<Event<Resource<Result>>> uploadFileObserver = mock(Observer.class);
        viewModel.uploadResult.observeForever(uploadFileObserver);
        viewModel.uploadFile(createFile());
        verify(uploadFileObserver, never()).onChanged(any());
        Event<Resource<Result>> uploadFileResource = new Event<>(Resource.success(createResult()));

        uploadFileResult.setValue(uploadFileResource);
        verify(uploadFileObserver).onChanged(uploadFileResource);
    }

    @Test
    public void bindGoogleAccount() {
        viewModel.bindGoogleResult.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository);
        viewModel.bindGoogleAccount("google ID", "email", "name",
                "picture URL");
        verify(userRepository).getUserID();
        verify(userRepository).bindGoogleAccount(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadFile() {
        viewModel.uploadResult.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(fileRepository);
        viewModel.uploadFile(createFile());
        verify(fileRepository).upload(any());
        verifyNoMoreInteractions(fileRepository);
    }

    @Test
    public void getUser() {
        viewModel.getUser();

        verify(userRepository).getUserFromDb();
    }

    @Test
    public void getUserID() {
        viewModel.getUserID();

        verify(userRepository).getUserID();
    }

    @Test
    public void getUserEmail() {
        viewModel.getUserEmail();

        verify(userRepository).getUserEmail();
    }

    @Test
    public void logout() {
        viewModel.logout();

        verify(userRepository).deleteUser();
        verify(userRepository).setUserID(0);
    }
}