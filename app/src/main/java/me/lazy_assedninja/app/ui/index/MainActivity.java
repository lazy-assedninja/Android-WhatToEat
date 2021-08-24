package me.lazy_assedninja.app.ui.index;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.lang.reflect.Field;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.ActivityMainBinding;
import me.lazy_assedninja.app.databinding.NavigationHeaderBinding;
import me.lazy_assedninja.library.ui.BaseActivity;
import me.lazy_assedninja.library.utils.DisplayUtils;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private NavController navController;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initView();
        initSearchView();
        initNavigationUI();
        initDrawer();
        initData();
    }

    private void initView() {
        binding.toolbar.inflateMenu(R.menu.menu_index_toolbar);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemID = item.getItemId();
            if (itemID == R.id.action_to_fragment_register) {
                navController.navigate(R.id.action_to_fragment_register);
                return true;
            } else if (itemID == R.id.action_clear_history) {
                viewModel.clearHistory();

                return true;
            }
            return false;
        });
        binding.floatingActionButton.setOnClickListener(v -> {
            if (navController.getCurrentDestination() != null) {
                if (navController.getCurrentDestination().getId() == R.id.fragment_home) return;
                navController.navigate(R.id.action_to_fragment_home);
            }
        });
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.getMenu().getItem(1).setEnabled(false);
    }

    private void initSearchView() {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = binding.toolbar.getMenu().findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(DisplayUtils.getScreenWidthPix(this));
        searchView.setOnSearchClickListener(v -> {
            // Pass keyword extra to search fragment.
            navController.navigate(R.id.action_to_fragment_search);
        });
        searchView.setOnCloseListener(() -> {
            navController.navigateUp();
            return false;
        });

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
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
                if (destinationID != R.id.fragment_home &&
                        destinationID != R.id.fragment_recommend &&
                        destinationID != R.id.fragment_profile) {
                    binding.appBarLayout.setBackground(ContextCompat.getDrawable(this, R.color.white));
                    binding.bottomAppBar.setVisibility(View.GONE);
                    binding.floatingActionButton.setVisibility(View.GONE);
                } else {
                    binding.appBarLayout.setBackground(ContextCompat.getDrawable(this, R.color.primary_color));
                    binding.bottomAppBar.setVisibility(View.VISIBLE);
                    binding.floatingActionButton.setVisibility(View.VISIBLE);
                }

                // SearchView
                if (destinationID != R.id.fragment_search) {
                    searchView.onActionViewCollapsed();
                    dismissKeyboard(binding.getRoot());
                }
                binding.toolbar.getMenu().findItem(R.id.action_search).setVisible(
                        destinationID == R.id.fragment_home || destinationID == R.id.fragment_recommend || destinationID == R.id.fragment_search);

                // Register menu item
                binding.toolbar.getMenu().findItem(R.id.action_to_fragment_register).setVisible(destinationID == R.id.fragment_login);

                // Clear History menu item
                binding.toolbar.getMenu().findItem(R.id.action_clear_history).setVisible(destinationID == R.id.fragment_history);
            });
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                    .Builder(R.id.fragment_home, R.id.fragment_recommend, R.id.fragment_profile)
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
            if (viewModel.getLoggedInUserID() == 0) {
                navController.navigate(R.id.action_to_fragment_login);
            } else if (itemID == R.id.fragment_promotion) {
                navController.navigate(R.id.action_to_fragment_promotion);
            } else if (itemID == R.id.fragment_favorite) {
                navController.navigate(R.id.action_to_fragment_favorite);
            } else if (itemID == R.id.fragment_history) {
                navController.navigate(R.id.action_to_fragment_history);
            } else if (itemID == R.id.fragment_reservation) {
                navController.navigate(R.id.action_to_fragment_reservation);
            }
            binding.drawer.close();
            return false;
        });
        binding.bottomNavigationView.setOnNavigationItemReselectedListener(menuItem -> {
            // Prevent fragment recreating when user double click.
        });
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int itemID = menuItem.getItemId();
            if (itemID == R.id.fragment_profile) {
                if (viewModel.getLoggedInUserID() == 0) {
                    navController.navigate(R.id.action_to_fragment_login);
                    return true;
                } else {
                    navController.navigate(R.id.action_to_fragment_profile);
                    return false;
                }
            } else if (itemID == R.id.fragment_recommend) {
                navController.navigate(R.id.action_to_fragment_recommend);
            }
            return true;
        });
    }

    private void initData() {
        NavigationHeaderBinding headerBinding =
                NavigationHeaderBinding.bind(binding.navigationView.getHeaderView(0));
        headerBinding.setLifecycleOwner(this);
        headerBinding.setUser(viewModel.getUser());
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }
}