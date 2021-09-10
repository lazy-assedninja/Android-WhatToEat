package me.lazy_assedninja.app.ui.user.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.User;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private UserRepository userRepository;

    private final MutableLiveData<UserDTO> login = new MutableLiveData<>();

    @Inject
    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loggedIn(int LoggedInUserID) {
        userRepository.setUserID(LoggedInUserID);
    }

    public LiveData<Resource<User>> user = Transformations.switchMap(login, userDTO -> {
        if (userDTO == null) {
            return AbsentLiveData.create();
        } else {
            return userRepository.loadUser(userDTO);
        }
    });

    public void setLogin(String email, String password) {
        if (login.getValue() == null || !login.getValue().getEmail().equals(email) ||
                !login.getValue().getPassword().equals(password)) {
            login.setValue(new UserDTO(email, password));
        }
    }
}