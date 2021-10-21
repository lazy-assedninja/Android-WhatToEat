package me.lazy_assedninja.app.ui.store.comment.create_comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.CommentRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.TimeUtils;

@HiltViewModel
public class CreateCommentViewModel extends ViewModel {

    private final TimeUtils timeUtils;
    private final UserRepository userRepository;
    private CommentRepository commentRepository;

    private final MutableLiveData<Comment> createComment = new MutableLiveData<>();

    private int id;

    @Inject
    public CreateCommentViewModel(TimeUtils timeUtils, UserRepository userRepository,
                                  CommentRepository commentRepository) {
        this.timeUtils = timeUtils;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(createComment, comment -> {
        if (comment == null) {
            return AbsentLiveData.create();
        } else {
            return commentRepository.addComment(comment);
        }
    });

    public void createComment(String star, String content) {
        createComment.setValue(new Comment(star, content, timeUtils.now(), id, userRepository.getUserID()));
    }
}