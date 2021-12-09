package me.lazy_assedninja.what_to_eat.ui.user;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createFile;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createGoogleAccount;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.what_to_eat.repository.FileRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.ui.user.profile.ProfileViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.GoogleAccount;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import okhttp3.MultipartBody;

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
        viewModel.bindGoogleAccount(createGoogleAccount());
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
        GoogleAccount googleAccount = createGoogleAccount();
        MutableLiveData<Event<Resource<Result>>> bindGoogleResult = new MutableLiveData<>();
        when(userRepository.bindGoogleAccount(googleAccount)).thenReturn(bindGoogleResult);
        Observer<Event<Resource<Result>>> bindGoogleObserver = mock(Observer.class);
        viewModel.bindGoogleResult.observeForever(bindGoogleObserver);
        viewModel.bindGoogleAccount(googleAccount);
        verify(bindGoogleObserver, never()).onChanged(any());

        Event<Resource<Result>> bindGoogleResource = new Event<>(Resource.success(createResult()));
        bindGoogleResult.setValue(bindGoogleResource);
        verify(bindGoogleObserver).onChanged(bindGoogleResource);

        // Upload file
        MultipartBody.Part file = createFile();
        MutableLiveData<Event<Resource<Result>>> uploadFileResult = new MutableLiveData<>();
        when(fileRepository.upload(file)).thenReturn(uploadFileResult);
        Observer<Event<Resource<Result>>> uploadFileObserver = mock(Observer.class);
        viewModel.uploadResult.observeForever(uploadFileObserver);
        viewModel.uploadFile(file);
        verify(uploadFileObserver, never()).onChanged(any());

        Event<Resource<Result>> uploadFileResource = new Event<>(Resource.success(createResult()));
        uploadFileResult.setValue(uploadFileResource);
        verify(uploadFileObserver).onChanged(uploadFileResource);
    }

    @Test
    public void bindGoogleAccount() {
        viewModel.bindGoogleResult.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(userRepository);

        GoogleAccount googleAccount = createGoogleAccount();
        viewModel.bindGoogleAccount(googleAccount);
        verify(userRepository).getUserID();
        verify(userRepository).bindGoogleAccount(googleAccount);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadFile() {
        viewModel.uploadResult.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(fileRepository);

        MultipartBody.Part file = createFile();
        viewModel.uploadFile(file);
        verify(fileRepository).upload(file);
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