package me.lazy_assedninja.app.ui.store.store_information;

import android.graphics.drawable.Drawable;
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
import androidx.transition.TransitionInflater;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.TimeUnit;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.binding.ImageDataBindingComponent;
import me.lazy_assedninja.app.databinding.StoreInformationFragmentBinding;
import me.lazy_assedninja.app.ui.store.reservation.reserve.ReserveFragment;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class StoreInformationFragment extends BaseFragment {

    private StoreInformationFragmentBinding binding;
    private StoreInformationViewModel viewModel;

    private NavController navController;

    private int id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.store_information_fragment,
                container,
                false,
                dataBindingComponent
        );

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_image_transform));
        binding.setImageRequestListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
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
        binding.floatingActionButton.setOnClickListener(v -> {
            if (viewModel.isLoggedIn()) {
                showToast(R.string.error_please_login_first);
                return;
            }
            viewModel.setFavoriteRequest();
        });
        binding.ivComment.setOnClickListener(v ->
                navController.navigate(StoreInformationFragmentDirections.actionToCommentFragment(id)));
        binding.ivPost.setOnClickListener(v ->
                navController.navigate(StoreInformationFragmentDirections.actionToPostFragment(id)));
        binding.btReserve.setOnClickListener(v -> {
            ReserveFragment reserveFragment = new ReserveFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("store_id", id);
            reserveFragment.setArguments(bundle);
            reserveFragment.show(getParentFragmentManager(), "reserve");
        });
        binding.btAddReport.setOnClickListener(v -> {
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResult(viewModel.result);
    }

    private void initData() {
        id = StoreInformationFragmentArgs.fromBundle(getArguments()).getStoreID();
        binding.setStore(viewModel.getStore(id));

        // Add to history
        viewModel.addHistory(id);

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