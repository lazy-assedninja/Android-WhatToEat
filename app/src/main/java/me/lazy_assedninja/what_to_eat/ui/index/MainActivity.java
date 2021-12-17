package me.lazy_assedninja.what_to_eat.ui.index;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import javax.inject.Inject;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.binding.ImageDataBindingComponent;
import me.lazy_assedninja.what_to_eat.databinding.DrawerHeaderBinding;
import me.lazy_assedninja.what_to_eat.databinding.MainActivityBinding;
import me.lazy_assedninja.library.ui.BaseActivity;
import me.lazy_assedninja.library.util.DisplayUtil;

@AndroidEntryPoint
public class MainActivity extends BaseActivity {

    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private MainActivityBinding binding;
    private MainViewModel viewModel;

    @Inject
    public DisplayUtil displayUtil;

    private NavController navController;
    private SearchView searchView;

    private ActivityResultLauncher<String[]> requestPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initView();
        initSearchView();
        initNavigationUI();
        initDrawer();
        initDrawerHeader();
        initBottomNavigation();
        initActivityResult();
    }

    private void initView() {
        binding.toolbar.inflateMenu(R.menu.menu_index_toolbar);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemID = item.getItemId();
            if (itemID == R.id.action_to_register_fragment) {
                navController.navigate(R.id.action_to_register_fragment);
                return true;
            } else if (itemID == R.id.action_clear_history) {
                viewModel.clearHistory();
                return true;
            }
            return false;
        });
        binding.floatingActionButton.setOnClickListener(v -> {
            if (navController.getCurrentDestination() == null) return;
            if (navController.getCurrentDestination().getId() != R.id.home_fragment) {
                navController.navigate(R.id.action_to_home_fragment);
            }
        });
        binding.bottomNavigationView.setBackground(null);
    }

    private void initSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = binding.toolbar.getMenu().findItem(R.id.action_to_search_fragment);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(displayUtil.getScreenWidthPix());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSearchClickListener(v -> {
            if (navController.getCurrentDestination() == null) return;
            if (navController.getCurrentDestination().getId() != R.id.search_fragment) {
                navController.navigate(R.id.action_to_search_fragment);
            }
        });
        searchView.setOnCloseListener(() -> {
            navController.navigateUp();
            return false;
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String keyword = cursor.getString(
                        cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchView.setQuery(keyword, true);
                return true;
            }
        });
    }

    private void initNavigationUI() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destinationID = destination.getId();

                // Toolbar & BottomAppBar
                binding.appBarLayout.setExpanded(true);
                if (destinationID != R.id.home_fragment && destinationID != R.id.recommend_fragment &&
                        destinationID != R.id.profile_fragment) {
                    binding.appBarLayout.setBackground(
                            ContextCompat.getDrawable(this, R.color.white));
                    binding.bottomAppBar.setVisibility(View.GONE);
                    binding.floatingActionButton.setVisibility(View.GONE);
                } else {
                    binding.appBarLayout.setBackground(
                            ContextCompat.getDrawable(this, R.color.primary_color));
                    binding.bottomAppBar.setVisibility(View.VISIBLE);
                    binding.floatingActionButton.setVisibility(View.VISIBLE);
                }

                // SearchView
                if (destinationID != R.id.search_fragment) {
                    searchView.onActionViewCollapsed();
                    dismissKeyboard(binding.getRoot());
                }
                binding.toolbar.getMenu().findItem(R.id.action_to_search_fragment)
                        .setVisible(destinationID == R.id.home_fragment ||
                                destinationID == R.id.recommend_fragment ||
                                destinationID == R.id.search_fragment);

                // Register menu item
                binding.toolbar.getMenu().findItem(R.id.action_to_register_fragment)
                        .setVisible(destinationID == R.id.login_fragment);

                // Clear History menu item
                binding.toolbar.getMenu().findItem(R.id.action_clear_history)
                        .setVisible(destinationID == R.id.history_fragment);
            });
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                    .Builder(R.id.home_fragment, R.id.recommend_fragment, R.id.profile_fragment)
                    .setOpenableLayout(binding.drawer)
                    .build();
            NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        }
    }

    private void initDrawer() {
        binding.navigationView.getMenu().setGroupCheckable(0, false, false);
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemID = menuItem.getItemId();
            if (viewModel.isLoggedIn()) {
                navController.navigate(R.id.action_to_login_fragment);
            } else if (itemID == R.id.map_fragment) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    navController.navigate(R.id.action_to_map_fragment);
                } else {
                    requestPermissions.launch(PERMISSIONS);
                }
            } else if (itemID == R.id.promotion_fragment) {
                navController.navigate(R.id.action_to_promotion_fragment);
            } else if (itemID == R.id.favorite_fragment) {
                navController.navigate(R.id.action_to_favorite_fragment);
            } else if (itemID == R.id.history_fragment) {
                navController.navigate(R.id.action_to_history_fragment);
            } else if (itemID == R.id.reservation_fragment) {
                navController.navigate(R.id.action_to_reservation_fragment);
            }
            binding.drawer.close();
            return false;
        });
        binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void initDrawerHeader() {
        DataBindingComponent dataBindingComponent =
                EntryPoints.get(getApplicationContext(), ImageDataBindingComponent.class);
        DrawerHeaderBinding drawerHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.drawer_header, binding.navigationView, false, dataBindingComponent);
        drawerHeaderBinding.setLifecycleOwner(this);
        drawerHeaderBinding.setUser(viewModel.getUser());
        binding.navigationView.addHeaderView(drawerHeaderBinding.getRoot());
    }

    private void initBottomNavigation() {
        binding.bottomNavigationView.setOnItemReselectedListener(menuItem -> {
            // Prevent fragment recreating when user double click.
        });
        binding.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int itemID = menuItem.getItemId();
            if (itemID == R.id.profile_fragment) {
                if (viewModel.isLoggedIn()) {
                    navController.navigate(R.id.action_to_login_fragment);
                    return true;
                } else {
                    navController.navigate(R.id.action_to_profile_fragment);
                    return false;
                }
            } else if (itemID == R.id.recommend_fragment) {
                navController.navigate(R.id.action_to_recommend_fragment);
            }
            return true;
        });
    }

    private void initActivityResult() {
        requestPermissions = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                    if (permissions.containsValue(true)) {
                        navController.navigate(R.id.action_to_map_fragment);
                    }
                });
    }
}