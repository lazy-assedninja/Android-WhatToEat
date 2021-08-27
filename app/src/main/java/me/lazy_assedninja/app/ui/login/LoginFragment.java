package me.lazy_assedninja.app.ui.login;

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

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.FragmentLoginBinding;
import me.lazy_assedninja.app.dto.UserRequest;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.ui.BaseFragment;

public class LoginFragment extends BaseFragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_login,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initData();
    }

    private void initView() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResource(viewModel.user);
        binding.btForgetPassword.setOnClickListener(v ->
                navController.navigate(R.id.action_to_fragment_verification));
        binding.btLogin.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilEmail.getEditText() == null || binding.tilPassword.getEditText() == null)
                return;

            String email = binding.tilEmail.getEditText().getText().toString();
            String password = binding.tilPassword.getEditText().getText().toString();
            if (email.isEmpty()) {
                binding.tilEmail.setError(getString(R.string.error_email_can_not_be_null));
                binding.tilPassword.setError(null);
            } else if (password.isEmpty()) {
                binding.tilEmail.setError(null);
                binding.tilPassword.setError(getString(R.string.error_password_can_not_be_null));
            } else {
                binding.tilEmail.setError(null);
                binding.tilPassword.setError(null);
                viewModel.setLogin(new UserRequest(email, password));
            }
        });
    }

    private void initData() {
        viewModel.user.observe(getViewLifecycleOwner(), userResource -> {
            if (userResource.getStatus().equals(Resource.SUCCESS)) {
                viewModel.setLoggedIn(userResource.getData().getId());
                showToast(R.string.success_login);
                navController.navigateUp();
            } else if (userResource.getStatus().equals(Resource.ERROR)) {
                showToast(userResource.getMessage());
            }
        });
    }
}