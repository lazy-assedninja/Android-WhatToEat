package me.lazy_assedninja.what_to_eat.ui.user.profile.head_portrait;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.PortraitOptionsFragmentBinding;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;

public class PortraitOptionsFragment extends BaseBottomSheetDialogFragment {

    private AutoClearedValue<PortraitOptionsFragmentBinding> binding;

    private final PortraitOptionsCallback callback;

    public PortraitOptionsFragment(PortraitOptionsCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        PortraitOptionsFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.portrait_options_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        binding.get().btTakePhotos.setOnClickListener(v -> {
            callback.takePicture();
            dismiss();
        });
        binding.get().btFromGallery.setOnClickListener(v -> {
            callback.getContent();
            dismiss();
        });
    }
}
