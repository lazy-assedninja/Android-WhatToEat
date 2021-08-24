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

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.FragmentPromotionBinding;
import me.lazy_assedninja.app.dto.PromotionRequest;
import me.lazy_assedninja.app.dto.StoreRequest;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.utils.ExecutorUtils;
import me.lazy_assedninja.library.utils.LogUtils;

import static java.util.Collections.emptyList;

public class PromotionFragment extends BaseFragment {

    private FragmentPromotionBinding binding;
    private PromotionViewModel viewModel;

    private NavController navController;
    private PromotionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_promotion,
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
        initSwipeRefreshLayout();
        initData();
    }

    private void initView() {
        adapter = new PromotionAdapter(
                new ExecutorUtils(),
                (id) -> navController.navigate(PromotionFragmentDirections.actionToFragmentPromotionInformation(id))
        );
        binding.rv.setAdapter(adapter);
    }

    private void initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener(() ->
                viewModel.setPromotionRequest(new PromotionRequest()));
        binding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
    }

    private void initData() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResource(viewModel.promotion);
        viewModel.setPromotionRequest(new PromotionRequest());
        viewModel.promotion.observe(getViewLifecycleOwner(), list -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (list.getData() != null) {
                adapter.submitList(list.getData());
            } else {
                adapter.submitList(emptyList());
            }
        });
    }
}