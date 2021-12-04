package me.lazy_assedninja.app.ui.store.comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.dto.CommentDTO;
import me.lazy_assedninja.app.repository.CommentRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.util.AbsentLiveData;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.app.vo.Resource;

@HiltViewModel
public class CommentViewModel extends ViewModel {

    private final UserRepository userRepository;
    private CommentRepository commentRepository;

    private final MutableLiveData<CommentDTO> commentRequest = new MutableLiveData<>();

    private int id;

    @Inject
    public CommentViewModel(UserRepository userRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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

    public final LiveData<Resource<List<Comment>>> comments = Transformations.switchMap(commentRequest, request -> {
        if (request == null) {
            return AbsentLiveData.create();
        } else {
            return commentRepository.loadComments(request);
        }
    });

    public void requestComments(int storeID) {
        if (commentRequest.getValue() == null) {
            commentRequest.setValue(new CommentDTO(storeID));
        }
    }
}