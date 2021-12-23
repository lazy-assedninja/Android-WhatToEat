package me.lazy_assedninja.what_to_eat.ui.promotion;

import static java.util.Collections.emptyList;

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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.PromotionFragmentBinding;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.util.ExecutorUtil;

@AndroidEntryPoint
public class PromotionFragment extends BaseFragment {

    @Inject
    public ExecutorUtil executorUtil;

    private AutoClearedValue<PromotionFragmentBinding> binding;
    private PromotionViewModel viewModel;

    private NavController navController;
    private AutoClearedValue<PromotionAdapter> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        PromotionFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.promotion_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PromotionViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initData();
    }

    private void initView() {
        PromotionAdapter adapter = new PromotionAdapter(executorUtil, (id) ->
                navController.navigate(PromotionFragmentDirections
                        .actionToPromotionInformationFragment(id))
        );
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().rv.setAdapter(adapter);

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setPromotions(viewModel.promotions);
    }

    private void initData() {
        viewModel.requestPromotion();

        viewModel.promotions.observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.getData() != null) {
                adapter.get().submitList(listResource.getData());
            } else {
                adapter.get().submitList(emptyList());
            }
        });
    }
}