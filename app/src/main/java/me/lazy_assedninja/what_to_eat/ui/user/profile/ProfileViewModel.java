package me.lazy_assedninja.what_to_eat.ui.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.dto.PictureDTO;
import me.lazy_assedninja.what_to_eat.repository.FileRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.GoogleAccount;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.User;
import okhttp3.MultipartBody;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private UserRepository userRepository;
    private FileRepository fileRepository;

    private final MutableLiveData<PictureDTO> uploadFile = new MutableLiveData<>();
    private final MutableLiveData<GoogleAccount> bindGoogle = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public LiveData<User> getUser() {
        return userRepository.getUserFromDb();
    }

    public int getUserID(){
        return userRepository.getUserID();
    }

    public String getUserEmail() {
        return userRepository.getUserEmail();
    }

    public void logout() {
        userRepository.deleteUser();
        userRepository.setUserID(0);
    }

    public LiveData<Event<Resource<Result>>> bindGoogleResult =
            Transformations.switchMap(bindGoogle, userDTO -> {
                if (userDTO == null) {
                    return AbsentLiveData.create();
                } else {
                    return userRepository.bindGoogleAccount(userDTO);
                }
            });

    public void bindGoogleAccount(GoogleAccount googleAccount) {
        googleAccount.setUserID(userRepository.getUserID());
        bindGoogle.setValue(googleAccount);
    }

    public LiveData<Event<Resource<Result>>> uploadResult =
            Transformations.switchMap(uploadFile, pictureDTO -> {
                if (pictureDTO == null) {
                    return AbsentLiveData.create();
                } else {
                    return fileRepository.upload(pictureDTO.getFile());
                }
            });

    public void uploadFile(MultipartBody.Part file) {
        uploadFile.setValue(new PictureDTO(file));
    }
}