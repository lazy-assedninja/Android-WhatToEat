package me.lazy_assedninja.what_to_eat.ui.store.favorite;

import static java.util.Collections.emptyList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OneShotPreDrawListener;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.TransitionInflater;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.binding.ImageDataBindingComponent;
import me.lazy_assedninja.what_to_eat.databinding.FavoriteFragmentBinding;
import me.lazy_assedninja.what_to_eat.databinding.StoreItemBinding;
import me.lazy_assedninja.what_to_eat.dto.FavoriteDTO;
import me.lazy_assedninja.what_to_eat.ui.store.StoreAdapter;
import me.lazy_assedninja.what_to_eat.ui.store.StoreCallback;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.RequestResult;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Status;
import me.lazy_assedninja.what_to_eat.vo.Store;

@AndroidEntryPoint
public class FavoriteFragment extends BaseFragment {

    private static final String ARGUMENT_IS_CHANGE = "is_change";

    private AutoClearedValue<FavoriteFragmentBinding> binding;
    private FavoriteViewModel viewModel;

    @Inject
    public ExecutorUtil executorUtil;

    private NavController navController;
    private AutoClearedValue<StoreAdapter> adapter;

    private boolean isChange;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isChange = false;
        NavBackStackEntry navBackStackEntry = NavHostFragment.findNavController(this)
                .getCurrentBackStackEntry();
        if (navBackStackEntry != null) {
            SavedStateHandle savedStateHandle = navBackStackEntry.getSavedStateHandle();
            savedStateHandle.getLiveData(ARGUMENT_IS_CHANGE).observe(navBackStackEntry, isChange ->
                    this.isChange = (boolean) isChange);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FavoriteFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.favorite_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.change_image_transform));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initSwipeRefreshLayout();
        initData();
    }

    private void initView() {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        StoreAdapter adapter = new StoreAdapter(executorUtil, dataBindingComponent,
                new StoreCallback() {
                    @Override
                    public void onFavoriteClick(int storeID, int position, boolean isFavorite) {
                        if (viewModel.isLoggedIn()) {
                            showToast(R.string.error_please_login_first);
                            return;
                        }

                        viewModel.changeFavoriteStatus(new Favorite(storeID, !isFavorite, position,
                                true));
                    }

                    @Override
                    public void onInformationClick(StoreItemBinding binding, int position) {
                        int storeID = binding.getStore().getId();
                        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                                .addSharedElement(binding.ivPicture, String.valueOf(storeID))
                                .build();
                        navController.navigate(FavoriteFragmentDirections
                                        .actionToStoreInformationFragment(storeID, position, true),
                                extras);
                    }
                }, false);
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().rv.setAdapter(adapter);
        if (!isChange) {
            postponeEnterTransition();
            OneShotPreDrawListener.add(binding.get().rv, this::startPostponedEnterTransition);
        }

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setStores(viewModel.stores);
        binding.get().setResult(viewModel.result);
    }

    private void initSwipeRefreshLayout() {
        binding.get().swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        binding.get().swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());
    }

    private void initData() {
        viewModel.requestStore(new FavoriteDTO());

        viewModel.stores.observe(getViewLifecycleOwner(), listResource -> {
            binding.get().swipeRefreshLayout.setRefreshing(false);

            List<Store> list = listResource.getData();
            if (list != null) {
                adapter.get().submitList(list);
            } else {
                adapter.get().submitList(emptyList());
            }
        });
        viewModel.result.observe(getViewLifecycleOwner(), event -> {
            Resource<RequestResult<Favorite>> resultResource = event.getContentIfNotHandled();
            if (resultResource == null || resultResource.getData() == null) return;

            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}