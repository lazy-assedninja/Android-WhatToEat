package me.lazy_assedninja.app.ui.user.reset_password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.ResetPasswordFragmentBinding;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class ResetPasswordFragment extends BaseFragment {

    private ResetPasswordFragmentBinding binding;
    private ResetPasswordViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.reset_password_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initData();
    }

    private void initView() {
        binding.btResetPassword.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilOldPassword.getEditText() == null ||
                    binding.tilNewPassword.getEditText() == null) return;

            // Clear errors
            binding.tilOldPassword.setError(null);
            binding.tilNewPassword.setError(null);

            String oldPassword = binding.tilOldPassword.getEditText().getText().toString();
            String newPassword = binding.tilNewPassword.getEditText().getText().toString();
            if (oldPassword.isEmpty()) {
                binding.tilOldPassword.setError(getString(R.string.error_password_can_not_be_null));
            } else if (newPassword.isEmpty()) {
                binding.tilNewPassword.setError(getString(R.string.error_new_password_can_not_be_null));
            } else {
                viewModel.resetPassword(new UserDTO(oldPassword, newPassword));
            }
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResult(viewModel.result);
    }

    private void initData() {
        viewModel.result.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
                navController.navigateUp();
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}