package me.lazy_assedninja.what_to_eat.repository;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createGoogleAccount;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createResult;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createUser;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createUserDTO;
import static me.lazy_assedninja.what_to_eat.util.ApiUtil.successCall;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.library.util.EncryptUtil;
import me.lazy_assedninja.library.util.TimeUtil;
import me.lazy_assedninja.what_to_eat.api.ApiResponse;
import me.lazy_assedninja.what_to_eat.api.WhatToEatService;
import me.lazy_assedninja.what_to_eat.db.UserDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.dto.UserDTO;
import me.lazy_assedninja.what_to_eat.util.InstantExecutorUtil;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.GoogleAccount;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.User;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class UserRepositoryTest {

    private final String USER_ID = "user_id";
    private final String USER_EMAIL = "user_email";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private UserRepository repository;
    private final Context context = mock(Context.class);
    private final EncryptUtil encryptUtil = mock(EncryptUtil.class);
    private final TimeUtil timeUtil = mock(TimeUtil.class);
    private final UserDao userDao = mock(UserDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    private final SharedPreferences sharedPreferences = mock(SharedPreferences.class);
    private final SharedPreferences.Editor sharedPreferencesEditor = mock(SharedPreferences.Editor.class);

    private final String userEmail = "user email";
    private final String newPassword = "new password";
    private final String googleID = "google ID";

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.userDao()).thenReturn(userDao);
        repository = new UserRepository(context, new InstantExecutorUtil(), encryptUtil, timeUtil,
                userDao, service);

        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
    }

    @Test
    public void loadUserFromNetwork() {
        MutableLiveData<User> dbData = new MutableLiveData<>();
        when(userDao.get()).thenReturn(dbData);

        UserDTO userDTO = createUserDTO();
        User user = createUser();
        LiveData<ApiResponse<User>> call = successCall(user);
        when(service.login(userDTO)).thenReturn(call);

        LiveData<Resource<User>> data = repository.loadUser(userDTO);
        verify(userDao).get();
        verifyNoMoreInteractions(service);

        Observer<Resource<User>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<User> updateDbData = new MutableLiveData<>();
        when(userDao.get()).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).login(userDTO);
        verify(userDao).insert(user);

        updateDbData.postValue(user);
        verify(observer).onChanged(Resource.success(user));
    }

    @Test
    public void loadUserWithGoogleFromNetwork() {
        MutableLiveData<User> dbData = new MutableLiveData<>();
        when(userDao.get()).thenReturn(dbData);

        UserDTO userDTO = createUserDTO(googleID);
        User user = createUser();
        LiveData<ApiResponse<User>> call = successCall(user);
        when(service.googleLogin(userDTO)).thenReturn(call);

        LiveData<Resource<User>> data = repository.loadUser(userDTO);
        verify(userDao).get();
        verifyNoMoreInteractions(service);

        Observer<Resource<User>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<User> updateDbData = new MutableLiveData<>();
        when(userDao.get()).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).googleLogin(userDTO);
        verify(userDao).insert(user);

        updateDbData.postValue(user);
        verify(observer).onChanged(Resource.success(user));
    }

    @Test
    public void loadUserFromDb() {
        MutableLiveData<User> dbData = new MutableLiveData<>();
        when(userDao.get()).thenReturn(dbData);

        UserDTO userDTO = createUserDTO();
        Observer<Resource<User>> observer = mock(Observer.class);
        repository.loadUser(userDTO).observeForever(observer);
        verify(userDao).get();
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));

        User user = createUser();
        dbData.postValue(user);
        verify(observer).onChanged(Resource.success(user));
    }

    @Test
    public void setUserID() {
        when(sharedPreferencesEditor.putInt(anyString(), anyInt()))
                .thenReturn(sharedPreferencesEditor);
        int userID = 1;
        repository.setUserID(userID);

        verify(sharedPreferencesEditor).putInt(USER_ID, userID);
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void getUserID() {
        repository.getUserID();

        int defaultValue = 0;
        verify(sharedPreferences).getInt(USER_ID, defaultValue);
    }

    @Test
    public void setUserEmail() {
        when(sharedPreferencesEditor.putString(anyString(), anyString()))
                .thenReturn(sharedPreferencesEditor);
        repository.setUserEmail(userEmail);

        verify(sharedPreferencesEditor).putString(USER_EMAIL, userEmail);
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void getUserEmail() {
        repository.getUserEmail();

        String defaultValue = "";
        verify(sharedPreferences).getString(USER_EMAIL, defaultValue);
    }

    @Test
    public void getUserFromDb() {
        repository.getUserFromDb();

        verify(userDao).get();
    }

    @Test
    public void deleteUser() {
        repository.deleteUser();

        verify(userDao).delete();
    }

    @Test
    public void register() {
        when(sharedPreferencesEditor.putString(anyString(), anyString()))
                .thenReturn(sharedPreferencesEditor);

        User user = createUser();
        user.setEmail(userEmail);
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.register(user)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.register(user).observeForever(observer);
        verify(service).register(user);

        verify(sharedPreferencesEditor).putString(USER_EMAIL, user.getEmail());
        verify(sharedPreferencesEditor).apply();

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }

    @Test
    public void bindGoogleAccount() {
        GoogleAccount googleAccount = createGoogleAccount();
        googleAccount.setGoogleID(googleID);
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.bindGoogleAccount(googleAccount)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.bindGoogleAccount(googleAccount).observeForever(observer);
        verify(service).bindGoogleAccount(googleAccount);
        verify(userDao).updateGoogleID(googleAccount.getGoogleID(), timeUtil.now());

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }

    @Test
    public void resetPassword() {
        UserDTO userDTO = createUserDTO();
        userDTO.setNewPassword(newPassword);
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.resetPassword(userDTO)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.resetPassword(userDTO).observeForever(observer);
        verify(service).resetPassword(userDTO);
        verify(userDao).updatePassword(userDTO.getNewPassword(), timeUtil.now());

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }

    @Test
    public void sendVerificationCode() {
        UserDTO userDTO = createUserDTO();
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.sendVerificationCode(userDTO)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.sendVerificationCode(userDTO).observeForever(observer);
        verify(service).sendVerificationCode(userDTO);

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }

    @Test
    public void forgetPassword() {
        UserDTO userDTO = createUserDTO();
        userDTO.setNewPassword(newPassword);
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.forgetPassword(userDTO)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.forgetPassword(userDTO).observeForever(observer);
        verify(service).forgetPassword(userDTO);
        verify(userDao).updatePassword(userDTO.getNewPassword(), timeUtil.now());

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }
}