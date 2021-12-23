package me.lazy_assedninja.what_to_eat.ui.store.search;

import static java.util.Collections.emptyList;

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
import me.lazy_assedninja.what_to_eat.content_provider.SuggestionProvider;
import me.lazy_assedninja.what_to_eat.databinding.SearchFragmentBinding;
import me.lazy_assedninja.what_to_eat.databinding.StoreItemBinding;
import me.lazy_assedninja.what_to_eat.dto.StoreDTO;
import me.lazy_assedninja.what_to_eat.ui.store.StoreAdapter;
import me.lazy_assedninja.what_to_eat.ui.store.StoreCallback;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Status;
import me.lazy_assedninja.what_to_eat.vo.Store;

@AndroidEntryPoint
public class SearchFragment extends BaseFragment {

    private static final String ARGUMENT_POSITION = "position";
    private static final String ARGUMENT_IS_CHANGE = "is_change";

    @Inject
    public ExecutorUtil executorUtil;

    private AutoClearedValue<SearchFragmentBinding> binding;
    private SearchViewModel viewModel;

    private NavController navController;
    private SearchView searchView;
    private AutoClearedValue<StoreAdapter> adapter;

    private int position;
    private boolean isChange;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = -1;
        isChange = false;
        NavBackStackEntry navBackStackEntry = NavHostFragment.findNavController(this)
                .getCurrentBackStackEntry();
        if (navBackStackEntry != null) {
            SavedStateHandle savedStateHandle = navBackStackEntry.getSavedStateHandle();
            savedStateHandle.getLiveData(ARGUMENT_POSITION).observe(navBackStackEntry, position ->
                    this.position = (int) position);
            savedStateHandle.getLiveData(ARGUMENT_IS_CHANGE).observe(navBackStackEntry, isChange ->
                    this.isChange = (boolean) isChange);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SearchFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.search_fragment,
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
        StoreAdapter adapter = new StoreAdapter(executorUtil, dataBindingComponent,
                new StoreCallback() {
                    @Override
                    public void onFavoriteClick(int storeID, boolean isFavorite) {
                        if (viewModel.isLoggedIn()) {
                            showToast(R.string.error_please_login_first);
                            return;
                        }

                        viewModel.changeFavoriteStatus(new Favorite(storeID, !isFavorite,
                                false));
                    }

                    @Override
                    public void onInformationClick(StoreItemBinding binding, int position) {
                        int storeID = binding.getStore().getId();
                        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                                .addSharedElement(binding.ivPicture, String.valueOf(storeID))
                                .build();
                        navController.navigate(SearchFragmentDirections.actionToStoreInformationFragment(
                                storeID, position, true), extras);
                    }
                }, false);
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().rv.setAdapter(adapter);
        postponeEnterTransition();
        OneShotPreDrawListener.add(binding.get().rv, this::startPostponedEnterTransition);

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setStores(viewModel.stores);
        binding.get().setResult(viewModel.result);
    }

    private void initSearchView() {
        if (getActivity() == null) return;
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_to_search_fragment);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.search(new StoreDTO(query));

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
        binding.get().swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        binding.get().swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());
    }

    private void initData() {
        viewModel.stores.observe(getViewLifecycleOwner(), listResource -> {
            binding.get().swipeRefreshLayout.setRefreshing(false);

            List<Store> list = listResource.getData();
            if (list != null) {
                if (position != -1 && isChange) {
                    Store store = list.get(position);
                    boolean isFavorite = store.isFavorite();
                    store.setFavorite(!isFavorite);
                    isChange = false;
                }
                viewModel.setList(list);
                adapter.get().submitList(list);
            } else {
                adapter.get().submitList(emptyList());
            }
        });
        viewModel.result.observe(getViewLifecycleOwner(), event -> {
            Resource<Favorite> resultResource = event.getContentIfNotHandled();
            if (resultResource == null || resultResource.getData() == null) return;

            Favorite favorite = resultResource.getData();
            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(favorite.getResult());
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }

            if (!resultResource.getStatus().equals(Status.LOADING)) {
                int position = viewModel.getStorePosition(favorite.getStoreID());
                adapter.get().notifyItemChanged(position, favorite.getStatus());
            }
        });
    }
}