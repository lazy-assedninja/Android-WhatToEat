package me.lazy_assedninja.app.ui.user.forget_password;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.util.AbsentLiveData;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@HiltViewModel
public class ForgetPasswordViewModel extends ViewModel {

    private UserRepository userRepository;

    private final MutableLiveData<UserDTO> sendVerificationCode = new MutableLiveData<>();
    private final MutableLiveData<UserDTO> forgetPassword = new MutableLiveData<>();

    @Inject
    public ForgetPasswordViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Event<Resource<Result>>> sendVerificationResult = Transformations.switchMap(sendVerificationCode, userDTO -> {
        if (userDTO == null) {
            return AbsentLiveData.create();
        } else {
            return userRepository.sendVerificationCode(userDTO);
        }
    });

    public LiveData<Event<Resource<Result>>> forgetPasswordResult = Transformations.switchMap(forgetPassword, userDTO -> {
        if (userDTO == null) {
            return AbsentLiveData.create();
        } else {
            return userRepository.forgetPassword(userDTO);
        }
    });

    public void sendVerificationCode(String email) {
        sendVerificationCode.setValue(new UserDTO(email));
    }

    public void forgetPassword(String email, String verificationCode, String newPassword) {
        forgetPassword.setValue(new UserDTO(email, verificationCode, newPassword,
                false));
    }
}