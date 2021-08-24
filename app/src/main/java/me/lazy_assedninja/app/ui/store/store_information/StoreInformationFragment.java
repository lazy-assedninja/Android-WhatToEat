package me.lazy_assedninja.app.ui.store.store_information;

import android.graphics.drawable.Drawable;
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
import androidx.transition.TransitionInflater;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.TimeUnit;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.FragmentStoreInformationBinding;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.utils.LogUtils;

public class StoreInformationFragment extends BaseFragment {

    private FragmentStoreInformationBinding binding;
    private StoreInformationViewModel viewModel;

    private NavController navController;

    private int id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_store_information,
                container,
                false
        );

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_image_transform));
        binding.setImageRequestListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }
        });
        postponeEnterTransition(1, TimeUnit.SECONDS);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StoreInformationViewModel.class);
        navController = Navigation.findNavController(view);

        initView();
        initData();
    }

    private void initView() {
        binding.ivComment.setOnClickListener(v ->
                navController.navigate(StoreInformationFragmentDirections.actionToFragmentComment(id)));
        binding.ivPost.setOnClickListener(v ->
                navController.navigate(StoreInformationFragmentDirections.actionToFragmentPost(id)));
        binding.btReserve.setOnClickListener(v ->
                navController.navigate(StoreInformationFragmentDirections.actionToFragmentReserve(id)));
        binding.btAddReport.setOnClickListener(v ->
                navController.navigate(StoreInformationFragmentDirections.actionToFragmentAddReport(id)));
    }

    private void initData() {
        id = StoreInformationFragmentArgs.fromBundle(getArguments()).getStoreID();
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setStore(viewModel.getStore(id));
        binding.setFragment(this);

        viewModel.addHistory(id);
        viewModel.favorite.observe(getViewLifecycleOwner(), resultResource -> {
            if (resultResource.getStatus().equals(Resource.SUCCESS)) {
                showToast(resultResource.getData().getResult());
            } else if (resultResource.getStatus().equals(Resource.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }

    public void favoriteOnClick(Store store) {
        if (viewModel.getLoggedInUserID() == 0) {
            showToast(R.string.error_please_login_first);
            return;
        }
        store.changeFavoriteStatus();
        viewModel.setFavoriteRequest(store.getId(), store.isFavorite());
    }
}