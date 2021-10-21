package me.lazy_assedninja.app.ui.store.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.PostDTO;
import me.lazy_assedninja.app.repository.PostRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.app.vo.Resource;

@HiltViewModel
public class PostViewModel extends ViewModel {

    private final UserRepository userRepository;
    private PostRepository postRepository;

    private final MutableLiveData<PostDTO> postRequest = new MutableLiveData<>();

    private int id;

    @Inject
    public PostViewModel(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public boolean isLoggedIn() {
        return userRepository.getUserID() == 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public final LiveData<Resource<List<Post>>> posts = Transformations.switchMap(postRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return postRepository.loadPosts(request);
        }
    });

    public void requestPosts(int storeID) {
        if (postRequest.getValue() == null) {
            postRequest.setValue(new PostDTO(storeID));
        }
    }
}