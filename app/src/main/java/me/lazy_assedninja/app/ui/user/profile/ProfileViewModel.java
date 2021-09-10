package me.lazy_assedninja.app.ui.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.vo.User;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;

    @Inject
    public ProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<User> getUser() {
        return userRepository.getUserFromDb();
    }

    public void logout() {
        userRepository.deleteUser();
        userRepository.setUserID(0);
    }
}