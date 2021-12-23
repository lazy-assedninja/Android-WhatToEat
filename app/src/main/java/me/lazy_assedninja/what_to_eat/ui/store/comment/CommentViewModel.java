package me.lazy_assedninja.what_to_eat.ui.store.comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.dto.CommentDTO;
import me.lazy_assedninja.what_to_eat.repository.CommentRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Resource;

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

    public final LiveData<Resource<List<Comment>>> comments =
            Transformations.switchMap(commentRequest, request -> {
                if (request == null) {
                    return AbsentLiveData.create();
                } else {
                    return commentRepository.loadComments(request);
                }
            });

    public void requestComment(CommentDTO commentDTO) {
        if (commentRequest.getValue() == null) {
            commentRequest.setValue(commentDTO);
        }
    }
}