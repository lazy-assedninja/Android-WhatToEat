package me.lazy_assedninja.what_to_eat.ui.user.forget_password;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.dto.UserDTO;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;

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

    public void sendVerificationCode(UserDTO userDTO) {
        sendVerificationCode.setValue(userDTO);
    }

    public void forgetPassword(UserDTO userDTO) {
        forgetPassword.setValue(userDTO);
    }
}