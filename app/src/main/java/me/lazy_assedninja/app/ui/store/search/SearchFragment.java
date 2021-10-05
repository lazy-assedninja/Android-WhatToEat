package me.lazy_assedninja.app.ui.store.search;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import me.lazy_assedninja.app.content_provider.SuggestionProvider;
import me.lazy_assedninja.app.databinding.SearchFragmentBinding;
import me.lazy_assedninja.app.databinding.StoreItemBinding;
import me.lazy_assedninja.app.ui.store.StoreAdapter;
import me.lazy_assedninja.app.ui.store.StoreCallback;
import me.lazy_assedninja.app.ui.store.home.HomeFragmentDirections;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.utils.ExecutorUtils;

import static java.util.Collections.emptyList;

@AndroidEntryPoint
public class SearchFragment extends BaseFragment {

    private SearchFragmentBinding binding;
    private SearchViewModel viewModel;

    @Inject
    public ExecutorUtils executorUtils;

    private NavController navController;
    private SearchView searchView;
    private StoreAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.search_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initSearchView();
        initSwipeRefreshLayout();
        initData();
    }

    private void initView() {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        adapter = new StoreAdapter(
                executorUtils,
                dataBindingComponent,
                new StoreCallback() {
                    @Override
                    public void onFavoriteClick(int storeID, boolean isFavorite) {
                        if (viewModel.isLoggedIn()) {
                            showToast(R.string.error_please_login_first);
                            return;
                        }

                        viewModel.setFavoriteRequest(storeID, isFavorite);
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

    private void initSearchView() {
        if (getActivity() == null) return;
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_to_search_fragment);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setStoreRequest(query);

                // Save to SuggestionProvider
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                        SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
                suggestions.saveRecentQuery(query, null);

                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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

            if (resultResource.getStatus().equals(Resource.SUCCESS)) {
                showToast(resultResource.getData().getResult());
            } else if (resultResource.getStatus().equals(Resource.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}