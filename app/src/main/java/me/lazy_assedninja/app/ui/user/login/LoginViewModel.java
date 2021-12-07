package me.lazy_assedninja.app.ui.user.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.util.AbsentLiveData;
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

    public void loggedIn(int userID, String email) {
        userRepository.setUserID(userID);
        userRepository.setUserEmail(email);
    }

    public String getUserEmail() {
        return userRepository.getUserEmail();
    }

    public LiveData<Resource<User>> user = Transformations.switchMap(login, userDTO -> {
        if (userDTO == null) {
            return AbsentLiveData.create();
        } else {
            return userRepository.loadUser(userDTO);
        }
    });

    public void login(UserDTO userDTO) {
        login.setValue(userDTO);
    }

    public void googleLogin(String googleID) {
        login.setValue(new UserDTO(googleID, "", true));
    }
}