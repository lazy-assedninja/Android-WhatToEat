package me.lazy_assedninja.app.ui.store.comment;

import static java.util.Collections.emptyList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.binding.ImageDataBindingComponent;
import me.lazy_assedninja.app.databinding.CommentFragmentBinding;
import me.lazy_assedninja.app.dto.CommentDTO;
import me.lazy_assedninja.app.ui.store.comment.create_comment.CreateCommentFragment;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;
import me.lazy_assedninja.library.util.ExecutorUtil;

@AndroidEntryPoint
public class CommentFragment extends BaseBottomSheetDialogFragment {

    private CommentFragmentBinding binding;
    private CommentViewModel viewModel;

    @Inject
    public ExecutorUtil executorUtil;

    private CommentAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.comment_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);

        initView();
        initData();
    }

    private void initView() {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        adapter = new CommentAdapter(executorUtil, dataBindingComponent);
        binding.rv.setAdapter(adapter);
        binding.btCreateComment.setOnClickListener(v -> {
            if (viewModel.isLoggedIn()) {
                showToast(R.string.error_please_login_first);
                return;
            }

            CreateCommentFragment createCommentFragment = new CreateCommentFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("store_id", viewModel.getId());
            createCommentFragment.setArguments(bundle);
            createCommentFragment.show(getParentFragmentManager(), "add_comment");
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setComments(viewModel.comments);
    }

    private void initData() {
        if (getArguments() == null) return;
        int id = getArguments().getInt("store_id");
        viewModel.setId(id);
        viewModel.requestComment(new CommentDTO(id));

        viewModel.comments.observe(getViewLifecycleOwner(), listResource -> {
            List<Comment> list = listResource.getData();
            if (list != null) {
                adapter.submitList(list);
                binding.setSize(list.size());
            } else {
                adapter.submitList(emptyList());
                binding.setSize(0);
            }
        });
    }
}