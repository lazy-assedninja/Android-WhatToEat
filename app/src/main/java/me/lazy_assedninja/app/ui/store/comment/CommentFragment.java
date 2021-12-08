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
import me.lazy_assedninja.app.util.AutoClearedValue;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;
import me.lazy_assedninja.library.util.ExecutorUtil;

@AndroidEntryPoint
public class CommentFragment extends BaseBottomSheetDialogFragment {

    private AutoClearedValue<CommentFragmentBinding> binding;
    private CommentViewModel viewModel;

    @Inject
    public ExecutorUtil executorUtil;

    private AutoClearedValue<CommentAdapter> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        CommentFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.comment_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
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
        CommentAdapter adapter = new CommentAdapter(executorUtil, dataBindingComponent);
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().rv.setAdapter(adapter);
        binding.get().btCreateComment.setOnClickListener(v -> {
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

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setComments(viewModel.comments);
    }

    private void initData() {
        if (getArguments() == null) return;
        int id = getArguments().getInt("store_id");
        viewModel.setId(id);
        viewModel.requestComment(new CommentDTO(id));

        viewModel.comments.observe(getViewLifecycleOwner(), listResource -> {
            List<Comment> list = listResource.getData();
            if (list != null) {
                adapter.get().submitList(list);
                binding.get().setSize(list.size());
            } else {
                adapter.get().submitList(emptyList());
                binding.get().setSize(0);
            }
        });
    }
}