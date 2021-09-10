package me.lazy_assedninja.app.ui.user.login;

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
import me.lazy_assedninja.app.databinding.LoginFragmentBinding;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class LoginFragment extends BaseFragment {

    private LoginFragmentBinding binding;
    private LoginViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.login_fragment,
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
        binding.btForgetPassword.setOnClickListener(v ->
                navController.navigate(R.id.action_to_forget_password_fragment));
        binding.btLogin.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilEmail.getEditText() == null || binding.tilPassword.getEditText() == null)
                return;

            // Clear errors
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);

            String email = binding.tilEmail.getEditText().getText().toString();
            String password = binding.tilPassword.getEditText().getText().toString();
            if (email.isEmpty()) {
                binding.tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else if (password.isEmpty()) {
                binding.tilPassword.setError(getString(R.string.error_password_can_not_be_null));
            } else {
                viewModel.login(email, password);
            }
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setUser(viewModel.user);
    }

    private void initData() {
        if (!viewModel.getUserEmail().isEmpty() && binding.tilEmail.getEditText() != null)
            binding.tilEmail.getEditText().setText(viewModel.getUserEmail());

        viewModel.user.observe(getViewLifecycleOwner(), userResource -> {
            if (userResource.getStatus().equals(Resource.SUCCESS)) {
                showToast(R.string.success_login);
                viewModel.loggedIn(userResource.getData().getId(), userResource.getData().getEmail());
                navController.navigateUp();
            } else if (userResource.getStatus().equals(Resource.ERROR)) {
                showToast(userResource.getMessage());
            }
        });
    }
}