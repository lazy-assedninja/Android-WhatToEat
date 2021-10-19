package me.lazy_assedninja.app.ui.store.comment.create_comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.CreateCommentFragmentBinding;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;

@AndroidEntryPoint
public class CreateCommentFragment extends BaseBottomSheetDialogFragment {

    private CreateCommentFragmentBinding binding;
    private CreateCommentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.create_comment_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateCommentViewModel.class);

        initView();
        initData();
    }

    private void initView() {
        binding.btComment.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilContent.getEditText() == null) return;

            // Clear errors
            binding.tilContent.setError(null);

            String star = String.valueOf(binding.ratingBar.getRating());
            String content = binding.tilContent.getEditText().getText().toString();
            if (content.isEmpty()) {
                binding.tilContent.setError(getString(R.string.error_content_can_not_be_null));
            } else {
                viewModel.createComment(star, content);
            }
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResult(viewModel.result);
    }

    private void initData() {
        if (getArguments() == null) return;
        int id = getArguments().getInt("store_id");
        viewModel.setId(id);

        viewModel.result.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getStatus().equals(Resource.SUCCESS)) {
                showToast(resultResource.getData().getResult());
                dismiss();
            } else if (resultResource.getStatus().equals(Resource.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}