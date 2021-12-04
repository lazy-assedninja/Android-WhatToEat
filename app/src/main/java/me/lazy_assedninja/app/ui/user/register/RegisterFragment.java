package me.lazy_assedninja.app.ui.user.register;

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
import me.lazy_assedninja.app.databinding.RegisterFragmentBinding;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class RegisterFragment extends BaseFragment {

    private RegisterFragmentBinding binding;
    private RegisterViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.register_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initData();
    }

    private void initView() {
        binding.btRegister.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilName.getEditText() == null || binding.tilEmail.getEditText() == null ||
                    binding.tilPassword.getEditText() == null || binding.tilConfirmPassword.getEditText() == null)
                return;

            // Clear errors
            binding.tilName.setError(null);
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);
            binding.tilConfirmPassword.setError(null);

            String name = binding.tilName.getEditText().getText().toString();
            String email = binding.tilEmail.getEditText().getText().toString();
            String password = binding.tilPassword.getEditText().getText().toString();
            String confirmPassword = binding.tilConfirmPassword.getEditText().getText().toString();
            if (name.isEmpty()) {
                binding.tilName.setError(getString(R.string.error_name_can_not_be_null));
            } else if (email.isEmpty()) {
                binding.tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else if (password.isEmpty()) {
                binding.tilPassword.setError(getString(R.string.error_password_can_not_be_null));
            } else if (confirmPassword.isEmpty()) {
                binding.tilConfirmPassword.setError(getString(R.string.error_confirm_password_can_not_be_null));
            } else if (!password.equals(confirmPassword)) {
                binding.tilPassword.setError(getString(R.string.error_passwords_are_not_the_same));
                binding.tilConfirmPassword.setError(getString(R.string.error_passwords_are_not_the_same));
            } else {
                viewModel.register(name, email, password);
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