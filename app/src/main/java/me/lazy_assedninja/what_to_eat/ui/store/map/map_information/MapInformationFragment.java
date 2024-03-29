package me.lazy_assedninja.what_to_eat.ui.store.map.map_information;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.MapInformationFragmentBinding;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.what_to_eat.vo.Store;

@AndroidEntryPoint
public class MapInformationFragment extends BaseBottomSheetDialogFragment {

    private AutoClearedValue<MapInformationFragmentBinding> binding;
    private MapInformationViewModel viewModel;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MapInformationFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.map_information_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapInformationViewModel.class);

        initView();
        initData();
        initLocation();
    }

    @SuppressWarnings("MissingPermission")
    private void initView() {
        binding.get().btNavigation.setOnClickListener(v -> {
            if (getActivity() == null) return;
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                Store store = viewModel.getStore();
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Directions
                    Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" +
                            location.getLatitude() + "," + location.getLongitude() +
                            "&destination=" + store.getLatitude() + "," + store.getLongitude());
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            });
        });
        binding.get().btCall.setOnClickListener(v -> {
            Store store = viewModel.getStore();
            if (!store.getPhone().isEmpty()) {
                showToast(getString(R.string.toast_call) + store.getPhone());
            }
        });

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initData() {
        if (getArguments() == null) return;
        String storeName = getArguments().getString("store_name");
        viewModel.getStore(storeName).observe(getViewLifecycleOwner(), store -> {
            binding.get().setStore(store);
            viewModel.setStore(store);
        });
    }

    private void initLocation() {
        if (getActivity() == null) return;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }
}
