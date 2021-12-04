package me.lazy_assedninja.app.ui.user.forget_password;

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
import me.lazy_assedninja.app.databinding.ForgetPasswordFragmentBinding;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class ForgetPasswordFragment extends BaseFragment {

    private ForgetPasswordFragmentBinding binding;
    private ForgetPasswordViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.forget_password_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ForgetPasswordViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initData();
    }

    private void initView() {
        binding.btSend.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilEmail.getEditText() == null || binding.tilVerificationCode.getEditText() == null ||
                    binding.tilNewPassword.getEditText() == null) return;

            // Clear errors
            binding.tilEmail.setError(null);

            String email = binding.tilEmail.getEditText().getText().toString();
            if (email.isEmpty()) {
                binding.tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else {
                viewModel.sendVerificationCode(email);
            }
        });
        binding.btConfirm.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilEmail.getEditText() == null || binding.tilVerificationCode.getEditText() == null ||
                    binding.tilNewPassword.getEditText() == null) return;

            // Clear errors
            binding.tilEmail.setError(null);
            binding.tilVerificationCode.setError(null);
            binding.tilNewPassword.setError(null);

            String email = binding.tilEmail.getEditText().getText().toString();
            String verificationCode = binding.tilVerificationCode.getEditText().getText().toString();
            String newPassword = binding.tilNewPassword.getEditText().getText().toString();
            if (email.isEmpty()) {
                binding.tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else if (verificationCode.isEmpty()) {
                binding.tilVerificationCode.setError(getString(R.string.error_verification_code_can_not_be_null));
            } else if (newPassword.isEmpty()) {
                binding.tilNewPassword.setError(getString(R.string.error_new_password_can_not_be_null));
            } else {
                viewModel.forgetPassword(email, verificationCode, newPassword);
            }
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setSendVerificationResult(viewModel.sendVerificationResult);
        binding.setForgetPasswordResult(viewModel.forgetPasswordResult);
    }

    private void initData() {
        viewModel.sendVerificationResult.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
        viewModel.forgetPasswordResult.observe(getViewLifecycleOwner(), event -> {
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