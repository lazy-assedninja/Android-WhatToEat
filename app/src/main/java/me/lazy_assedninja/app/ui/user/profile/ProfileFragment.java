package me.lazy_assedninja.app.ui.user.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.binding.ImageDataBindingComponent;
import me.lazy_assedninja.app.databinding.ProfileFragmentBinding;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class ProfileFragment extends BaseFragment {

    private ProfileFragmentBinding binding;
    private ProfileViewModel viewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.profile_fragment,
                container,
                false,
                dataBindingComponent
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
        binding.btResetPassword.setOnClickListener(v ->
                navController.navigate(R.id.action_to_reset_password_fragment));
        binding.btLogout.setOnClickListener(v -> {
            viewModel.logout();
            navController.navigate(R.id.action_to_home_fragment);
        });
        binding.btReport.setOnClickListener(v -> {
            navController.navigate(R.id.action_to_report_fragment);
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initData() {
        binding.setUser(viewModel.getUser());
    }
}