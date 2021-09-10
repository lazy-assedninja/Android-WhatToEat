package me.lazy_assedninja.app.ui.store.reservation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.ReservationFragmentBinding;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.utils.ExecutorUtils;

import static java.util.Collections.emptyList;

@AndroidEntryPoint
public class ReservationFragment extends BaseFragment {

    private ReservationFragmentBinding binding;
    private ReservationViewModel viewModel;

    @Inject
    public ExecutorUtils executorUtils;

    private ReservationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.reservation_fragment,
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
        adapter = new ReservationAdapter(executorUtils, (reservation) ->
                viewModel.setCancelRequest(reservation));
        binding.rv.setAdapter(adapter);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setReservations(viewModel.reservations);
        binding.setResult(viewModel.result);
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
        viewModel.requestReservation();

        viewModel.reservations.observe(getViewLifecycleOwner(), list -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (list.getData() != null) {
                adapter.submitList(list.getData());
            } else {
                adapter.submitList(emptyList());
            }
        });
        viewModel.result.observe(getViewLifecycleOwner(), result -> {
            if (result.getStatus().equals(Resource.SUCCESS)) {
                showToast(result.getData().getResult());
            } else if (result.getStatus().equals(Resource.ERROR)) {
                showToast(result.getMessage());
            }
        });
    }
}