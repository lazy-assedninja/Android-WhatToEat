package me.lazy_assedninja.app.ui.password.reset_password;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.FragmentResetPasswordBinding;

public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;
    private ResetPasswordViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reset_password,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        // TODO: Use the ViewModel
    }
}