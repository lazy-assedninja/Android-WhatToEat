package me.lazy_assedninja.what_to_eat.ui.user.forget_password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.ForgetPasswordFragmentBinding;
import me.lazy_assedninja.what_to_eat.dto.UserDTO;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Status;

@AndroidEntryPoint
public class ForgetPasswordFragment extends BaseFragment {

    private AutoClearedValue<ForgetPasswordFragmentBinding> binding;
    private ForgetPasswordViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ForgetPasswordFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.forget_password_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
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
        binding.get().btSend.setOnClickListener(v -> {
            dismissKeyboard(v);
            EditText etEmail = binding.get().tilEmail.getEditText();
            if (etEmail == null) return;

            // Clear errors
            binding.get().tilEmail.setError(null);

            String email = etEmail.getText().toString();
            if (email.isEmpty()) {
                binding.get().tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else {
                viewModel.sendVerificationCode(new UserDTO(email));
            }
        });
        binding.get().btConfirm.setOnClickListener(v -> {
            dismissKeyboard(v);
            EditText etEmail = binding.get().tilEmail.getEditText();
            EditText etVerificationCode = binding.get().tilVerificationCode.getEditText();
            EditText etNewPassword = binding.get().tilNewPassword.getEditText();
            if (etEmail == null || etVerificationCode == null || etNewPassword == null) return;

            // Clear errors
            binding.get().tilEmail.setError(null);
            binding.get().tilVerificationCode.setError(null);
            binding.get().tilNewPassword.setError(null);

            String email = etEmail.getText().toString();
            String verificationCode = etVerificationCode.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            if (email.isEmpty()) {
                binding.get().tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else if (verificationCode.isEmpty()) {
                binding.get().tilVerificationCode.setError(getString(R.string.error_verification_code_can_not_be_null));
            } else if (newPassword.isEmpty()) {
                binding.get().tilNewPassword.setError(getString(R.string.error_new_password_can_not_be_null));
            } else {
                viewModel.forgetPassword(new UserDTO(email, verificationCode, newPassword));
            }
        });

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setSendVerificationResult(viewModel.sendVerificationResult);
        binding.get().setForgetPasswordResult(viewModel.forgetPasswordResult);
    }

    private void initData() {
        viewModel.sendVerificationResult.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getData() != null && resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
        viewModel.forgetPasswordResult.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getData() != null && resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
                navController.navigateUp();
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}