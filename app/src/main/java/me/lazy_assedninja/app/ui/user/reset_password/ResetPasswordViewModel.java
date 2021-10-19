package me.lazy_assedninja.app.ui.user.reset_password;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

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

    public void resetPassword(String oldPassword, String newPassword) {
        resetPassword.setValue(new UserDTO(userRepository.getUserEmail(), oldPassword, newPassword,
                true));
    }
}