package me.lazy_assedninja.app.ui.user.register;

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
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.RegisterFragmentBinding;
import me.lazy_assedninja.app.util.AutoClearedValue;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.app.vo.User;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class RegisterFragment extends BaseFragment {

    private AutoClearedValue<RegisterFragmentBinding> binding;
    private RegisterViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RegisterFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.register_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
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
        binding.get().btRegister.setOnClickListener(v -> {
            dismissKeyboard(v);
            EditText etName = binding.get().tilName.getEditText();
            EditText etEmail = binding.get().tilEmail.getEditText();
            EditText etPassword = binding.get().tilPassword.getEditText();
            EditText etConfirmPassword = binding.get().tilConfirmPassword.getEditText();
            if (etName == null || etEmail == null || etPassword == null || etConfirmPassword == null)
                return;

            // Clear errors
            binding.get().tilName.setError(null);
            binding.get().tilEmail.setError(null);
            binding.get().tilPassword.setError(null);
            binding.get().tilConfirmPassword.setError(null);

            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            if (name.isEmpty()) {
                binding.get().tilName.setError(getString(R.string.error_name_can_not_be_null));
            } else if (email.isEmpty()) {
                binding.get().tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else if (password.isEmpty()) {
                binding.get().tilPassword.setError(getString(R.string.error_password_can_not_be_null));
            } else if (confirmPassword.isEmpty()) {
                binding.get().tilConfirmPassword.setError(getString(R.string.error_confirm_password_can_not_be_null));
            } else if (!password.equals(confirmPassword)) {
                binding.get().tilPassword.setError(getString(R.string.error_passwords_are_not_the_same));
                binding.get().tilConfirmPassword.setError(getString(R.string.error_passwords_are_not_the_same));
            } else {
                viewModel.register(new User(email, password, name, email + ".jpg",
                        "user"));
            }
        });

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setResult(viewModel.result);
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