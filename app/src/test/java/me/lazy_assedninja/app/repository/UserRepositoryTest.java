package me.lazy_assedninja.app.repository;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createGoogleAccount;
import static me.lazy_assedninja.app.common.TestUtil.createResult;
import static me.lazy_assedninja.app.common.TestUtil.createUser;
import static me.lazy_assedninja.app.common.TestUtil.createUserDTO;
import static me.lazy_assedninja.app.util.ApiUtil.successCall;

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

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.UserDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.GoogleAccount;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.util.EncryptUtil;
import me.lazy_assedninja.library.util.TimeUtil;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class UserRepositoryTest {

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

        UserDTO userDTO = createUserDTO("google ID");
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
        String KEY = "user_id";
        int userID = 1;
        repository.setUserID(userID);

        verify(sharedPreferencesEditor).putInt(KEY, userID);
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void getUserID() {
        repository.getUserID();

        String KEY = "user_id";
        int defaultValue = 0;
        verify(sharedPreferences).getInt(KEY, defaultValue);
    }

    @Test
    public void setUserEmail() {
        when(sharedPreferencesEditor.putString(anyString(), anyString()))
                .thenReturn(sharedPreferencesEditor);
        String KEY = "user_email";
        String userEmail = "user email";
        repository.setUserEmail(userEmail);

        verify(sharedPreferencesEditor).putString(KEY, userEmail);
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void getUserEmail() {
        repository.getUserEmail();

        String KEY = "user_email";
        String defaultValue = "";
        verify(sharedPreferences).getString(KEY, defaultValue);
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
        user.setEmail("user email");
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.register(user)).thenReturn(call);

        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        repository.register(user).observeForever(observer);
        verify(service).register(user);

        String KEY = "user_email";
        verify(sharedPreferencesEditor).putString(KEY, user.getEmail());
        verify(sharedPreferencesEditor).apply();

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }

    @Test
    public void bindGoogleAccount() {
        GoogleAccount googleAccount = createGoogleAccount();
        googleAccount.setGoogleID("google ID");
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
        userDTO.setNewPassword("new password");
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
        userDTO.setNewPassword("new password");
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