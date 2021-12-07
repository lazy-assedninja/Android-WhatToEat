package me.lazy_assedninja.app.ui.store.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.MapFragmentBinding;
import me.lazy_assedninja.app.dto.StoreDTO;
import me.lazy_assedninja.app.ui.store.map.map_information.MapInformationFragment;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.ui.BaseFragment;

@AndroidEntryPoint
public class MapFragment extends BaseFragment {

    private MapFragmentBinding binding;
    private MapViewModel viewModel;

    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.map_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        initView();
        initMapFragment();
        initData();
    }

    private void initView() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setStores(viewModel.stores);
    }

    @SuppressWarnings("MissingPermission")
    private void initMapFragment() {
        if (getActivity() == null) return;
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(map -> {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(marker -> {
                MapInformationFragment mapInformationFragment = new MapInformationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("store_name", marker.getTitle());
                mapInformationFragment.setArguments(bundle);
                mapInformationFragment.show(getParentFragmentManager(), "map_information");
                return true;
            });

            viewModel.requestStore(new StoreDTO());

            // Move camera
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(25.042115050892985, 121.5256179979202))
                    .zoom(16)
                    .build()));
        });
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.support_map_fragment, mapFragment)
                .commit();
    }

    private void initData() {
        viewModel.stores.observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.getData() != null) {
                for (Store store : listResource.getData()) {
                    createMarker(store.getName(), Double.parseDouble(store.getLatitude()),
                            Double.parseDouble(store.getLongitude()));
                }
            }
        });
    }

    private void createMarker(String storeName, double latitude, double longitude) {
        googleMap.addMarker(new MarkerOptions()
                .title(storeName)
                .position(new LatLng(latitude, longitude)));
    }
}