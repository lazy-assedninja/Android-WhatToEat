package me.lazy_assedninja.app.ui.profile;

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
import me.lazy_assedninja.app.databinding.FragmentProfileBinding;
import me.lazy_assedninja.library.ui.BaseFragment;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_profile,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initData();
    }

    private void initView() {
        binding.btResetPassword.setOnClickListener(v -> {
            navController.navigate(R.id.action_to_fragment_reset_password);
        });
        binding.btLogout.setOnClickListener(v -> {
            viewModel.logout();
            navController.navigate(R.id.action_to_fragment_home);
        });
    }

    private void initData() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setUser(viewModel.getUser());
    }
}