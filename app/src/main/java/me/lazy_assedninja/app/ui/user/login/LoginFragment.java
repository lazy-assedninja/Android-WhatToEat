package me.lazy_assedninja.app.ui.user.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.LoginFragmentBinding;
import me.lazy_assedninja.app.dto.UserDTO;
import me.lazy_assedninja.app.util.AutoClearedValue;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class LoginFragment extends BaseFragment {

    private AutoClearedValue<LoginFragmentBinding> binding;
    private LoginViewModel viewModel;

    private NavController navController;

    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> googleSignIn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LoginFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.login_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initGoogleSignIn();
        initData();
        initActivityResult();
    }

    private void initView() {
        binding.get().btForgetPassword.setOnClickListener(v ->
                navController.navigate(R.id.action_to_forget_password_fragment));
        binding.get().btLogin.setOnClickListener(v -> {
            dismissKeyboard(v);
            EditText etEmail = binding.get().tilEmail.getEditText();
            EditText etPassword = binding.get().tilPassword.getEditText();
            if (etEmail == null || etPassword == null)
                return;

            // Clear errors
            binding.get().tilEmail.setError(null);
            binding.get().tilPassword.setError(null);

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.isEmpty()) {
                binding.get().tilEmail.setError(getString(R.string.error_email_can_not_be_null));
            } else if (password.isEmpty()) {
                binding.get().tilPassword.setError(getString(R.string.error_password_can_not_be_null));
            } else {
                viewModel.login(new UserDTO(email, password, false));
            }
        });
        binding.get().btGoogleLogin.setOnClickListener(v ->
                googleSignIn.launch(googleSignInClient.getSignInIntent()));

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setUser(viewModel.user);
    }

    private void initGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
        if (getContext() == null) return;
        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

    }

    private void initActivityResult() {
        googleSignIn = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    Task<GoogleSignInAccount> task =
                            GoogleSignIn.getSignedInAccountFromIntent(activityResult.getData());
                    handleSignInResult(task);
                }
        );
    }

    private void initData() {
        EditText etEmail = binding.get().tilEmail.getEditText();
        if (!viewModel.getUserEmail().isEmpty() && etEmail != null)
            etEmail.setText(viewModel.getUserEmail());

        viewModel.user.observe(getViewLifecycleOwner(), userResource -> {
            if (userResource.getStatus().equals(Status.SUCCESS)) {
                showToast(R.string.success_login);
                viewModel.loggedIn(userResource.getData().getId(), userResource.getData().getEmail());
                navController.navigateUp();
                googleSignInClient.signOut();
            } else if (userResource.getStatus().equals(Status.ERROR)) {
                showToast(userResource.getMessage());
                googleSignInClient.signOut();
            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Bind google account
            viewModel.googleLogin(account.getId());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showToast("Sign In Result: Failed code = " + e.getStatusCode());
        }
    }
}