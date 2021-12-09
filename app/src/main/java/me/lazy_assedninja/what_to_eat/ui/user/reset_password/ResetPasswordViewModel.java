package me.lazy_assedninja.what_to_eat.ui.user.reset_password;

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
public class ResetPasswordViewModel extends ViewModel {

    private UserRepository userRepository;

    private final MutableLiveData<UserDTO> resetPassword = new MutableLiveData<>();

    @Inject
    public ResetPasswordViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(resetPassword, userDTO -> {
        if (userDTO == null) {
            return AbsentLiveData.create();
        } else {
            return userRepository.resetPassword(userDTO);
        }
    });

    public void resetPassword(UserDTO userDTO) {
        userDTO.setEmail(userRepository.getUserEmail());
        resetPassword.setValue(userDTO);
    }
}