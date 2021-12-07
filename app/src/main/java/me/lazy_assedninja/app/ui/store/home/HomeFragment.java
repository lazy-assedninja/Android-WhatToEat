package me.lazy_assedninja.app.ui.store.home;

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
import androidx.navigation.fragment.FragmentNavigator;

import javax.inject.Inject;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.binding.ImageDataBindingComponent;
import me.lazy_assedninja.app.databinding.HomeFragmentBinding;
import me.lazy_assedninja.app.databinding.StoreItemBinding;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.ui.store.StoreAdapter;
import me.lazy_assedninja.app.ui.store.StoreCallback;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.app.vo.Tag;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.util.ExecutorUtil;

import static java.util.Collections.emptyList;

@AndroidEntryPoint
public class HomeFragment extends BaseFragment {

    private HomeFragmentBinding binding;
    private HomeViewModel viewModel;

    @Inject
    public ExecutorUtil executorUtil;

    private NavController navController;
    private StoreAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.home_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initSwipeRefreshLayout();
        initData();
    }

    private void initView() {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        adapter = new StoreAdapter(
                executorUtil,
                dataBindingComponent,
                new StoreCallback() {
                    @Override
                    public void onFavoriteClick(int storeID, boolean isFavorite) {
                        if (viewModel.isLoggedIn()) {
                            showToast(R.string.error_please_login_first);
                            return;
                        }

                        viewModel.changeFavoriteStatus(new Favorite(storeID, isFavorite));
                    }

                    @Override
                    public void onInformationClick(StoreItemBinding binding) {
                        int storeID = binding.getStore().getId();
                        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                                .addSharedElement(binding.ivPicture, String.valueOf(storeID))
                                .build();
                        navController.navigate(HomeFragmentDirections
                                .actionToStoreInformationFragment(storeID), extras);
                    }
                });
        binding.rv.setAdapter(adapter);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setStores(viewModel.stores);
        binding.setResult(viewModel.result);
    }

    private void initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());
    }

    private void initData() {
        viewModel.requestStore(new StoreDTO(Tag.HOME.getValue()));

        viewModel.stores.observe(getViewLifecycleOwner(), listResource -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (listResource.getData() != null) {
                adapter.submitList(listResource.getData());
            } else {
                adapter.submitList(emptyList());
            }
        });
        viewModel.result.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}