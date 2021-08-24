package me.lazy_assedninja.app.ui.reservation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.FragmentReservationBinding;
import me.lazy_assedninja.app.dto.PromotionRequest;
import me.lazy_assedninja.app.dto.ReservationRequest;
import me.lazy_assedninja.app.ui.promotion.PromotionAdapter;
import me.lazy_assedninja.app.ui.promotion.PromotionFragmentDirections;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.utils.ExecutorUtils;

import static java.util.Collections.emptyList;

public class ReservationFragment extends BaseFragment {

    private FragmentReservationBinding binding;
    private ReservationViewModel viewModel;

    private ReservationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reservation,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        initView();
        initSwipeRefreshLayout();
        initData();
    }

    private void initView() {
        adapter = new ReservationAdapter(
                new ExecutorUtils(),
                (reservation) -> {
                    viewModel.cancelReservation(reservation);
                    viewModel.setReservationRequest(new ReservationRequest("user", viewModel.getLoggedInUserID()));
                }
        );
        binding.rv.setAdapter(adapter);
    }

    private void initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener(() ->
                viewModel.setReservationRequest(new ReservationRequest("user", viewModel.getLoggedInUserID())));
        binding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
    }

    private void initData() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResource(viewModel.reservations);
        viewModel.setReservationRequest(new ReservationRequest("user", viewModel.getLoggedInUserID()));
        viewModel.reservations.observe(getViewLifecycleOwner(), list -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (list.getData() != null) {
                adapter.submitList(list.getData());
            } else {
                adapter.submitList(emptyList());
            }
        });
    }
}