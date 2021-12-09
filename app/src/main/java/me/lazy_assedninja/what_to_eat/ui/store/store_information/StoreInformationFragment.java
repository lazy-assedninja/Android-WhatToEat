package me.lazy_assedninja.what_to_eat.ui.store.store_information;

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
import androidx.transition.TransitionInflater;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.TimeUnit;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.binding.ImageDataBindingComponent;
import me.lazy_assedninja.what_to_eat.databinding.StoreInformationFragmentBinding;
import me.lazy_assedninja.what_to_eat.ui.store.comment.CommentFragment;
import me.lazy_assedninja.what_to_eat.ui.store.post.PostFragment;
import me.lazy_assedninja.what_to_eat.ui.store.reservation.reserve.ReserveFragment;
import me.lazy_assedninja.what_to_eat.ui.user.create_report.CreateReportFragment;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.what_to_eat.vo.Favorite;
import me.lazy_assedninja.what_to_eat.vo.History;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Status;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class StoreInformationFragment extends BaseFragment {

    private AutoClearedValue<StoreInformationFragmentBinding> binding;
    private StoreInformationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        StoreInformationFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.store_information_fragment,
                container,
                false,
                dataBindingComponent
        );
        this.binding = new AutoClearedValue<>(this, binding);

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_image_transform));
        binding.setImageRequestListener(new RequestListener<>() {
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

        initView();
        initData();
    }

    private void initView() {
        binding.get().floatingActionButton.setOnClickListener(v -> {
            if (viewModel.isLoggedIn()) {
                showToast(R.string.error_please_login_first);
                return;
            }
            viewModel.changeFavoriteStatus(new Favorite());
        });
        binding.get().ivComment.setOnClickListener(v -> {
            CommentFragment commentFragment = new CommentFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("store_id", viewModel.getId());
            commentFragment.setArguments(bundle);
            commentFragment.show(getParentFragmentManager(), "comment");
        });
        binding.get().ivPost.setOnClickListener(v -> {
            PostFragment postFragment = new PostFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("store_id", viewModel.getId());
            postFragment.setArguments(bundle);
            postFragment.show(getParentFragmentManager(), "post");
        });
        binding.get().btReserve.setOnClickListener(v -> {
            if (viewModel.isLoggedIn()) {
                showToast(R.string.error_please_login_first);
                return;
            }

            ReserveFragment reserveFragment = new ReserveFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("store_id", viewModel.getId());
            reserveFragment.setArguments(bundle);
            reserveFragment.show(getParentFragmentManager(), "reserve");
        });
        binding.get().btAddReport.setOnClickListener(v -> {
            if (viewModel.isLoggedIn()) {
                showToast(R.string.error_please_login_first);
                return;
            }

            CreateReportFragment createReportFragment = new CreateReportFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_store", true);
            bundle.putInt("id", viewModel.getId());
            createReportFragment.setArguments(bundle);
            createReportFragment.show(getParentFragmentManager(), "create_report");
        });

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setResult(viewModel.result);
    }

    private void initData() {
        int id = StoreInformationFragmentArgs.fromBundle(getArguments()).getStoreID();
        binding.get().setStore(viewModel.getStore(id));
        viewModel.setId(id);
        viewModel.addToHistory(new History(id));

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