package me.lazy_assedninja.app.ui.store.history;

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
import me.lazy_assedninja.app.databinding.HistoryFragmentBinding;
import me.lazy_assedninja.app.databinding.StoreItemBinding;
import me.lazy_assedninja.app.ui.store.StoreAdapter;
import me.lazy_assedninja.app.ui.store.StoreCallback;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.util.ExecutorUtil;

import static java.util.Collections.emptyList;

@AndroidEntryPoint
public class HistoryFragment extends BaseFragment {

    private HistoryFragmentBinding binding;
    private HistoryViewModel viewModel;

    @Inject
    public ExecutorUtil executorUtil;

    private NavController navController;
    private StoreAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.history_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
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
                        navController.navigate(HistoryFragmentDirections
                                .actionToStoreInformationFragment(storeID), extras);
                    }
                });
        binding.rv.setAdapter(adapter);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResult(viewModel.result);
    }

    private void initData() {
        viewModel.requestHistory();

        viewModel.stores.observe(getViewLifecycleOwner(), listResource -> {
            if (listResource != null) {
                adapter.submitList(listResource);
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