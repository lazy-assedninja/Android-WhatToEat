package me.lazy_assedninja.app.ui.promotion;

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
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.PromotionFragmentBinding;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.utils.ExecutorUtils;

import static java.util.Collections.emptyList;

@AndroidEntryPoint
public class PromotionFragment extends BaseFragment {

    private PromotionFragmentBinding binding;
    private PromotionViewModel viewModel;

    @Inject
    public ExecutorUtils executorUtils;

    private NavController navController;
    private PromotionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.promotion_fragment,
                container,
                false
        );
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
        adapter = new PromotionAdapter(executorUtils, (id) ->
                navController.navigate(PromotionFragmentDirections.actionToPromotionInformationFragment(id))
        );
        binding.rv.setAdapter(adapter);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setPromotions(viewModel.promotions);
    }

    private void initData() {
        viewModel.requestPromotion();

        viewModel.promotions.observe(getViewLifecycleOwner(), list -> {
            if (list.getData() != null) {
                adapter.submitList(list.getData());
            } else {
                adapter.submitList(emptyList());
            }
        });
    }
}