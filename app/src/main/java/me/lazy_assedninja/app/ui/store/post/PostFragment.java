package me.lazy_assedninja.app.ui.store.post;

import static java.util.Collections.emptyList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.PostFragmentBinding;
import me.lazy_assedninja.app.dto.PostDTO;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;
import me.lazy_assedninja.library.util.ExecutorUtil;

@AndroidEntryPoint
public class PostFragment extends BaseBottomSheetDialogFragment {

    private PostFragmentBinding binding;
    private PostViewModel viewModel;

    @Inject
    public ExecutorUtil executorUtil;

    private PostAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.post_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        initView();
        initData();
    }

    private void initView() {
        adapter = new PostAdapter(executorUtil);
        binding.rv.setAdapter(adapter);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setPosts(viewModel.posts);
    }

    private void initData() {
        if (getArguments() == null) return;
        int id = getArguments().getInt("store_id");
        viewModel.setId(id);

        viewModel.requestPost(new PostDTO(id));
        viewModel.posts.observe(getViewLifecycleOwner(), listResource -> {
            List<Post> list = listResource.getData();
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