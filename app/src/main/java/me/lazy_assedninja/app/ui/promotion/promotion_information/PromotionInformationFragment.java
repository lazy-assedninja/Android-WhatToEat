package me.lazy_assedninja.app.ui.promotion.promotion_information;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.FragmentPromotionInformationBinding;

public class PromotionInformationFragment extends Fragment {

    private FragmentPromotionInformationBinding binding;
    private PromotionInformationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_promotion_information,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PromotionInformationViewModel.class);

        initData();
    }

    private void initData() {
        int id = PromotionInformationFragmentArgs.fromBundle(getArguments()).getPromotionID();
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setPromotion(viewModel.getPromotion(id));
    }
}