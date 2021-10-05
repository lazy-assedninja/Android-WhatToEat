package me.lazy_assedninja.app.ui.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.PictureDTO;
import me.lazy_assedninja.app.repository.Event;
import me.lazy_assedninja.app.repository.FileRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.User;
import okhttp3.MultipartBody;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private FileRepository fileRepository;

    private final MutableLiveData<PictureDTO> uploadFile = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public LiveData<User> getUser() {
        return userRepository.getUserFromDb();
    }

    public String getUserEmail() {
        return userRepository.getUserEmail();
    }

    public void logout() {
        userRepository.deleteUser();
        userRepository.setUserID(0);
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(uploadFile, pictureDTO -> {
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