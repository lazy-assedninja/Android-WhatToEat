package me.lazy_assedninja.what_to_eat.ui.store.comment.create_comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.CreateCommentFragmentBinding;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.what_to_eat.vo.Comment;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Status;

@AndroidEntryPoint
public class CreateCommentFragment extends BaseBottomSheetDialogFragment {

    private AutoClearedValue<CreateCommentFragmentBinding> binding;
    private CreateCommentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        CreateCommentFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.create_comment_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
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
        binding.get().btComment.setOnClickListener(v -> {
            dismissKeyboard(v);
            EditText etContent = binding.get().tilContent.getEditText();
            if (etContent == null) return;

            // Clear errors
            binding.get().tilContent.setError(null);

            String star = String.valueOf(binding.get().ratingBar.getRating());
            String content = etContent.getText().toString();
            if (content.isEmpty()) {
                binding.get().tilContent.setError(getString(R.string.error_content_can_not_be_null));
            } else {
                viewModel.createComment(new Comment(star, content));
            }
        });

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setResult(viewModel.result);
    }

    private void initData() {
        if (getArguments() == null) dismiss();
        int id = getArguments().getInt("store_id");
        viewModel.setId(id);

        viewModel.result.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getData() != null && resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
                dismiss();
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}