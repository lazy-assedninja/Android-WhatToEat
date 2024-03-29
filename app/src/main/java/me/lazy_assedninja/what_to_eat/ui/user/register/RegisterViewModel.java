package me.lazy_assedninja.what_to_eat.ui.user.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.User;

@HiltViewModel
public class RegisterViewModel extends ViewModel {

    private UserRepository userRepository;

    private final MutableLiveData<User> register = new MutableLiveData<>();

    @Inject
    public RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(register, user -> {
        if (user == null) {
            return AbsentLiveData.create();
        } else {
            return userRepository.register(user);
        }
    });

    public void register(User user) {
        register.setValue(user);
    }
}